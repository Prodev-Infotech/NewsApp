package org.kotlin.multiplatform.newsapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.kotlin.multiplatform.newsapp.model.ChangePasswordRequest
import org.kotlin.multiplatform.newsapp.model.EditProfileRequest
import org.kotlin.multiplatform.newsapp.model.LoginRequest
import org.kotlin.multiplatform.newsapp.model.ResultState
import org.kotlin.multiplatform.newsapp.model.SignUpRequest
import org.kotlin.multiplatform.newsapp.model.User
import org.kotlin.multiplatform.newsapp.model.UserResponseData
import org.kotlin.multiplatform.newsapp.network.KtorfitServiceCreator
import org.kotlin.multiplatform.newsapp.utils.SessionUtil
import org.kotlin.multiplatform.newsapp.utils.baseUrl

class UserViewModel : ViewModel() {

    private val _loginState = mutableStateOf<ResultState<User>>(ResultState.Initial)
    val loginState: State<ResultState<User>> get() = _loginState

    private val _signUpState = mutableStateOf<ResultState<User>>(ResultState.Initial)
    val signUpState: State<ResultState<User>> get() = _signUpState

    private val _getUserState = mutableStateOf<ResultState<UserResponseData>>(ResultState.Initial)
    val getUserState: State<ResultState<UserResponseData>> get() = _getUserState

    private val _updateProfileState = mutableStateOf<ResultState<User>>(ResultState.Initial)
    val updateProfileState: State<ResultState<User>> get() = _updateProfileState

    private val _changePasswordState = mutableStateOf<ResultState<Boolean>>(ResultState.Initial)
    val changePasswordState: State<ResultState<Boolean>> get() = _changePasswordState

    private val _deleteAccountState = mutableStateOf<ResultState<Boolean>>(ResultState.Initial)
    val deleteAccountState: State<ResultState<Boolean>> get() = _deleteAccountState

    private val ktorfitService = KtorfitServiceCreator(baseUrl)
    fun resetUpdateProfileState() {
        _updateProfileState.value = ResultState.Initial
    }
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
                        SessionUtil.saveUser(user)
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
                        SessionUtil.saveUser(user)

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
    fun getUserById(userId: String) {
        _getUserState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.getUserById(userId)
                if (response.status && response.data != null) {
                    _getUserState.value = ResultState.Success(response.data)
                } else {
                    _getUserState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _getUserState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun editProfile(userId: String, updatedUser: EditProfileRequest) {
        _updateProfileState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.updateUser(updatedUser, userId)
                if (response.status && response.data != null) {
                    _updateProfileState.value = ResultState.Success(response.data)
                } else {
                    _updateProfileState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _updateProfileState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun changePassword(request: ChangePasswordRequest) {
        _changePasswordState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.changePassword(request)
                if (response.status) {
                    _changePasswordState.value = ResultState.Success(true)
                } else {
                    _changePasswordState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _changePasswordState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun deleteAccount(userId: String) {
        _deleteAccountState.value = ResultState.Loading

        viewModelScope.launch {
            try {
                val response = ktorfitService.api.deleteUser(userId)
                if (response.status) {
                    _deleteAccountState.value = ResultState.Success(true)
                    SessionUtil.logout() // Optional: clear session
                } else {
                    _deleteAccountState.value = ResultState.Error(response.message)
                }
            } catch (e: Exception) {
                _deleteAccountState.value = ResultState.Error(e.message ?: "Unknown error")
            }
        }
    }


}