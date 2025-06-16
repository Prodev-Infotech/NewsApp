package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_backarrow
import newskotlinproject.composeapp.generated.resources.ic_edit
import newskotlinproject.composeapp.generated.resources.ic_user_profile_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.camera.CameraManager
import org.kotlin.multiplatform.newsapp.camera.getImageDetailsFromUri
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionsManager
import org.kotlin.multiplatform.newsapp.model.EditProfileRequest
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.UserResponseData
import org.kotlin.multiplatform.newsapp.utils.ImagePickerManager
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    viewmodel: CommunityViewModel,
    cameraManager: CameraManager,
    permissionsManager: PermissionsManager,
) {
    val userState by userViewModel.getUserState

    var user by remember { mutableStateOf(UserResponseData("", "", "", "")) }
    var isSelectPhoto by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        userViewModel.getUserById(SessionUtil.getUserId().toString())
    }
    val coroutineScope = rememberCoroutineScope()

    when (userState) {
        is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            user = (userState as ResultState.Success).data
        }

        is ResultState.Error -> {
            val error = (userState as ResultState.Error)
            println("Error:->$error")
        }

        else -> {}
    }
    var properUri by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val imagePickerManager = remember {
        ImagePickerManager(cameraManager, permissionsManager)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF757575)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_backarrow),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Profile Image
            Box(
                modifier = Modifier
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable {
                            isSelectPhoto = true
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (selectedImageUri != null) {
                        selectedImageUri?.let { file ->
                            println("Selected file:-$file")// Convert file path to proper URI format
                            properUri = if (file.startsWith("/")) {
                                "file://$file"
                            } else {
                                file
                            }
                            properUri?.let { uri ->
                                val imageResource = asyncPainterResource(uri)

                                KamelImage(
                                    resource = { imageResource },
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    onLoading = { println("Loading...") },
                                    onFailure = { println("Failed to load image: ${it.message}") }
                                )
                            }


                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        // Error message
                        errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                        }


                        // Loading indicator
                        if (isLoading) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                    } else {
                        val imageResource = user.profileImage?.let { asyncPainterResource(it) }

                        if (imageResource == null || imageResource is Resource.Loading || imageResource is Resource.Failure) {
                            Image(
                                painter = painterResource(Res.drawable.ic_user_profile_pl),
                                contentDescription = "Loading placeholder",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            KamelImage(
                                resource = { imageResource },
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                onLoading = {
                                    println("Loading image...")
                                },
                                onFailure = {
                                    println("Failed to load image")
                                }
                            )
                        }
                    }


                }
                // Edit Icon (Bottom Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                        .clickable {
                            isSelectPhoto = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = "Edit Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = user.name,
                onValueChange = { user.name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 15.dp)
            )


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val uriString = properUri.toString()
                    properUri?.let { uri ->
                        coroutineScope.launch {
                            val (name, bytes) = getImageDetailsFromUri(uriString)
                            println("Name: $name")
                            println("Size: ${bytes?.size}")
                            if (bytes != null) {
                                if (name != null) {
                                    viewmodel.uploadImage(bytes, name)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text("Save")
            }
            when (val uploadState = viewmodel.uploadState.value) {
                is ResultState.Loading -> {
                    println("Upload Loading...")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ResultState.Success -> {
                    println("Upload successful: ${uploadState.data}")
                    val imageName = (uploadState as? ResultState.Success)?.data
                    LaunchedEffect(imageName) {
                        if (imageName != null) {
                            println("Triggering download for: $imageName")
                            viewmodel.downloadImage(imageName)
                        }
                    }
                }

                is ResultState.Error -> {
                    Text("Error: ${uploadState.exception}")
                    println("Error:-->${uploadState.exception}")
                }

                else -> {}
            }
            val downloadState = viewmodel.downloadState.value

            when (downloadState) {
                is ResultState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ResultState.Success -> {
                    val imageUrl = downloadState.data
                    LaunchedEffect(key1 = imageUrl) {
                        user = user.copy(profileImage = imageUrl)

                        val request = EditProfileRequest(
                            name = user.name,
                            profileImageUrl = user.profileImage
                        )
                        userViewModel.editProfile(user.id, request)
                    }
                }

                is ResultState.Error -> {
                    Text("Error: ${downloadState.exception}")
                }

                else -> {}
            }
            val postState by userViewModel.updateProfileState
            when (postState) {
                is ResultState.Loading -> CircularProgressIndicator()
                is ResultState.Success -> {
                    println("Post created: ${(postState as ResultState.Success).data}")
                    val postResult = (postState as ResultState.Success).data
                    LaunchedEffect(postResult) {
                        println("Post created: $postResult")
                        navController.navigateUp()
                        userViewModel.resetUpdateProfileState()
                    }
                }

                is ResultState.Error -> {
                    println("Error: ${(postState as ResultState.Error).exception}")
                }

                else -> {}
            }
            LaunchedEffect(isSelectPhoto) {
                if (isSelectPhoto) {
                    val result = imagePickerManager.showImagePicker()
                    isLoading = false
                    isSelectPhoto = false

                    when {
                        result.error != null -> errorMessage = result.error
                        result.isCancelled -> { /* Handle cancellation */
                        }

                        result.imageUri != null -> selectedImageUri = result.imageUri
                    }
                }
            }

        }
    }


}
