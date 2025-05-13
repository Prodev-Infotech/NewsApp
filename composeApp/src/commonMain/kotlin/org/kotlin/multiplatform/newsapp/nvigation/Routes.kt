package org.kotlin.multiplatform.newsapp.nvigation

import androidx.compose.material3.Icon
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_bookmark
import newskotlinproject.composeapp.generated.resources.ic_news
import org.jetbrains.compose.resources.painterResource

object Graph {
    const val NAVIGATION_BAR_SCREEN_GRAPH = "navigationBarScreenGraph"
}

sealed class Routes(var route: String) {
    data object Label : Routes("news")
    data object Notes : Routes("bookmark")
}


val navigationItemsLists = listOf(
    NavigationItem(
        title = "News",
        route = Routes.Label.route,
        selectedIcon = { Icon(painter = painterResource(Res.drawable.ic_news), contentDescription = "News") },
        unSelectedIcon = { Icon(painter = painterResource(Res.drawable.ic_news), contentDescription = "News") },
    ),
    NavigationItem(
        title = "BookMark",
        route = Routes.Notes.route,
        selectedIcon = { Icon(painter = painterResource(Res.drawable.ic_bookmark), contentDescription = "BookMark") },
        unSelectedIcon = { Icon(painter = painterResource(Res.drawable.ic_bookmark), contentDescription = "BookMark") },
    ),

)