package org.kotlin.multiplatform.newsapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import io.kamel.core.config.KamelConfig
import io.kamel.image.config.resourcesFetcher
import org.kotlin.multiplatform.newsapp.camera.createCameraManager
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionsManager
import org.kotlin.multiplatform.newsapp.imagepicker.createPermissionsManager

class MainActivity : ComponentActivity() {

    private lateinit var permissionsManager: PermissionsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        permissionsManager = createPermissionsManager(this)
        val cameraManager = createCameraManager(this)

        setContent {
            val context: Context = LocalContext.current
            FileKit.init(this)
            val androidConfig = KamelConfig {
                // Available only on Android.
                resourcesFetcher(context)
            }
            App(cameraManager,permissionsManager)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    App(cameraManager, permissionsManager)
}