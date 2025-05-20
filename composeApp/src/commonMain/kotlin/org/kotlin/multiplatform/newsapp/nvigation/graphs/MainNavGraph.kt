package org.kotlin.multiplatform.newsapp.nvigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.kotlin.multiplatform.newsapp.nvigation.Graph
import org.kotlin.multiplatform.newsapp.nvigation.Routes
import org.kotlin.multiplatform.newsapp.screen.BookMarkScreen
import org.kotlin.multiplatform.newsapp.screen.NewsScreen
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel

fun NavGraphBuilder.mainNavGraph(
    rootNavController: NavHostController,
    innerPadding: PaddingValues,
    navController: NavController
) {


    navigation(
        startDestination = Routes.Label.route,
        route = Graph.NAVIGATION_BAR_SCREEN_GRAPH
    ) {
        composable(route = Routes.Label.route) {
            val newsViewmodel:NewsViewmodel= viewModel()
            NewsScreen(rootNavController = rootNavController, paddingValues = innerPadding,newsViewmodel,navController=navController)
        }
        composable(route = Routes.Notes.route) {
            val newsViewmodel:NewsViewmodel= viewModel()
            BookMarkScreen(rootNavController = rootNavController, paddingValues = innerPadding,newsViewmodel,navController=navController)
        }

    }

}