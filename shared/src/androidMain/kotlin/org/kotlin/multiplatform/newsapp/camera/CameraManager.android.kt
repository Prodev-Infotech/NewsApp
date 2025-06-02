package org.kotlin.multiplatform.newsapp.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

actual class CameraManager(private val activity: ComponentActivity) {

    private var currentPhotoPath: String = ""
    private var pendingImageUri: Uri? = null

    // Pre-register launchers during initialization
    private val takePictureLauncher = activity.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        takePictureCallback?.invoke(success)
    }

    private val pickImageLauncher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        pickImageCallback?.invoke(uri)
    }

    private val imageChooserLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        imageChooserCallback?.invoke(result)
    }

    private var takePictureCallback: ((Boolean) -> Unit)? = null
    private var pickImageCallback: ((Uri?) -> Unit)? = null
    private var imageChooserCallback: ((androidx.activity.result.ActivityResult) -> Unit)? = null

    actual suspend fun takePicture(): CameraResult = suspendCancellableCoroutine { continuation ->
        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath

            val photoURI = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                photoFile
            )

            pendingImageUri = photoURI

            takePictureCallback = { success ->
                if (success && pendingImageUri != null) {
                    continuation.resume(CameraResult(imageUri = currentPhotoPath))
                } else {
                    continuation.resume(CameraResult(isCancelled = true))
                }
            }

            takePictureLauncher.launch(photoURI)

        } catch (e: Exception) {
            continuation.resume(CameraResult(error = e.message))
        }
    }

    actual suspend fun pickFromGallery(): CameraResult = suspendCancellableCoroutine { continuation ->
        pickImageCallback = { uri ->
            if (uri != null) {
                // Copy the image to app's private directory
                val copiedUri = copyImageToPrivateStorage(uri)
                continuation.resume(CameraResult(imageUri = copiedUri))
            } else {
                continuation.resume(CameraResult(isCancelled = true))
            }
        }

        pickImageLauncher.launch("image/*")
    }

    actual suspend fun showImagePicker(): CameraResult = suspendCancellableCoroutine { continuation ->
        // Create intents
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        // Create photo file for camera
        val photoFile = try {
            createImageFile()
        } catch (e: Exception) {
            continuation.resume(CameraResult(error = e.message))
            return@suspendCancellableCoroutine
        }

        currentPhotoPath = photoFile.absolutePath
        val photoURI = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            photoFile
        )

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        // Create chooser
        val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        imageChooserCallback = { result ->
            when {
                result.resultCode == Activity.RESULT_OK -> {
                    val data = result.data
                    if (data?.data != null) {
                        // Image from gallery
                        val copiedUri = copyImageToPrivateStorage(data.data!!)
                        continuation.resume(CameraResult(imageUri = copiedUri))
                    } else {
                        // Image from camera
                        continuation.resume(CameraResult(imageUri = currentPhotoPath))
                    }
                }
                else -> {
                    continuation.resume(CameraResult(isCancelled = true))
                }
            }
        }

        imageChooserLauncher.launch(chooserIntent)
    }

    actual fun isAvailable(): Boolean {
        return activity.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun copyImageToPrivateStorage(sourceUri: Uri): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timeStamp}.jpg"
        val destinationFile = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

        try {
            activity.contentResolver.openInputStream(sourceUri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return destinationFile.absolutePath
    }
}

actual fun createCameraManager(): CameraManager {
    throw IllegalStateException("Use createCameraManager(activity: ComponentActivity) instead")
}

fun createCameraManager(activity: ComponentActivity): CameraManager {
    return CameraManager(activity)
}