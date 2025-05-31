package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_backarrow
import newskotlinproject.composeapp.generated.resources.ic_comments
import newskotlinproject.composeapp.generated.resources.ic_news_pl
import newskotlinproject.composeapp.generated.resources.ic_unfill_bookmark
import newskotlinproject.composeapp.generated.resources.ic_unfill_like
import newskotlinproject.composeapp.generated.resources.ic_user_profile_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.Post
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.openLink
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.getTimeAgo
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailsScreen(
    communityId: String,
    communityViewModel: CommunityViewModel,
    navController: NavController,
) {
    val communityState by communityViewModel.singleCommunityState
    var selectedTab by remember { mutableStateOf(0) }
    val postsState by communityViewModel.communityPosts
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }

    LaunchedEffect(communityId) {
        communityViewModel.fetchSingleCommunity(communityId, SessionUtil.getUserId().toString())
        communityViewModel.fetchCommunityPosts(communityId)
    }
    when (postsState) {
        is ResultState.Loading -> {
            CircularProgressIndicator()
        }

        is ResultState.Success -> {
            posts = (postsState as ResultState.Success).data
        }

        is ResultState.Error -> {
            val error = (postsState as ResultState.Error).exception
            Text("Error: ${error}")
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
            val community = (communityState as ResultState.Success).data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFECEEFF))
            ) {
                HeaderSection(community, communityViewModel, navController)
                TabsSection(selectedTab) { selectedTab = it }

                TransactionList(
                    isPrivate = community.isPrivate,
                    joinStatus = community.joinStatus,
                    selectedTab = selectedTab,
                    posts=posts
                )

            }
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

}

@Composable
fun HeaderSection(
    community: CommunityWithJoinStatus,
    communityViewModel: CommunityViewModel,
    navController: NavController,
) {
    val imageResource = asyncPainterResource(community.imageUrl ?: "")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Approx 1/6 screen
    ) {

        if (imageResource is Resource.Loading || imageResource is Resource.Failure) {
            Image(
                painter = painterResource(Res.drawable.ic_news_pl),
                contentDescription = "Loading placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize().blur(5.dp),
            )
        }
        KamelImage({ imageResource },
            contentDescription = "contentDescription",
            modifier = Modifier
                .matchParentSize().blur(5.dp),
            contentScale = ContentScale.Crop,
            onLoading = {
                println("Loading image...")
            }, onFailure = {
                println("Failed to load image")
            })

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_backarrow),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        // Overlay content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = community.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = community.authorName,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val isJoined = community.joinStatus == "Joined"
                val isPrivate = community.isPrivate

                val isButtonEnabled = !isPrivate || isJoined

                Button(
                    onClick = {
//                        navController.navigate("CreatePostScreen")
                        navController.navigate("create/post/${community.id}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isButtonEnabled) Color(0xFF1A237E) else Color.Gray
                    ),
                    enabled = isButtonEnabled
                ) {
                    Text("Create Community", color = Color.White)
                }

                Button(
                    onClick = {
                        if (community.joinStatus.equals("Join"))
                            SessionUtil.getUser()
                                ?.let { communityViewModel.joinCommunity(it, community.id) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Text(community.joinStatus, color = Color.White)
                }
            }
        }

    }
}

@Composable
fun TabsSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
) {
    val tabs = listOf("Image", "Video")

    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .padding(8.dp)
            ) {
                Text(
                    title,
                    color = if (selectedTab == index) Color(0xFF375BCE) else Color.Gray,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                )
                if (selectedTab == index) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(24.dp)
                            .background(Color(0xFF375BCE))
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    isPrivate: Boolean,
    joinStatus: String,
    selectedTab: Int,
    posts:List<Post>
) {
    val isJoined = joinStatus == "Joined"
    val isButtonEnabled = !isPrivate || isJoined

    if (!isButtonEnabled) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "This is a private community.",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Join to see more.",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        MainFeedScreen(selectedTab = selectedTab,posts)
    }
}

@Composable
fun MainFeedScreen(selectedTab: Int,posts: List<Post>) {
//    val sampleData = listOf(
//        Post(
//            postId = "4960",
//            communityId = "community123",
//            userId = "user123",
//            title = "Learn Kotlin",
//            authorName = "John Doe",
//            userProfileImageUrl = "https://static3.depositphotos.com/1000951/138/i/450/depositphotos_1380772-stock-photo-profile-of-beautiful-smiling-girl.jpg",
//            media = MediaItem(
//                "media123",
//                "Image",
//                "https://static3.depositphotos.com/1000951/138/i/450/depositphotos_1380772-stock-photo-profile-of-beautiful-smiling-girl.jpg"
//            ),
//            link = "https://kotlinlang.org"
//        ),
//        Post(
//            postId = "98e5f0e9",
//            communityId = "community123",
//            userId = "user123",
//            title = "Kotlin Video",
//            authorName = "John Doe",
//            userProfileImageUrl = "https://static3.depositphotos.com/1000951/138/i/450/depositphotos_1380772-stock-photo-profile-of-beautiful-smiling-girl.jpg",
//            media = MediaItem("media124", "Video", mediaUrl = "https://example.com/video.mp4"),
//            link = null
//        )
//    )

    val filteredPosts = when (selectedTab) {
        0 -> posts.filter { it.media.type.equals("Image", ignoreCase = true) }
        1 -> posts.filter { it.media.type.equals("Video", ignoreCase = true) }
        else -> posts
    }

    InstaStyleFeed(posts = filteredPosts,selectedTab = selectedTab)
}

@Composable
fun InstaStyleFeed(posts: List<Post>, selectedTab: Int) {
    if (posts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (selectedTab == 0) "No photo yet!" else "No video yet!",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(posts) { post ->
                PostItem(post)
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    val timeAgo = remember(post.postedAt) {
        getTimeAgo(post.postedAt)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Top Row: Profile & Author & Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Load user profile image here
                    val profilePainter = asyncPainterResource(post.userProfileImageUrl)
                    KamelImage(
                        resource = { profilePainter },
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        onFailure = {
                            // fallback: simple placeholder circle with initials or emoji
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Gray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.authorName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(timeAgo, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Text("⋯", fontSize = 24.sp) // Options menu icon
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Media content
            when (post.media.type.lowercase()) {
                "image" -> {
                    val imagePainter = asyncPainterResource(post.media.mediaUrl)
                    KamelImage(
                        resource = { imagePainter },
                        contentDescription = "Post Image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        onLoading = {
                            Box(
                                Modifier.fillMaxWidth().height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Loading...")
                            }
                        },
                        onFailure = {
                            Box(
                                Modifier.fillMaxWidth().height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Failed to load image")
                            }
                        }
                    )
                }

                "video", "viedo" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶️", fontSize = 48.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Like, Comment, Share
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
//                val isLiked = likeState is ResultState.Success && (likeState).data
//                println("isLiked:--$isLiked")
                Icon(
                    painter = painterResource(
//                        if (isLiked) Res.drawable.ic_fill_like else
                        Res.drawable.ic_unfill_like
                    ),
                    contentDescription = "Like",
//                    tint = if (isLiked) Color.Red else Color.Gray,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
//                            newsViewmodel.toggleLike(
//                                news.id,
//                                SessionUtil.getUserId().toString()
//                            )
                        }
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Like count
//                when (likeCountState) {
//                    is ResultState.Success -> Text(
//                        text = "${(likeCountState).data}",
//                        color = Color.Black
//                    )
//
//                    is ResultState.Loading -> CircularProgressIndicator(
//                        modifier = Modifier.size(
//                            16.dp
//                        )
//                    )
//
//                    else -> Text("0", color = Color.Black)
//                }
                Spacer(modifier = Modifier.width(16.dp))

                // Comment icon and count
                Icon(
                    painter = painterResource(Res.drawable.ic_comments),
                    contentDescription = "Comments",
                    modifier = Modifier
                        .size(24.dp)
//                        .clickable {
//                            showBottomSheet.value = true
//                            scope.launch { sheetState.show() }
//                        }
                )
                Spacer(modifier = Modifier.width(6.dp))
//                if (commentsState is ResultState.Success) {
//                    val count = (commentsState).data.size
//                    Text("$count", color = Color.Black)
//                }
                Spacer(modifier = Modifier.weight(1f))
                // Bookmark
                Icon(
                    painter = painterResource(
//                        if (isBookmarked) Res.drawable.ic_fill_bookmark else
                        Res.drawable.ic_unfill_bookmark
                    ),
                    contentDescription = "Bookmark",
//                    tint = if (isBookmarked) Color.Blue else Color.Gray,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(28.dp)
//                        .clickable {
//                            if (!isBookMarkScreen) newsViewmodel.toggleBookmark(news)
//                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title or Description
            Text(post.title, fontSize = 14.sp)

            // Link (if any)
            if (post.link?.isNotEmpty() == true) {
                Text(
                    post.link!!,
                    fontSize = 14.sp,
                    color = Color(0xFF1A73E8),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                        .clickable {
                            openLink(post.link!!)
                        }
                )
            }
        }
    }
}

@Composable
fun TransactionItem(name: String, amount: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_user_profile_pl), // replace with avatar
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            Text("Today, 12:30 PM", fontSize = 12.sp, color = Color.Gray)
        }
        Text(amount, color = color, fontWeight = FontWeight.Bold)
    }
}