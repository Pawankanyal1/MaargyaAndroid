package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.example.data.repository.CareerRepository
import com.example.data.repository.CourseRepository
import com.example.data.repository.NotificationRepository
import com.example.domain.model.Achievement
import com.example.domain.model.CareerItem
import com.example.domain.model.CourseItem
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserProfile = UserProfile(),
    val achievements: List<Achievement> = emptyList(),
    val savedCareers: List<CareerItem> = emptyList(),
    val enrolledCourses: List<CourseItem> = emptyList(),
    val isEditing: Boolean = false,
    val isUpdatedSuccess: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val careerRepository: CareerRepository,
    private val courseRepository: CourseRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            combine(
                authRepository.currentUser,
                notificationRepository.achievements,
                careerRepository.bookmarkedCareers,
                courseRepository.myCourses
            ) { user, achs, bookmarks, courses ->
                ProfileUiState(
                    user = user ?: UserProfile(),
                    achievements = achs,
                    savedCareers = bookmarks,
                    enrolledCourses = courses
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateProfile(name: String, email: String, mobile: String, classLevel: String, targetCareer: String) {
        viewModelScope.launch {
            val updated = _uiState.value.user.copy(
                name = name,
                email = email,
                mobile = mobile,
                classLevel = classLevel,
                targetCareer = targetCareer
            )
            authRepository.updateUserProfile(updated)
            _uiState.value = _uiState.value.copy(
                user = updated,
                isEditing = false,
                isUpdatedSuccess = true
            )
        }
    }
}
