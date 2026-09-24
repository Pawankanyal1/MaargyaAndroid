package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.local.entities.*
import com.example.data.remote.NetworkResult
import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// 1. Auth Repository
interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    suspend fun login(identifier: String, password: String): NetworkResult<UserProfile>
    suspend fun register(fullName: String, email: String, mobile: String, dob: String, classLevel: String, password: String): NetworkResult<UserProfile>
    suspend fun forgotPassword(identifier: String): NetworkResult<String>
    suspend fun verifyOtpAndReset(otp: String, newPass: String): NetworkResult<Boolean>
    suspend fun logout(): NetworkResult<Unit>
    suspend fun updateUserProfile(profile: UserProfile): NetworkResult<UserProfile>
}

// 2. 7D Career Lab Repository
interface CareerLabRepository {
    val labDays: Flow<List<CareerLabDay>>
    suspend fun getDay(dayNumber: Int): CareerLabDay?
    suspend fun completeDay(dayNumber: Int, answers: Map<String, String>): NetworkResult<Unit>
    suspend fun getDiscoverySummary(): CareerDiscoverySummary
}

// 3. Handwriting Repository
interface HandwritingRepository {
    val savedReports: Flow<List<HandwritingReport>>
    suspend fun analyzeSample(imageUriString: String): NetworkResult<HandwritingReport>
    suspend fun saveReport(report: HandwritingReport): NetworkResult<Unit>
}

// 4. Games Repository
interface GamesRepository {
    val gameRecords: Flow<List<GameRecord>>
    fun getAvailableGames(): List<GameInfo>
    suspend fun recordGameResult(type: GameType, score: Int, accuracy: Int, timeSeconds: Int, level: Int): NetworkResult<GameRecord>
    suspend fun getBestScore(type: GameType): GameRecord?
}

// 5. Career Repository
interface CareerRepository {
    val allCareers: Flow<List<CareerItem>>
    val bookmarkedCareers: Flow<List<CareerItem>>
    fun getCategories(): List<CareerCategory>
    suspend fun getCareerById(id: String): CareerItem?
    suspend fun toggleBookmark(careerId: String, title: String, subtitle: String): Boolean
    fun isCareerBookmarked(id: String): Flow<Boolean>
    fun searchCareers(query: String, category: String? = null): List<CareerItem>
}

// 6. Course & Learning Repository
interface CourseRepository {
    val allCourses: Flow<List<CourseItem>>
    val myCourses: Flow<List<CourseItem>>
    val savedCourses: Flow<List<CourseItem>>
    suspend fun getCourseById(id: String): CourseItem?
    suspend fun getLessonsForCourse(courseId: String): List<Lesson>
    suspend fun getLesson(lessonId: String): Lesson?
    suspend fun markLessonCompleted(courseId: String, lessonId: String): Unit
    suspend fun saveLessonNote(courseId: String, lessonId: String, note: String): Unit
    suspend fun toggleSaveCourse(courseId: String): Boolean
    suspend fun getQuizForCourse(courseId: String): Quiz?
}

// 7. Notification & Achievements Repository
interface NotificationRepository {
    val notifications: Flow<List<NotificationItem>>
    val achievements: Flow<List<Achievement>>
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
}

data class GameInfo(
    val type: GameType,
    val title: String,
    val description: String,
    val difficulty: String,
    val targetSkills: List<String>
)
