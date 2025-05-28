package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_backarrow
import newskotlinproject.composeapp.generated.resources.ic_news_pl
import newskotlinproject.composeapp.generated.resources.ic_send
import newskotlinproject.composeapp.generated.resources.ic_user_profile_pl
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.CommunityWithJoinStatus
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailsScreen(
    communityId: String,
    communityViewModel: CommunityViewModel,
    navController: NavController,
) {
    val communityState by communityViewModel.singleCommunityState

LaunchedEffect(communityId){
    communityViewModel.fetchSingleCommunity(communityId,SessionUtil.getUserId().toString())
}

    // UI based on state
    when (communityState) {
        is ResultState.Initial, is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ResultState.Success -> {
            val community = (communityState as ResultState.Success).data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFECEEFF))
            ) {
                HeaderSection(community,communityViewModel,navController)
                TabsSection()
                TransactionList(
                    modifier = Modifier.weight(1f),
                    isPrivate = community.isPrivate,
                    joinStatus = community.joinStatus
                )

            }
        }

        is ResultState.Error -> {
            val message = (communityState as ResultState.Error).exception
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: $message", color = Color.Red)
            }
        }
    }

}

@Composable
fun HeaderSection(
    community: CommunityWithJoinStatus,
    communityViewModel:CommunityViewModel,
    navController: NavController
) {
    val imageResource = asyncPainterResource(community.imageUrl)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Approx 1/6 screen
    ) {

            if (imageResource is Resource.Loading || imageResource is Resource.Failure) {
                Image(
                    painter = painterResource(Res.drawable.ic_news_pl),
                    contentDescription = "Loading placeholder",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize() .blur(5.dp),
                )
            }
            KamelImage({ imageResource },
                contentDescription = "contentDescription",
                modifier = Modifier
                    .matchParentSize() .blur(5.dp),
                contentScale = ContentScale.Crop,
                onLoading = {
                    println("Loading image...")
                }, onFailure = {
                    println("Failed to load image")
                })

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_backarrow),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        // Overlay content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = community.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = community.authorName,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val isJoined = community.joinStatus == "Joined"
                val isPrivate = community.isPrivate

                val isButtonEnabled = !isPrivate || isJoined

                Button(
                    onClick = { /* Handle create community action */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isButtonEnabled) Color(0xFF1A237E) else Color.Gray
                    ),
                    enabled = isButtonEnabled
                ) {
                    Text("Create Community", color = Color.White)
                }

                Button(
                    onClick = {if(community.joinStatus.equals("Join"))
                        SessionUtil.getUser()?.let { communityViewModel.joinCommunity(it, community.id) } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Text(community.joinStatus, color = Color.White)
                }
            }
        }

    }
}

@Composable
fun TabsSection() {
    val tabs = listOf("Image", "Video", "Link")
    var selectedTab by remember { mutableStateOf(0) }

    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { selectedTab = index }
                    .padding(8.dp)
            ) {
                Text(
                    title,
                    color = if (selectedTab == index) Color(0xFF375BCE) else Color.Gray,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                )
                if (selectedTab == index) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(24.dp)
                            .background(Color(0xFF375BCE))
                    )
                }
            }
        }
    }
}

//@Composable
//fun TransactionList(modifier: Modifier = Modifier) {
//    val items = listOf(
//        Triple("Chintan Patel", "+\$225", Color(0xFF4CAF50)),
//        Triple("Jhon Dele", "-\$1125", Color(0xFFF44336)),
//        Triple("Mario Mathum", "+\$532", Color(0xFF4CAF50)),
//        Triple("Texas Patel", "-\$145", Color(0xFFF44336)),
//    )
//
//    LazyColumn(modifier = modifier.padding(top = 8.dp)) {
//        items(items) { (name, amount, color) ->
//            TransactionItem(name, amount, color)
//        }
//    }
//}

@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    isPrivate: Boolean,
    joinStatus: String
) {
    if (isPrivate && joinStatus == "Join") {
        // Show restricted message
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "This is a private community. Join to see more.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    } else {
        // Show actual list
        val items = listOf(
            Triple("Chintan Patel", "+\$225", Color(0xFF4CAF50)),
            Triple("Jhon Dele", "-\$1125", Color(0xFFF44336)),
            Triple("Mario Mathum", "+\$532", Color(0xFF4CAF50)),
            Triple("Texas Patel", "-\$145", Color(0xFFF44336)),
        )

        LazyColumn(modifier = modifier.padding(top = 8.dp)) {
            items(items) { (name, amount, color) ->
                TransactionItem(name, amount, color)
            }
        }
    }
}

@Composable
fun TransactionItem(name: String, amount: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_user_profile_pl), // replace with avatar
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.SemiBold)
            Text("Today, 12:30 PM", fontSize = 12.sp, color = Color.Gray)
        }
        Text(amount, color = color, fontWeight = FontWeight.Bold)
    }
}