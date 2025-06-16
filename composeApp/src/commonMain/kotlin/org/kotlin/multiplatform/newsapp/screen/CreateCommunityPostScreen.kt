package org.kotlin.multiplatform.newsapp.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_add_photo
import newskotlinproject.composeapp.generated.resources.ic_backarrow
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.VideoPicker.VideoPlayer
import org.kotlin.multiplatform.newsapp.camera.CameraManager
import org.kotlin.multiplatform.newsapp.camera.getImageDetailsFromUri
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionsManager
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.CreatePostRequest
import org.kotlin.multiplatform.newsapp.model.MediaItem
import org.kotlin.multiplatform.newsapp.model.MediaType
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.UserResponseData
import org.kotlin.multiplatform.newsapp.utils.ImagePickerManager
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.generateId
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    communityId: String,
    viewModel: CommunityViewModel,
    navController: NavController,
    userViewModel: UserViewModel,
    cameraManager: CameraManager, permissionsManager: PermissionsManager,
) {

    var hasNavigatedBack by remember { mutableStateOf(false) }

    val communityState by viewModel.singleCommunityState
    var community: CommunityWithJoinStatus? = null
    var communityTitle by remember { mutableStateOf("") }
    var communityLink by remember { mutableStateOf("") }

    var user by remember { mutableStateOf(UserResponseData("", "", "", "")) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val imagePickerManager = remember {
        // You'll need to create this based on your DI setup
        ImagePickerManager(cameraManager, permissionsManager)
    }

    val userState by userViewModel.getUserState
    LaunchedEffect(communityId) {
        viewModel.fetchSingleCommunity(communityId, SessionUtil.getUserId().toString())
        userViewModel.getUserById(SessionUtil.getUserId().toString())
    }
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
    // UI based on state
    when (communityState) {
        is ResultState.Initial, is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            community = (communityState as ResultState.Success).data

        }

        is ResultState.Error -> {
            val message = (communityState as ResultState.Error).exception
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $message", color = Color.Red)
            }
        }
    }
    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()
    var properUri by remember { mutableStateOf<String?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Community Post",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        androidx.compose.material3.Icon(
                            painter = painterResource(Res.drawable.ic_backarrow),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF757575)
                )
            )
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp)
                    .verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable {
                            isLoading = true
                            errorMessage = null

                            CoroutineScope(Dispatchers.Main).launch {
                                println("Picker starting...") // check this log
                                val result = imagePickerManager.showImagePicker()
                                println("RESULT: $result")
                                isLoading = false

                                when {
                                    result.error != null -> errorMessage = result.error
                                    result.isCancelled -> { /* Handle cancellation */
                                    }

                                    result.imageUri != null -> {
                                        selectedImageUri = result.imageUri
                                        selectedVideoUri = null // reset video
                                    }

                                    result.videoUri != null -> {
                                        selectedVideoUri = result.videoUri
                                        selectedImageUri = null // reset image
                                    }
                                }
                            }
                        }
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                )
                {
                    when {
                        selectedImageUri != null -> {
                            // Show image
                            val imageResource = asyncPainterResource("file://$selectedImageUri")

                                KamelImage(
                                    resource = { imageResource },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                    onLoading = { println("Loading...") },
                                    onFailure = { println("Failed to load image: ${it.message}") }
                                )
                        }

                        selectedVideoUri != null -> {
                            // Show video preview (replace with your video player)
                            VideoPlayer(
                                modifier = Modifier.fillMaxWidth().height(300.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                url = selectedVideoUri!! // Ensure `video.path` is the full URI
                            )
                        }

                        else -> {
                            // Default icon
                            Icon(
                                painter = painterResource(Res.drawable.ic_add_photo),
                                contentDescription = "Add Media",
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    // Show error message
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    // Loading indicator
                        if (isLoading) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                }

                val detailsScrollState = rememberScrollState()

                LaunchedEffect(communityTitle) {
                    detailsScrollState.scrollTo(detailsScrollState.maxValue)
                }

                TextField(
                    value = communityTitle,
                    onValueChange = { communityTitle = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    placeholder = { Text("What's on your mind?") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    maxLines = Int.MAX_VALUE
                )
                TextField(
                    value = communityLink,
                    onValueChange = { communityLink = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    placeholder = { Text("Optional Link") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            when {
                                selectedImageUri != null -> {
                                    val (name, bytes) = getImageDetailsFromUri("file://$selectedImageUri")
                                    println("Image name: $name, size: ${bytes?.size}")
                                    if (bytes != null && name != null) {
                                        viewModel.uploadImage(bytes, name)
                                    }
                                }

                                selectedVideoUri != null -> {
                                    val (name, bytes) = getImageDetailsFromUri("file://$selectedVideoUri")
                                    println("Video name: $name, size: ${bytes?.size}")
                                    if (bytes != null && name != null) {
                                        viewModel.uploadVideo(bytes, name)
                                    }
                                }

                                else -> {
                                    println("No media selected.")
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Upload Media")
                }

                when (val uploadState = viewModel.uploadState.value) {
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
                                viewModel.downloadImage(imageName)
                            }
                        }
                    }

                    is ResultState.Error -> {
                        Text("Error: ${uploadState.exception}")
                        println("Error:-->${uploadState.exception}")
                    }

                    else -> {}
                }
                when (val uploadVideoState = viewModel.uploadVideoState.value) {
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
                        println("Upload successful: ${uploadVideoState.data}")
                        val videoName = (uploadVideoState as? ResultState.Success)?.data
                        LaunchedEffect(videoName) {
                            if (videoName != null) {
                                println("Triggering download for: $videoName")
                                viewModel.fetchVideoUrl(videoName)
                            }
                        }
                    }

                    is ResultState.Error -> {
                        Text("Error: ${uploadVideoState.exception}")
                        println("Error:-->${uploadVideoState.exception}")
                    }

                    else -> {}
                }
                val videoUrlState = viewModel.videoUrlState.value

                when (videoUrlState) {
                    is ResultState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ResultState.Success -> {
                        val videoUrl = videoUrlState.data
                        LaunchedEffect(key1 = videoUrl) {
                            println("Upload successful: $videoUrl")
                            println("image url:-->$videoUrl")

                            val request = community?.id?.let {
                                CreatePostRequest(
                                    communityId = it,
                                    userId = SessionUtil.getUserId().toString(),
                                    title = communityTitle,
                                    authorName = user.name,
                                    userProfileImageUrl = user.profileImage.toString(),
                                    media = MediaItem(
                                        id = generateId(),
                                        type = MediaType.Video.toString(),
                                        mediaUrl = videoUrl
                                    ),
                                    link = communityLink
                                )
                            }
                            if (request != null) {
                                viewModel.createPost(request)
                                // Reset image selection after initiating post creation
                            }
                        }
                    }

                    is ResultState.Error -> {
                        Text("Error: ${videoUrlState.exception}")
                    }

                    else -> {}
                }
                val downloadState = viewModel.downloadState.value

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

                            val request = community?.id?.let {
                                CreatePostRequest(
                                    communityId = it,
                                    userId = SessionUtil.getUserId().toString(),
                                    title = communityTitle,
                                    authorName = user.name,
                                    userProfileImageUrl = user.profileImage.toString(),
                                    media = MediaItem(
                                        id = generateId(),
                                        type = MediaType.Image.toString(),
                                        mediaUrl = imageUrl
                                    ),
                                    link = communityLink
                                )
                            }
                            if (request != null) {
                                viewModel.createPost(request)
                                // Reset image selection after initiating post creation
                            }
                        }
                    }

                    is ResultState.Error -> {
                        Text("Error: ${downloadState.exception}")
                    }

                    else -> {}
                }
                val postState by viewModel.createPostState
                when (postState) {
                    is ResultState.Loading -> CircularProgressIndicator()
                    is ResultState.Success -> {
                        println("Post created: ${(postState as ResultState.Success).data}")
                        val postResult = (postState as ResultState.Success).data
                        if (!hasNavigatedBack) {
                            hasNavigatedBack = true
                            LaunchedEffect(postResult) {
                                println("Post created: $postResult")
                                navController.navigateUp()
                                viewModel.resetCreatePostState()
                            }
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
}
//@Composable
//fun CreatePostScreen() {
//    // State management
//    val coroutineScope = rememberCoroutineScope()
//    val pickedFiles = remember { mutableStateListOf<PlatformFile>() }
//    var selectedVideoFile by remember { mutableStateOf<PlatformFile?>(null) }
//
//    // Step 4: Video player dialog (shows when video is tapped)
//    selectedVideoFile?.let { videoFile ->
//        VideoPlayerDialog(
//            file = videoFile,
//            onDismiss = { selectedVideoFile = null }
//        )
//    }
//
//    // Step 5: Main UI Layout
//    Column(modifier = Modifier.fillMaxSize()) {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(3),
//            modifier = Modifier.weight(1f)
//        ) {
//            // Step 6: Display picked images/videos
//            items(pickedFiles) { file ->
//                // Check if file is video
//                val isVideo = file.extension?.lowercase() in listOf("mp4", "mov", "avi", "mkv", "webm", "3gp") ||
//                        file.name.lowercase().let { name ->
//                            name.endsWith(".mp4") || name.endsWith(".mov") ||
//                                    name.endsWith(".avi") || name.endsWith(".mkv") ||
//                                    name.endsWith(".webm") || name.endsWith(".3gp")
//                        }
//
//                // Debug log
//                println("🎥 File: ${file.name}, Extension: '${file.extension}', IsVideo: $isVideo")
//
//                Box(
//                    modifier = Modifier
//                        .aspectRatio(1f)
//                        .padding(4.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .clickable {
//                            if (isVideo) {
//                                selectedVideoFile = file
//                            } else {
//                                pickedFiles.remove(file)
//                            }
//                        }
//                ) {
//                    if (isVideo) {
//                        // Step 7: Video preview with gradient background
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(
//                                    brush = Brush.verticalGradient(
//                                        colors = listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))
//                                    )
//                                ),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                // Play button
//                                Box(
//                                    modifier = Modifier
//                                        .size(40.dp)
//                                        .background(Color.White.copy(alpha = 0.9f), CircleShape),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text(
//                                        text = "▶",
//                                        color = Color.Black,
//                                        fontSize = 18.sp
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.height(8.dp))
//
//                                // Video file name
//                                Text(
//                                    text = file.nameWithoutExtension,
//                                    color = Color.White,
//                                    fontSize = 10.sp,
//                                    maxLines = 2,
//                                    textAlign = TextAlign.Center,
//                                    overflow = TextOverflow.Ellipsis
//                                )
//                            }
//                        }
//                    } else {
//                        // Step 8: Image preview
//                        AsyncImage(
//                            file = file,
//                            contentDescription = file.name,
//                            modifier = Modifier.fillMaxSize(),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
//
//                    // Step 9: File name overlay for images
//                    if (!isVideo) {
//                        Text(
//                            text = file.nameWithoutExtension,
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .background(Color.Black.copy(alpha = 0.7f))
//                                .padding(4.dp),
//                            color = Color.White,
//                            fontSize = 10.sp,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//                }
//            }
//
//            // Step 10: Add media button
//            item {
//                Box(
//                    modifier = Modifier
//                        .aspectRatio(1f)
//                        .padding(4.dp)
//                        .background(Color.LightGray, RoundedCornerShape(8.dp))
//                        .clickable {
//                            coroutineScope.launch {
//                                val files = FileKit.openFilePicker(
//                                    type = FileKitType.ImageAndVideo,
//                                    mode = FileKitMode.Multiple()
//                                )
//                                files?.let {
//                                    pickedFiles.addAll(it)
//
//                                    it.forEach { file ->
//                                        println("📷 Picked file: name=${file.name}, type=${file.extension}, size=${file.nameWithoutExtension}")
//                                    }
//                                }
//                            }
//                        },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text("+", fontSize = 32.sp, color = Color.Gray)
//                        Text("Add Media", fontSize = 12.sp, color = Color.Gray)
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Step 11: Video Player Dialog
//@Composable
//fun VideoPlayerDialog(
//    file: PlatformFile,
//    onDismiss: () -> Unit
//) {
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Column {
//                // Step 12: Dialog Header
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = file.nameWithoutExtension,
//                        style = MaterialTheme.typography.headlineSmall,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    IconButton(onClick = onDismiss) {
//                        Text("✕", fontSize = 18.sp)
//                    }
//                }
//
//                // Step 13: Video player area
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .background(Color.Black),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Text(
//                            text = "🎬",
//                            fontSize = 48.sp,
//                            color = Color.White
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = "Video Player\n(Platform-specific implementation needed)",
//                            color = Color.White,
//                            textAlign = TextAlign.Center,
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//
//                // Step 14: Control buttons
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Button(
//                        onClick = {
//                            // TODO: Add actual video playback
//                            println("▶️ Playing video: ${file.name}")
//                        }
//                    ) {
//                        Text("Play")
//                    }
//
//                    Button(
//                        onClick = onDismiss
//                    ) {
//                        Text("Close")
//                    }
//                }
//            }
//        }
//    }
//}


//LazyVerticalGrid(
//columns = GridCells.Fixed(3),
//modifier = Modifier.weight(1f)
//) {
//    // ✅ Display picked images first
//    items(pickedFiles) { file ->
//        Box(
//            modifier = Modifier
//                .aspectRatio(1f)
//                .padding(4.dp)
//                .background(Color.Gray)
//                .clickable {
//                    // Remove image on click (optional)
//                    pickedFiles.remove(file)
//                }
//        ) {
//            AsyncImage(
//                file = file,
//                contentDescription = file.name,
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop
//            )
//
//            // ✅ Optional: Show file name overlay
//            Text(
//                text = file.nameWithoutExtension,
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .background(Color.Black.copy(alpha = 0.7f))
//                    .padding(4.dp),
//                color = Color.White,
//                fontSize = 10.sp,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//
//    // ✅ Add Button to pick more images/videos
//    item {
//        Box(
//            modifier = Modifier
//                .aspectRatio(1f)
//                .padding(4.dp)
//                .background(Color.LightGray, RoundedCornerShape(8.dp))
//                .clickable {
//                    coroutineScope.launch {
//                        val files = FileKit.openFilePicker(
//                            type = FileKitType.ImageAndVideo,
//                            mode = FileKitMode.Multiple()
//                        )
//                        files?.let {
//                            pickedFiles.addAll(it)
//
//                            it.forEach { file ->
//                                println("📷 Picked file: name=${file.name}, type=${file.extension}, size=${file.nameWithoutExtension}")
//                            }
//                        }
//                    }
//                },
//            contentAlignment = Alignment.Center
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Text("+", fontSize = 32.sp, color = Color.Gray)
//                Text("Add Media", fontSize = 12.sp, color = Color.Gray)
//            }
//        }
//    }
//
//}