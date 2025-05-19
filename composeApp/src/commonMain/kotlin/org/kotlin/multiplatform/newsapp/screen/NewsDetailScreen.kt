package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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


    var news by remember { mutableStateOf<NewsPost>(NewsPost("", "", "", "", "", "", imageUrl = "")) }
    when (updateLabelsState) {

        is ResultState.Success -> {
            news = (updateLabelsState as ResultState.Success).data
        }

        is ResultState.Error -> {
            val error = (updateLabelsState as ResultState.Error)
        }

        else -> {}
    }


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
                        shareNews(news.title, news.details, news.link)
                    }) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
                },
            )

            // Display the details of the news post
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

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
                Spacer(modifier = Modifier.height(4.dp))
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