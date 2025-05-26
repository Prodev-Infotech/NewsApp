package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_user_profile_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userState by userViewModel.getUserState
    var showPickerDialog by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }
    var userProfile by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        userViewModel.getUserById(SessionUtil.getUserId().toString())
    }
    var requestCameraPermission by remember { mutableStateOf(false) }
    var requestGalleryPermission by remember { mutableStateOf(false) }
//    val permissionsManager = createPermissionsManager(object : PermissionCallback {
//        override fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus) {
//            println("Permission result for $permissionType: $status")
//            if (permissionType == PermissionType.CAMERA && status == PermissionStatus.GRANTED) {
//                println("Launching camera...")
////                cameraManager.launch()
//            }
//            if (permissionType == PermissionType.GALLERY && status == PermissionStatus.GRANTED) {
//                println("Launching gallery picker...")
////                galleryManager.launch()
//            }
//        }
//    })
    when(userState){
        is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ResultState.Success -> {
            userName = (userState as ResultState.Success).data.name
            userProfile = (userState as ResultState.Success).data.profileImage.toString()
        }

        is ResultState.Error -> {
            val error = (userState as ResultState.Error)
            println("Error:->$error")
        }

        else -> {}
    }
    val imageResource = asyncPainterResource(userProfile)
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Profile Image
        Box(
            modifier = Modifier
                .background(Color.White)
                .clickable { /* Optional: open preview */ },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { /* Optional: open preview */ },
                contentAlignment = Alignment.Center
            ) {
//                val platformImage = image?.toPlatformImage()
//                if (platformImage is Painter) {
//                    Image(
//                        painter = platformImage,
//                        contentDescription = "Selected Image",
//                        modifier = Modifier.size(100.dp).clip(CircleShape),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
                    if (imageResource is Resource.Loading || imageResource is Resource.Failure) {
                        Image(
                            painter = painterResource(Res.drawable.ic_user_profile_pl),
                            contentDescription = "Loading placeholder",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    KamelImage({ imageResource },
                        contentDescription = "contentDescription",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            println("Loading image...")
                        }, onFailure = {
                            println("Failed to load image")
                        })
//                }


            }
            // Edit Icon (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
                    .clickable { /*onEditClick()*/ },
                contentAlignment = Alignment.Center
            ) {
//                Icon(
//                    imageVector = Icons.Default.Edit,
//                    contentDescription = "Edit Icon",
//                    tint = Color.Black,
//                    modifier = Modifier.size(18.dp)
//                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

//        OutlinedTextField(
//            value = user.email,
//            onValueChange = {},
//            label = { Text("Email") },
//            enabled = false,
//            modifier = Modifier.fillMaxWidth()
//        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                showPickerDialog = true
//                val updated = EditProfileRequest(name = userName, profileImageUrl =userProfile)
//                userViewModel.editProfile(updated)
//                onSave()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }


        if (showPickerDialog) {
//            AlertDialog(
//                onDismissRequest = { showPickerDialog = false },
//                title = { Text("Select Image") },
//                text = {
//                    Column {
//                        Text(
//                            "Take from Camera",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    showPickerDialog = false
//                                    requestCameraPermission = true
//                                }
//                                .padding(8.dp)
//                        )
//                        HorizontalDivider()
//                        Text(
//                            "Choose from Gallery",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    showPickerDialog = false
//                                    requestGalleryPermission = true
//                                }
//                                .padding(8.dp)
//                        )
//                    }
//                },
//                confirmButton = {},
//                dismissButton = {}
//            )
//            permissionsManager.isPermissionGranted(PermissionType.CAMERA)

        }
//        when (val state = viewModel.updateProfileState.value) {
//            is ResultState.Loading -> CircularProgressIndicator()
//            is ResultState.Success -> Text("Profile updated!", color = Color.Green)
//            is ResultState.Error -> Text(state.message ?: "Error", color = Color.Red)
//            else -> {}
//        }
    }


}
