package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppContainer

class MaargyaViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    container.authRepository,
                    container.careerLabRepository,
                    container.careerRepository,
                    container.courseRepository,
                    container.notificationRepository
                ) as T
            }
            modelClass.isAssignableFrom(CareerLabViewModel::class.java) -> {
                CareerLabViewModel(container.careerLabRepository) as T
            }
            modelClass.isAssignableFrom(HandwritingViewModel::class.java) -> {
                HandwritingViewModel(container.handwritingRepository) as T
            }
            modelClass.isAssignableFrom(GamesViewModel::class.java) -> {
                GamesViewModel(container.gamesRepository) as T
            }
            modelClass.isAssignableFrom(CareerViewModel::class.java) -> {
                CareerViewModel(container.careerRepository) as T
            }
            modelClass.isAssignableFrom(CourseViewModel::class.java) -> {
                CourseViewModel(container.courseRepository) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(container.courseRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    container.authRepository,
                    container.careerRepository,
                    container.courseRepository,
                    container.notificationRepository
                ) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(container.notificationRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(container.authRepository, container.userPreferences) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(container.userPreferences, container.networkMonitor) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
