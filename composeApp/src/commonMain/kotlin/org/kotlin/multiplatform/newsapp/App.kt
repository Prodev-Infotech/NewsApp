package org.kotlin.multiplatform.newsapp

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kotlin.multiplatform.newsapp.screen.LoginScreen
import org.kotlin.multiplatform.newsapp.screen.MainScreen
import org.kotlin.multiplatform.newsapp.screen.NewsDetailScreen
import org.kotlin.multiplatform.newsapp.screen.SignUpScreen
import org.kotlin.multiplatform.newsapp.screen.SplashScreen
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.setStatusBarColor
import org.kotlin.multiplatform.newsapp.viewmodel.NewsViewmodel
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val startDestination = if (SessionUtil.isUserLoggedIn()) "main" else "Login"
    val newsViewmodel = remember { NewsViewmodel() }

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

        composable(
            route="news/detail/{news_id}",
            arguments = listOf(navArgument("news_id"){ type= NavType.StringType})
        ){backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("news_id") ?: ""
            println("NavController News ID:-$newsId")
            NewsDetailScreen(newsId, newsViewmodel = newsViewmodel, navController = navController)

        }


    }
}