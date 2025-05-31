package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.vinceglb.filekit.coil.AsyncImage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_backarrow
import newskotlinproject.composeapp.generated.resources.ic_edit
import newskotlinproject.composeapp.generated.resources.ic_user_profile_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.CreatePostRequest
import org.kotlin.multiplatform.newsapp.model.EditProfileRequest
import org.kotlin.multiplatform.newsapp.model.MediaItem
import org.kotlin.multiplatform.newsapp.model.MediaType
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.model.UserResponseData
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.generateId
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    viewmodel: CommunityViewModel
) {
    val userState by userViewModel.getUserState

    var user by remember { mutableStateOf(UserResponseData("","","","")) }


    LaunchedEffect(Unit){
        userViewModel.getUserById(SessionUtil.getUserId().toString())
    }
    val coroutineScope = rememberCoroutineScope()
    var pickedFiles = remember { mutableStateListOf<PlatformFile>() }

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
            user = (userState as ResultState.Success).data
        }

        is ResultState.Error -> {
            val error = (userState as ResultState.Error)
            println("Error:->$error")
        }

        else -> {}
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
//            .padding(paddingValues)
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
                            coroutineScope.launch {
                                val files = FileKit.openFilePicker(
                                    type = FileKitType.Image
                                )
                                files?.let { file ->
                                    pickedFiles.clear()
                                    pickedFiles.add(file)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    val file = pickedFiles.firstOrNull()
                    if (file != null) {
                        AsyncImage(
                            file = file,
                            contentDescription = file.name,
                            modifier = Modifier.fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
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
                            coroutineScope.launch {
                                val files = FileKit.openFilePicker(
                                    type = FileKitType.Image
                                )
                                files?.let { file ->
                                    pickedFiles.clear()
                                    pickedFiles.add(file)
                                }
                            }
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
                    coroutineScope.launch {
                        pickedFiles.forEach { file ->
                            val bytes = file.readBytes() // ✅ This works in KMP
                            viewmodel.uploadImage(bytes, file.name)
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
                        println("Upload successful: $imageUrl")
                        println("image url:-->$imageUrl")

                        // ✅ Update user profile image URL
                        user = user.copy(profileImage = imageUrl)

                        val request = EditProfileRequest(
                            name = user.name,
                            profileImageUrl = user.profileImage // ✅ Now contains the updated image URL
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
                }
                }

                is ResultState.Error -> {
                    println("Error: ${(postState as ResultState.Error).exception}")
                }

                else -> {}
            }

        }
    }


}
