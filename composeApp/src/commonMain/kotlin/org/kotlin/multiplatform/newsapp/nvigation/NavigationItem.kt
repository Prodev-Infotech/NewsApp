package org.kotlin.multiplatform.newsapp.nvigation

import androidx.compose.runtime.Composable

data class NavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: @Composable () -> Unit,
    val unSelectedIcon: @Composable () -> Unit,
)