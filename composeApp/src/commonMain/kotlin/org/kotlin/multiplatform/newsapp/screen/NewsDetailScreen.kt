package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_news_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.openLink
import org.kotlin.multiplatform.newsapp.utils.shareNews
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: String,
    newsViewmodel: NewsViewmodel,
    navController: NavController,
) {

    val updateLabelsState by newsViewmodel.newsDetailState

    LaunchedEffect(newsId) {
        newsViewmodel.getNewsById(newsId)
    }


    var news by remember { mutableStateOf(NewsPost("", "", "", "", "", "", imageUrl = "")) }
    when (updateLabelsState) {

        is ResultState.Success -> {
            news = (updateLabelsState as ResultState.Success).data
        }

        is ResultState.Error -> {
            val error = (updateLabelsState as ResultState.Error)
            println("NewsDetails Error:-$error")
        }

        else -> {}
    }

val scrollState= rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = news.channelName,
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        shareNews(news.title, news.details, news.link,news.imageUrl)
                    }) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
                },
            )

            // Display the details of the news post
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)
                .verticalScroll(scrollState)) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
                // Image
                val imageResource = asyncPainterResource(news.imageUrl)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    if (imageResource is Resource.Loading || imageResource is Resource.Failure) {
                        Image(
                            painter = painterResource(Res.drawable.ic_news_pl),
                            contentDescription = "Loading placeholder",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }
                    KamelImage({ imageResource }, contentDescription = "contentDescription",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop,
                        onLoading = {
                            println("Loading image...")
                        }, onFailure = {
                            println("Failed to load image")
                        })
                }
                Spacer(modifier = Modifier.height(18.dp))

                // Link
                Text(
                    text = news.link,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        openLink(news.link)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // News content/details
                Text(
                    text = news.details,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }


}