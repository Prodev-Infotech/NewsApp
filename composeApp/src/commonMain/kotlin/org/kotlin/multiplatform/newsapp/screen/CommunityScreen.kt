package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_news_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.BaseResponse
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.formatDateOnly
import org.kotlin.multiplatform.newsapp.utils.getTimeAgo
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunicationScreen(
    rootNavController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: CommunityViewModel,
    navController: NavController,
) {
    val updateLabelsState by viewModel.communityState
    var newsList by remember { mutableStateOf<List<CommunityWithJoinStatus>>(emptyList()) }
    val navBackStackEntry = remember { rootNavController.currentBackStackEntryFlow }
    when (updateLabelsState) {

        is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

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
    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry.collect { entry ->
            entry.lifecycle.addObserver(
                LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.fetchCommunities(SessionUtil.getUserId().toString())
                    }
                }
            )
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.TopStart

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
                        text = "Community",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(newsList) { community ->
                        CommunityCard(community, viewModel)
                    }
                }
            }

        }
    }
}

@Composable
fun CommunityCard(
    community: CommunityWithJoinStatus,
    viewModel: CommunityViewModel,
) {

    val joinState by viewModel.joinCommunityState

    when (joinState) {
        is ResultState.Loading -> CircularProgressIndicator()
        is ResultState.Success -> {
            println("Success: ${(joinState as ResultState.Success<BaseResponse>).data.message}")
        }

        is ResultState.Error -> {
            println("Error: ${(joinState as ResultState.Error).exception}")
        }

        ResultState.Initial -> {}
    }
    val formattedDate = formatDateOnly(community.createdAt)
    val timeAgo = remember(community.createdAt) {
        getTimeAgo(community.createdAt)
    }

    val imageResource = community.imageUrl?.let { asyncPainterResource(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (imageResource is Resource.Loading || imageResource is Resource.Failure) {
            Image(
                painter = painterResource(Res.drawable.ic_news_pl),
                contentDescription = "Loading placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize(),
            )
        }
        KamelImage({ imageResource!! },
            contentDescription = "contentDescription",
            modifier = Modifier
                .matchParentSize(),
            contentScale = ContentScale.Crop,
            onLoading = {
                println("Loading image...")
            }, onFailure = {
                println("Failed to load image")
            })

        // Overlay background
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                    )
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    text = community.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))


                Text(
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
                    text = community.authorName,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }

            // Join Community Button
            Button(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp).fillMaxWidth(),
                onClick = {
                    if(community.joinStatus.equals("Join"))
                    SessionUtil.getUser()?.let { viewModel.joinCommunity(it, community.id) }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = community.joinStatus, color = Color(0xFF6200EE))
            }
        }
    }
}