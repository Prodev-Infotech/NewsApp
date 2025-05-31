package org.kotlin.multiplatform.newsapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import newskotlinproject.composeapp.generated.resources.Res
import newskotlinproject.composeapp.generated.resources.ic_backarrow
import org.jetbrains.compose.resources.painterResource
import org.kotlin.multiplatform.newsapp.model.ChangePasswordRequest
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    userViewModel: UserViewModel,
    navController: NavController
) {
    val changePasswordState = userViewModel.changePasswordState.value

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                        text = "Change PassWord",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF757575)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_backarrow),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = oldPassword,
                onValueChange = {
                    oldPassword = it
                    oldPasswordError = null
                },
                label = { Text("Old Password") },
                isError = oldPasswordError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 15.dp)
            )
            if (oldPasswordError != null) {
                Text(text = oldPasswordError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    newPasswordError = null
                },
                label = { Text("New Password") },
                isError = newPasswordError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 15.dp)
            )
            if (newPasswordError != null) {
                Text(text = newPasswordError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                label = { Text("Confirm Password") },
                isError = confirmPasswordError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 15.dp)
            )
            if (confirmPasswordError != null) {
                Text(text = confirmPasswordError ?: "", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (oldPassword.isBlank()) {
                        oldPasswordError = "Old password is required"
                        isValid = false
                    }
                    if (newPassword.isBlank()) {
                        newPasswordError = "New password is required"
                        isValid = false
                    }
                    if (confirmPassword.isBlank()) {
                        confirmPasswordError = "Please confirm new password"
                        isValid = false
                    } else if (newPassword != confirmPassword) {
                        confirmPasswordError = "Passwords do not match"
                        isValid = false
                    }

                    if (isValid) {
                        val userId = SessionUtil.getUserId()
                        val changePasswordRequest = userId?.let {
                            ChangePasswordRequest(
                                userId = it,
                                oldPassword = oldPassword,
                                newPassword = newPassword


                            )
                        }
                        if (changePasswordRequest != null) {
                            userViewModel.changePassword(changePasswordRequest)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 15.dp),
                enabled = changePasswordState !is ResultState.Loading
            ) {
                Text("Change Password")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (changePasswordState) {
                is ResultState.Error -> {
                    Text(
                        text = changePasswordState.exception,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is ResultState.Success -> {
                    LaunchedEffect(Unit) {
                        navController.navigateUp()
                    }
                }

                else -> {}
            }
        }
    }
}