package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.MaargyaDao
import com.example.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        CareerLabDayEntity::class,
        GameRecordEntity::class,
        CourseProgressEntity::class,
        LessonNoteEntity::class,
        BookmarkEntity::class,
        NotificationEntity::class,
        HandwritingReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maargyaDao(): MaargyaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maargya_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.maargyaDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: MaargyaDao) {
                // Seed User
                dao.insertOrUpdateUser(
                    UserProfileEntity(
                        id = "user_default",
                        name = "Pawan Kanyal",
                        email = "pawankanyal1@gmail.com",
                        mobile = "+91 98765 43210",
                        dob = "2004-08-15",
                        classLevel = "Class 12 / Higher Secondary",
                        targetCareer = "Software Engineering & Artificial Intelligence",
                        isLoggedIn = true,
                        journeyProgress = 42
                    )
                )

                // Seed 7D Days
                val initialDays = listOf(
                    CareerLabDayEntity(
                        dayNumber = 1,
                        title = "Know Yourself",
                        subtitle = "Personality dimensions & internal motivators",
                        isCompleted = true,
                        isUnlocked = true
                    ),
                    CareerLabDayEntity(
                        dayNumber = 2,
                        title = "Interests",
                        subtitle = "Curiosity domains, passion areas & hobbies",
                        isCompleted = true,
                        isUnlocked = true
                    ),
                    CareerLabDayEntity(
                        dayNumber = 3,
                        title = "Strengths",
                        subtitle = "Cognitive, analytical & interpersonal assets",
                        isCompleted = false,
                        isUnlocked = true
                    ),
                    CareerLabDayEntity(
                        dayNumber = 4,
                        title = "Skills",
                        subtitle = "Current abilities vs future technological skills",
                        isCompleted = false,
                        isUnlocked = false
                    ),
                    CareerLabDayEntity(
                        dayNumber = 5,
                        title = "Values",
                        subtitle = "Work ethics, impact, autonomy & life goals",
                        isCompleted = false,
                        isUnlocked = false
                    ),
                    CareerLabDayEntity(
                        dayNumber = 6,
                        title = "Career Exploration",
                        subtitle = "Matching your blueprint with real-world careers",
                        isCompleted = false,
                        isUnlocked = false
                    ),
                    CareerLabDayEntity(
                        dayNumber = 7,
                        title = "Build Your Career Roadmap",
                        subtitle = "Actionable milestones, education choices & targets",
                        isCompleted = false,
                        isUnlocked = false
                    )
                )
                dao.insertCareerLabDays(initialDays)

                // Seed Notifications
                dao.insertNotifications(
                    listOf(
                        NotificationEntity(
                            id = "notif_1",
                            title = "7D Career Lab: Day 3 Unlocked!",
                            message = "Continue your journey with 'Strengths' assessment to explore your cognitive power.",
                            category = "7D Journey",
                            timeAgo = "10m ago",
                            isRead = false
                        ),
                        NotificationEntity(
                            id = "notif_2",
                            title = "New Future Skill Course Available",
                            message = "Check out 'Foundations of UI/UX & Human-Centered Design' by Google experts.",
                            category = "Learning",
                            timeAgo = "2h ago",
                            isRead = false
                        ),
                        NotificationEntity(
                            id = "notif_3",
                            title = "Badge Earned: 7D Starter",
                            message = "You completed Days 1 and 2 of the 7D Career Lab! Keep thriving.",
                            category = "Achievements",
                            timeAgo = "1d ago",
                            isRead = true
                        )
                    )
                )

                // Seed initial course progress
                dao.insertOrUpdateCourseProgress(
                    CourseProgressEntity(
                        courseId = "course_ai_intro",
                        title = "AI & Modern Software Systems",
                        completedLessons = 3,
                        totalLessons = 8,
                        progressPercent = 37,
                        isCompleted = false,
                        isSaved = true,
                        isEnrolled = true
                    )
                )
            }
        }
    }
}
