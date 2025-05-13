package org.kotlin.multiplatform.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.kotlin.multiplatform.newsapp.model.LoginRequest
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.SignUpRequest
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.network.KtorfitServiceCreator
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.baseUrl

class UserViewModel : ViewModel() {

    private val _loginState = mutableStateOf<ResultState<User>>(ResultState.Initial)
    val loginState: State<ResultState<User>> get() = _loginState

    private val _signUpState = mutableStateOf<ResultState<User>>(ResultState.Initial)
    val signUpState: State<ResultState<User>> get() = _signUpState

    private val ktorfitService = KtorfitServiceCreator(baseUrl)

    // Function for handling signup
    fun signUp(request: SignUpRequest) {
        _signUpState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.signUp(request)
                if (response.status) {
                    val user = response.data
                    if (user != null) {
                        _signUpState.value = ResultState.Success(user)
                        SessionUtil.saveUserId(user.id)
                    } else {
                        _signUpState.value = ResultState.Error("User data is null")
                    }
                } else {
                    _signUpState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _signUpState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Function for handling login
    fun login(request: LoginRequest) {
        _loginState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.login(request)
                if (response.status) {
                    val user = response.data
                    if (user != null) {
                        _loginState.value = ResultState.Success(user)
                        SessionUtil.saveUserId(user.id)

                    } else {
                        _loginState.value = ResultState.Error("User data is null")
                    }
                } else {
                    _loginState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _loginState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
}