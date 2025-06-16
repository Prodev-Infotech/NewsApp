package org.kotlin.multiplatform.newsapp

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.kotlin.multiplatform.newsapp.camera.CameraManager
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionsManager
import org.kotlin.multiplatform.newsapp.screen.ChangePasswordScreen
import org.kotlin.multiplatform.newsapp.screen.CommunityDetailsScreen
import org.kotlin.multiplatform.newsapp.screen.CreatePostScreen
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
//@Preview
fun App(cameraManager: CameraManager, permissionsManager: PermissionsManager) {
    val navController = rememberNavController()
    val newsViewmodel = remember { NewsViewmodel() }
    val communityViewModel = remember { CommunityViewModel() }
    val userViewModel = remember { UserViewModel() }

    NavHost(navController = navController, startDestination = "Splash") {

        composable("Splash") {
            SplashScreen(navController = navController)
        }
        composable("Login") {
            LoginScreen(
                viewModel = UserViewModel(),
                navController = navController
            )
        }
        composable("SignUp") {
            SignUpScreen(
                viewModel = UserViewModel(),
                navController = navController
            )
        }
        composable("main") {
            MainScreen(navController = navController)
//            VideoPickerScreen(viewModel,communityViewModel)
        }
        composable("editProfile") {
            EditProfileScreen(
                navController = navController,
                userViewModel = userViewModel,
                viewmodel = communityViewModel,
                cameraManager = cameraManager, permissionsManager
            )
        }

        composable(
            route = "news/detail/{news_id}",
            arguments = listOf(navArgument("news_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("news_id") ?: ""
            println("NavController News ID:-$newsId")
            NewsDetailScreen(newsId, newsViewmodel = newsViewmodel, navController = navController)

        }
        composable(
            route = "community/detail/{community_id}",
            arguments = listOf(navArgument("community_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("community_id") ?: ""
            println("NavController Community ID:-$communityId")
            CommunityDetailsScreen(
                communityId,
                communityViewModel = communityViewModel,
                navController = navController
            )

        }
        composable(
            route = "create/post/{community_id}",
            arguments = listOf(navArgument("community_id") { type = NavType.StringType })
        ) { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("community_id") ?: ""
            println("NavController Community ID:-$communityId")
            CreatePostScreen(
                communityId, viewModel = communityViewModel,
                navController = navController,
                userViewModel,
                cameraManager = cameraManager,
                permissionsManager
            )

        }

        composable("changePassword") {
            ChangePasswordScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}