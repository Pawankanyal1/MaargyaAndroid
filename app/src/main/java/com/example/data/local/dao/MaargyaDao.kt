package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MaargyaDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    fun getUserProfile(userId: String = "user_default"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfileEntity)

    // 7D Career Lab
    @Query("SELECT * FROM career_lab_days ORDER BY dayNumber ASC")
    fun getAllCareerLabDays(): Flow<List<CareerLabDayEntity>>

    @Query("SELECT * FROM career_lab_days WHERE dayNumber = :dayNumber LIMIT 1")
    suspend fun getCareerLabDay(dayNumber: Int): CareerLabDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerLabDays(days: List<CareerLabDayEntity>)

    @Query("UPDATE career_lab_days SET isCompleted = 1, answersJson = :answersJson, completedAt = :timestamp WHERE dayNumber = :dayNumber")
    suspend fun markDayCompleted(dayNumber: Int, answersJson: String, timestamp: Long)

    @Query("UPDATE career_lab_days SET isUnlocked = 1 WHERE dayNumber = :dayNumber")
    suspend fun unlockDay(dayNumber: Int)

    // Game Records
    @Query("SELECT * FROM game_records ORDER BY playedAt DESC")
    fun getAllGameRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records WHERE gameType = :gameType ORDER BY score DESC LIMIT 1")
    suspend fun getBestScoreForGame(gameType: String): GameRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameRecord(record: GameRecordEntity)

    // Course Progress
    @Query("SELECT * FROM course_progress")
    fun getAllCourseProgress(): Flow<List<CourseProgressEntity>>

    @Query("SELECT * FROM course_progress WHERE courseId = :courseId LIMIT 1")
    fun getCourseProgress(courseId: String): Flow<CourseProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCourseProgress(progress: CourseProgressEntity)

    // Lesson Notes
    @Query("SELECT * FROM lesson_notes WHERE lessonId = :lessonId LIMIT 1")
    fun getLessonNote(lessonId: String): Flow<LessonNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLessonNote(note: LessonNoteEntity)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE itemId = :itemId)")
    fun isItemBookmarked(itemId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE itemId = :itemId")
    suspend fun removeBookmark(itemId: String)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // Handwriting Reports
    @Query("SELECT * FROM handwriting_reports ORDER BY id DESC")
    fun getAllHandwritingReports(): Flow<List<HandwritingReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHandwritingReport(report: HandwritingReportEntity)
}
