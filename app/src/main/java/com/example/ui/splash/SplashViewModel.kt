package com.example.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.UserRole
import com.example.core.datastore.DataStoreManager
import com.example.domain.repository.AuthRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SplashDestination {
    object Loading : SplashDestination()
    data class Navigate(val route: String) : SplashDestination()
}

class SplashViewModel(
    private val dataStoreManager: DataStoreManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        decideNextDestination()
    }

    private fun decideNextDestination() {
        viewModelScope.launch {
            // Splash branding animation delay for visual branding transition
            delay(1000)

            val prefs = dataStoreManager.preferencesFlow.first()
            val isOnboardingCompleted = prefs[DataStoreManager.KEY_ONBOARDING_COMPLETED] ?: false
            if (!isOnboardingCompleted) {
                _destination.value = SplashDestination.Navigate(Screen.Onboarding.route)
                return@launch
            }

            val isLoggedIn = prefs[DataStoreManager.KEY_IS_LOGGED_IN] ?: false
            if (!isLoggedIn) {
                _destination.value = SplashDestination.Navigate(Screen.Welcome.route)
                return@launch
            }

            val isEmailVerified = prefs[DataStoreManager.KEY_EMAIL_VERIFIED] ?: false
            if (!isEmailVerified) {
                _destination.value = SplashDestination.Navigate(Screen.EmailVerification.route)
                return@launch
            }

            val savedRole = UserRole.fromString(prefs[DataStoreManager.KEY_USER_ROLE])
            val targetRoute = when (savedRole) {
                UserRole.STUDENT -> Screen.StudentDashboard.route
                UserRole.TEACHER -> Screen.TeacherDashboard.route
                UserRole.ADMIN -> Screen.AdminDashboard.route
            }

            _destination.value = SplashDestination.Navigate(targetRoute)
        }
    }
}
