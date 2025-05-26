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
import org.kotlin.multiplatform.newsapp.screen.CommunicationScreen
import org.kotlin.multiplatform.newsapp.screen.EditProfileScreen
import org.kotlin.multiplatform.newsapp.screen.NewsScreen
import org.kotlin.multiplatform.newsapp.screen.ProfileScreen
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

fun NavGraphBuilder.mainNavGraph(
    rootNavController: NavHostController,
    innerPadding: PaddingValues,
    navController: NavController
) {


    navigation(
        startDestination = Routes.News.route,
        route = Graph.NAVIGATION_BAR_SCREEN_GRAPH
    ) {
        composable(route = Routes.News.route) {
            val newsViewmodel:NewsViewmodel= viewModel()
            NewsScreen(rootNavController = rootNavController, paddingValues = innerPadding,newsViewmodel,navController=navController)
        }
        composable(route = Routes.BookMark.route) {
            val newsViewmodel:NewsViewmodel= viewModel()

            BookMarkScreen(rootNavController = rootNavController, paddingValues = innerPadding,newsViewmodel,navController=navController)
        }
        composable(route = Routes.Community.route) {
            val communityViewModel:CommunityViewModel= viewModel()
            CommunicationScreen(rootNavController=rootNavController,paddingValues = innerPadding,viewModel =communityViewModel,navController=navController)

        }
        composable(route = Routes.Profile.route) {
            val userViewModel:UserViewModel= viewModel()
           ProfileScreen(navController=navController, paddingValues = innerPadding,userViewModel)
        }


    }

}