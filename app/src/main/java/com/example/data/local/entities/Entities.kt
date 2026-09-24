package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "user_default",
    val name: String,
    val email: String,
    val mobile: String,
    val dob: String,
    val classLevel: String,
    val targetCareer: String,
    val isLoggedIn: Boolean,
    val journeyProgress: Int
)

@Entity(tableName = "career_lab_days")
data class CareerLabDayEntity(
    @PrimaryKey val dayNumber: Int,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean,
    val isUnlocked: Boolean,
    val answersJson: String = "{}",
    val completedAt: Long = 0L
)

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val gameType: String,
    val score: Int,
    val accuracy: Int,
    val timeSeconds: Int,
    val level: Int,
    val playedAt: Long
)

@Entity(tableName = "course_progress")
data class CourseProgressEntity(
    @PrimaryKey val courseId: String,
    val title: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val progressPercent: Int,
    val isCompleted: Boolean,
    val isSaved: Boolean,
    val isEnrolled: Boolean
)

@Entity(tableName = "lesson_notes")
data class LessonNoteEntity(
    @PrimaryKey val lessonId: String,
    val courseId: String,
    val noteContent: String,
    val isCompleted: Boolean,
    val updatedAt: Long
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String, // e.g., "career_software_dev"
    val itemType: String, // "CAREER" or "COURSE"
    val itemId: String,
    val title: String,
    val subtitle: String,
    val savedAt: Long
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val category: String,
    val timeAgo: String,
    val isRead: Boolean
)

@Entity(tableName = "handwriting_reports")
data class HandwritingReportEntity(
    @PrimaryKey val id: String,
    val sampleUri: String,
    val dateAnalyzed: String,
    val observationsJson: String,
    val summaryInsight: String
)
