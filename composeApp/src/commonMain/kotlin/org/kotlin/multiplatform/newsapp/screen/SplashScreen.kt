package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.kotlin.multiplatform.newsapp.utils.SessionUtil

@Composable
fun SplashScreen(navController: NavController){
    val startDestination = if (SessionUtil.isUserLoggedIn()) "main" else "Login"

    LaunchedEffect(Unit){
        delay(2000)
        navController.navigate(startDestination)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Welcome to My App!", fontSize = 30.sp)
    }
}