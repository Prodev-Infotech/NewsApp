package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_call
import newskotlinproject.composeapp.generated.resources.ic_documntetion
import newskotlinproject.composeapp.generated.resources.ic_lock
import newskotlinproject.composeapp.generated.resources.ic_logout
import newskotlinproject.composeapp.generated.resources.ic_user
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController:NavController,
                  paddingValues: PaddingValues,
                  userViewModel: UserViewModel
){
    val userState by userViewModel.getUserState

    var userName by remember { mutableStateOf("") }
    var userProfile by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        userViewModel.getUserById(SessionUtil.getUserId().toString())
    }
    when(userState){
        is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ResultState.Success -> {
            userName = (userState as ResultState.Success).data.name
            userProfile = (userState as ResultState.Success).data.profileImage.toString()
        }

        is ResultState.Error -> {
            val error = (userState as ResultState.Error)
            println("Error:->$error")
        }

        else -> {}
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF757575)
                )
            )
            // Top profile section
            ProfileSection(navController,userName,userProfile)

            Spacer(modifier = Modifier.height(16.dp))

            // Settings options
            val settingsItems = listOf(
                Triple("Change Password", Res.drawable.ic_lock, Color(0xFF3B66FF)),
                Triple("Contact us", Res.drawable.ic_call, Color(0xFF2CDEDB)),
                Triple("Terms & Conditions", Res.drawable.ic_documntetion, Color(0xFFB96FF1)),
                Triple("Logout", Res.drawable.ic_logout, Color(0xFFF05A4F))
            )

            settingsItems.forEach { (title, icon, bgColor) ->
                SettingsItem(title = title, icon = icon, bgColor = bgColor,navController)
            }
        }
    }
}
@Composable
fun ProfileSection(navController:NavController,useName:String,userProfile:String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Image(
//            painter = rememberAsyncImagePainter("https://randomuser.me/api/portraits/women/1.jpg"),
            painter = painterResource(Res.drawable.ic_user),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Name and Edit Account
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

                Text(
                    text = useName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

            Text(
                text = "Edit Account",
                color = Color(0xFF3B66FF),
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    navController.navigate("editProfile")
                }
            )
        }
    }
}
@Composable
fun SettingsItem(title: String, icon: DrawableResource, bgColor: Color, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    SessionUtil.logout()
                    showDialog = false
                    navController.navigate("Login") {
//                            popUpTo("home") { inclusive = true }
                        popUpTo("main") { inclusive = true }
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable { if(title.equals("Logout")){
                showDialog=true
            }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(bgColor.copy(alpha = 0.15f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = title,
                colorFilter = ColorFilter.tint(bgColor),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray
        )
    }

}