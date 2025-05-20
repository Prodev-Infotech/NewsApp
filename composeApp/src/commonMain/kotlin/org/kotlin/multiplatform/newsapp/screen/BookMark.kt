package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.kotlin.multiplatform.newsapp.model.NewsPost
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookMarkScreen(
    rootNavController: NavController,
    paddingValues: PaddingValues,
    newsViewmodel: NewsViewmodel,
    navController:NavController
) {
    val updateLabelsState by newsViewmodel.newsState
    var newsList by remember { mutableStateOf<List<NewsPost>>(emptyList()) }
    LaunchedEffect(Unit) {

        newsViewmodel.fetchBookMarkNews()
    }

    when (updateLabelsState) {

        is ResultState.Success -> {
            newsList = (updateLabelsState as ResultState.Success).data
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
                        text = "BookMark News",
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
                LazyColumn {
                    items(newsList) { news ->
                        NewsItem(news, newsViewmodel, true, navController = navController)
                    }
                }
            }
        }

    }
}