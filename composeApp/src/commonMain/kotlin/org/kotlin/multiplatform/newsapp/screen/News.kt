package org.kotlin.multiplatform.newsapp.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.kamel.core.Resource
import io.kamel.core.config.KamelConfig
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.imageBitmapDecoder
import kotlinx.coroutines.launch
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_comments
import newskotlinproject.composeapp.generated.resources.ic_fill_bookmark
import newskotlinproject.composeapp.generated.resources.ic_fill_like
import newskotlinproject.composeapp.generated.resources.ic_logout
import newskotlinproject.composeapp.generated.resources.ic_news
import newskotlinproject.composeapp.generated.resources.ic_send
import newskotlinproject.composeapp.generated.resources.ic_unfill_bookmark
import newskotlinproject.composeapp.generated.resources.ic_unfill_like
import newskotlinproject.composeapp.generated.resources.ic_user
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.openLink
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.formatDateOnly
import org.kotlin.multiplatform.newsapp.utils.getTimeAgo
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    rootNavController: NavController,
    paddingValues: PaddingValues,
    newsViewmodel:NewsViewmodel,
    navController:NavController
) {
    val updateLabelsState by newsViewmodel.newsState
    var newsList by remember { mutableStateOf<List<NewsPost>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    SessionUtil.logout()
                    showDialog = false

                    navController.navigate("Login") {
                        popUpTo("main") { inclusive = true } // remove "main" from back stack
                        launchSingleTop = true
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
    when (updateLabelsState) {

            is ResultState.Success -> {
                newsList = (updateLabelsState as ResultState.Success).data
                println("Updated News List: $newsList")
            }

            is ResultState.Error -> {
                val error = (updateLabelsState as ResultState.Error)
                println("Error:->$error")
            }

            else -> {}
        }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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
                        text = "News",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_logout),
                        contentDescription = "Log Out",
                        modifier = Modifier.clickable {
                            showDialog = true
                        },
                        tint = Color.White
                    )
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF757575)
                )
            )

            if (newsList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Empty!",
                        fontSize = 30.sp
                    )
                }
            }
            else {
                LazyColumn {
                    items(newsList) { news ->
                        NewsItem(news, newsViewmodel, false, navController)
                    }
                }
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsItem(
    news: NewsPost,
    newsViewmodel: NewsViewmodel,
    isBookMarkScreen: Boolean,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomSheet = remember { mutableStateOf(false) }

    val commentsState = newsViewmodel.commentsStates[news.id] ?: ResultState.Initial
    val likeState = newsViewmodel.likeStates[news.id] ?: ResultState.Initial
    val likeCountState = newsViewmodel.likeCountStates[news.id] ?: ResultState.Initial
    var commentInput by remember { mutableStateOf("") }

    // Load like count and comments when this composable enters composition
    LaunchedEffect(news.id) {
        newsViewmodel.getLikeCount(news.id)
        newsViewmodel.getComments(news.id)
        newsViewmodel.getUserLikeStatus(news.id, SessionUtil.getUserId().toString())
    }

    val timeAgo = remember(news.createdAt) { getTimeAgo(news.createdAt) }
    val formattedDate = formatDateOnly(news.createdAt)
    val isBookmarked = newsViewmodel.isBookmarked(news.id)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("news/detail/${news.id}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier
            .padding(10.dp)
            .wrapContentHeight()
        ) {
            // Image on the left

                val imageResource = asyncPainterResource(news.imageUrl)

                KamelImage(
                    resource = imageResource,
                    contentDescription = "contentDescription",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(150.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(5.dp)),
                    onLoading = {
                        println("Loading image..")
                    },
                    onFailure = {
                            println("Failed to load image")
                    }
                )


            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Top: Channel name and time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = news.channelName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f) // takes up remaining space
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // small gap
                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = news.link,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { openLink(news.link) },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(5.dp))

                // Date and Bookmark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_news),
                        contentDescription = "Calendar",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(
                            if (isBookmarked) Res.drawable.ic_fill_bookmark else Res.drawable.ic_unfill_bookmark
                        ),
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color.Blue else Color.Gray,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                if (!isBookMarkScreen) {
                                    newsViewmodel.toggleBookmark(news)
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Like and Comment Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like Button
                    val isLiked = likeState is ResultState.Success && (likeState).data
                    println("isLiked:--$isLiked")
                    Icon(
                        painter = painterResource(
                            if (isLiked) Res.drawable.ic_fill_like else Res.drawable.ic_unfill_like
                        ),
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                newsViewmodel.toggleLike(
                                    news.id,
                                    SessionUtil.getUserId().toString()
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // Like count
                    when (likeCountState) {
                        is ResultState.Success -> Text(
                            text = "${(likeCountState as ResultState.Success).data}",
                            color = Color.Black
                        )

                        is ResultState.Loading -> CircularProgressIndicator(
                            modifier = Modifier.size(
                                16.dp
                            )
                        )

                        else -> Text("0", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Comment icon and count
                    Icon(
                        painter = painterResource(Res.drawable.ic_comments),
                        contentDescription = "Comments",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                showBottomSheet.value = true
                                scope.launch { sheetState.show() }
                            }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (commentsState is ResultState.Success) {
                        val count = (commentsState as ResultState.Success).data.size
                        Text("$count", color = Color.Black)
                    }
                }
            }
        }

    }
    val listState = rememberLazyListState()

    if (showBottomSheet.value) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet.value = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.75f)
                    .padding(horizontal = 16.dp)
                    .imePadding()
            ) {
                Text(
                    text = "Comments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                when (commentsState) {
                    is ResultState.Success -> {
                        val comments = (commentsState).data

                        if (comments.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No comment!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        } else {
                        LaunchedEffect(comments.size) {
                            if (comments.isNotEmpty()) {
                                listState.animateScrollToItem(comments.lastIndex)
                            }
                        }
                        }
                        LazyColumn(
                            state = listState, // <--- this is crucial
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {


                            items(comments) { comment ->

                                val commentId = comment.id
                                val userId = SessionUtil.getUserId().toString()

                                // Trigger fetching like status/count per comment
                                LaunchedEffect(commentId) {
                                    newsViewmodel.getCommentLikeStatus(commentId, userId)
                                    newsViewmodel.getCommentLikeCount(commentId)
                                }

                                val isLiked = newsViewmodel.commentLikeStates[comment.id] is ResultState.Success && (newsViewmodel.commentLikeStates[comment.id] as ResultState.Success).data
                                val likeCount = newsViewmodel.commentLikeCountStates[comment.id] as? ResultState.Success<Int>

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Placeholder avatar
                                    Image(
                                        painter = painterResource(Res.drawable.ic_user), // replace with actual avatar
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = comment.userName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = remember(comment.createdAt) { getTimeAgo(comment.createdAt) }, // optionally format actual timestamp
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }

                                        Text(
                                            text = comment.content,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        // View replies text
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                newsViewmodel.toggleCommentLike(commentId, userId)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Like comment",
                                                tint = if (isLiked) Color.Red else Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = "${likeCount?.data ?: 0}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Divider(color = Color.LightGray)
                            }
                        }

                    }

                    is ResultState.Loading -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    is ResultState.Error -> Text("Failed to load comments", color = Color.Red)
                    else -> {}
                }



                // Comment input field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder avatar
                    val scrollState = rememberScrollState()

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = { Text("Add a comment...") },
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.LightGray,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            maxLines = 5,
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (commentInput.isNotBlank()) {
                                newsViewmodel.postComment(news.id, SessionUtil.getUserId().toString(), commentInput,SessionUtil.getUserName().toString())
                                commentInput = ""
                                keyboardController?.hide() // Dismiss keyboard
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
//                        Text("GIF") // or replace with an icon

                        Icon(painter = painterResource(Res.drawable.ic_send),contentDescription = "Send")
                    }
                }
            }
        }
    }


}
