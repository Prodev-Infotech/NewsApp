package org.kotlin.multiplatform.newsapp

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kotlin.multiplatform.newsapp.screen.CommunityDetailsScreen
import org.kotlin.multiplatform.newsapp.screen.EditProfileScreen
import org.kotlin.multiplatform.newsapp.screen.LoginScreen
import org.kotlin.multiplatform.newsapp.screen.MainScreen
import org.kotlin.multiplatform.newsapp.screen.NewsDetailScreen
import org.kotlin.multiplatform.newsapp.screen.SignUpScreen
import org.kotlin.multiplatform.newsapp.screen.SplashScreen
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val newsViewmodel = remember { NewsViewmodel() }
    val communityViewModel = remember { CommunityViewModel() }

    NavHost(navController = navController, startDestination = "Splash") {

        composable("Splash"){
            SplashScreen(navController = navController)
        }
        composable("Login") {
            LoginScreen(viewModel = UserViewModel(), navController = navController)
        }
        composable("SignUp") {
            SignUpScreen(viewModel = UserViewModel(), navController = navController)
        }
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("editProfile"){
            val userViewModel:UserViewModel= viewModel()
                EditProfileScreen(navController=navController,userViewModel=userViewModel)
        }

        composable(
            route="news/detail/{news_id}",
            arguments = listOf(navArgument("news_id"){ type= NavType.StringType})
        ){backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("news_id") ?: ""
            println("NavController News ID:-$newsId")
            NewsDetailScreen(newsId, newsViewmodel = newsViewmodel, navController = navController)

        }
        composable(
            route="community/detail/{community_id}",
            arguments = listOf(navArgument("community_id"){ type= NavType.StringType})
        ){backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("community_id") ?: ""
            println("NavController Community ID:-$communityId")
            CommunityDetailsScreen(communityId, communityViewModel = communityViewModel, navController = navController)

        }


    }
}