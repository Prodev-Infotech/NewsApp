package org.kotlin.multiplatform.newsapp.nvigation

import androidx.compose.material3.Icon
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_bookmark
import newskotlinproject.composeapp.generated.resources.ic_community
import newskotlinproject.composeapp.generated.resources.ic_news
import newskotlinproject.composeapp.generated.resources.ic_user
import org.jetbrains.compose.resources.painterResource

object Graph {
    const val NAVIGATION_BAR_SCREEN_GRAPH = "navigationBarScreenGraph"
}

sealed class Routes(var route: String) {
    data object News : Routes("news")
    data object BookMark : Routes("bookmark")
    data object Community : Routes("community")
    data object Profile : Routes("profile")
}


val navigationItemsLists = listOf(
    NavigationItem(
        title = "News",
        route = Routes.News.route,
        selectedIcon = { Icon(painter = painterResource(Res.drawable.ic_news), contentDescription = "News") },
        unSelectedIcon = { Icon(painter = painterResource(Res.drawable.ic_news), contentDescription = "News") },
    ),
    NavigationItem(
        title = "Community",
        route = Routes.Community.route,
        selectedIcon = { Icon(painter = painterResource(Res.drawable.ic_community), contentDescription = "Community") },
        unSelectedIcon = { Icon(painter = painterResource(Res.drawable.ic_community), contentDescription = "Community") },
    ),
    NavigationItem(
        title = "BookMark",
        route = Routes.BookMark.route,
        selectedIcon = { Icon(painter = painterResource(Res.drawable.ic_bookmark), contentDescription = "BookMark") },
        unSelectedIcon = { Icon(painter = painterResource(Res.drawable.ic_bookmark), contentDescription = "BookMark") },
    ),
    NavigationItem(
        title = "Profile",
        route = Routes.Profile.route,
        selectedIcon = { Icon(painter = painterResource(Res.drawable.ic_user), contentDescription = "Profile") },
        unSelectedIcon = { Icon(painter = painterResource(Res.drawable.ic_user), contentDescription = "Profile") },
    ),

)