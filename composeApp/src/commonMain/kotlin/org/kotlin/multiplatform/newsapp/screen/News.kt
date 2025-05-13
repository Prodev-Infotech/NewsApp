package org.kotlin.multiplatform.newsapp.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_bookmark
import newskotlinproject.composeapp.generated.resources.ic_logout
import newskotlinproject.composeapp.generated.resources.ic_news
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

@Composable
fun NewsItem(news: NewsPost,
             newsViewmodel:NewsViewmodel,
             isBookMarkScreen:Boolean,
             navController:NavController) {
    val timeAgo = remember(news.createdAt) {
        getTimeAgo(news.createdAt)
    }
    val formattedDate = formatDateOnly(news.createdAt)
    val isBookmarked = newsViewmodel.isBookmarked(news.id)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("news/detail/${news.id}")
                println("New List News Id:-->${news.id}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Title and Bookmark Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_bookmark),
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) Color.Blue else Color.Gray,

                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable {
                            if(!isBookMarkScreen) {
                                newsViewmodel.toggleBookmark(news)
                            }


                        }
                )
            }

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

            Spacer(modifier = Modifier.height(8.dp))

            // Date and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Channel name aligned right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = news.channelName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
            }
        }
    }

}

