package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.*
import com.example.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: UserProfile = UserProfile(),
    val journeyProgress: Int = 42,
    val labDays: List<CareerLabDay> = emptyList(),
    val currentLabDay: CareerLabDay? = null,
    val recommendedCareers: List<CareerItem> = emptyList(),
    val continueCourse: CourseItem? = null,
    val unreadNotificationsCount: Int = 2,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val careerLabRepository: CareerLabRepository,
    private val careerRepository: CareerRepository,
    private val courseRepository: CourseRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                authRepository.currentUser,
                careerLabRepository.labDays,
                careerRepository.allCareers,
                courseRepository.allCourses,
                notificationRepository.notifications
            ) { user, days, careers, courses, notifs ->
                val currentDay = days.find { it.isUnlocked && !it.isCompleted } ?: days.firstOrNull()
                val continueCourse = courses.find { it.isEnrolled && it.progressPercent in 1..99 } ?: courses.firstOrNull()
                val unreadNotifs = notifs.count { !it.isRead }
                HomeUiState(
                    user = user ?: UserProfile(),
                    journeyProgress = user?.journeyProgress ?: 42,
                    labDays = days,
                    currentLabDay = currentDay,
                    recommendedCareers = careers.take(4),
                    continueCourse = continueCourse,
                    unreadNotificationsCount = unreadNotifs,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun refreshHome() {
        loadDashboardData()
    }
}
