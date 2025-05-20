package org.kotlin.multiplatform.newsapp.nvigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import org.kotlin.multiplatform.newsapp.nvigation.Graph

@Composable
fun RootNavGraph(
    rootNavController: NavHostController,
    innerPadding: PaddingValues,
    navController: NavController
) {

    NavHost(
        navController = rootNavController,
        startDestination = Graph.NAVIGATION_BAR_SCREEN_GRAPH,
    ) {
        mainNavGraph(rootNavController = rootNavController, innerPadding = innerPadding,navController=navController)
    }
}