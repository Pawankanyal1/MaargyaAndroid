package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.repository.NotificationRepository
import com.example.domain.model.Achievement
import com.example.domain.model.NotificationCategory
import com.example.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(private val database: AppDatabase) : NotificationRepository {

    private val staticAchievements = listOf(
        Achievement(
            id = "ach_7d_starter",
            title = "7D Starter",
            description = "Completed the first 2 self-discovery milestones in the 7D Career Lab.",
            badgeIcon = "Explore",
            isUnlocked = true,
            progressPercent = 100
        ),
        Achievement(
            id = "ach_career_explorer",
            title = "Career Explorer",
            description = "Explored 5 or more detailed career roadmaps and saved prospective paths.",
            badgeIcon = "Work",
            isUnlocked = true,
            progressPercent = 100
        ),
        Achievement(
            id = "ach_game_master",
            title = "Cognitive Game Master",
            description = "Achieved over 90% accuracy in Focus Challenge and Memory Match games.",
            badgeIcon = "Psychology",
            isUnlocked = false,
            progressPercent = 65
        ),
        Achievement(
            id = "ach_learning_explorer",
            title = "Learning Explorer",
            description = "Completed at least 3 multimedia lessons and took your first mastery quiz.",
            badgeIcon = "School",
            isUnlocked = true,
            progressPercent = 100
        ),
        Achievement(
            id = "ach_first_course",
            title = "First Course Complete",
            description = "Finished 100% of curriculum lessons and assignments in an enrolled course.",
            badgeIcon = "EmojiEvents",
            isUnlocked = true,
            progressPercent = 100
        ),
        Achievement(
            id = "ach_7d_complete",
            title = "7D Pathway Architect",
            description = "Successfully synthesized all 7 days of the Career Lab into an action roadmap.",
            badgeIcon = "Stars",
            isUnlocked = false,
            progressPercent = 42
        )
    )

    override val notifications: Flow<List<NotificationItem>> = database.maargyaDao().getAllNotifications()
        .map { entities ->
            if (entities.isEmpty()) {
                listOf(
                    NotificationItem(
                        id = "notif_1",
                        title = "7D Career Lab: Day 3 Unlocked!",
                        message = "Continue your journey with 'Strengths' assessment to explore your cognitive power.",
                        category = NotificationCategory.JOURNEY,
                        timeAgo = "10m ago",
                        isRead = false
                    ),
                    NotificationItem(
                        id = "notif_2",
                        title = "New Course: AI & Modern Software Systems",
                        message = "Explore the new curriculum and learn how software and AI converge.",
                        category = NotificationCategory.LEARNING,
                        timeAgo = "2h ago",
                        isRead = false
                    ),
                    NotificationItem(
                        id = "notif_3",
                        title = "Achievement Unlocked: 7D Starter",
                        message = "Congratulations on taking the first structured step towards finding your true path!",
                        category = NotificationCategory.ACHIEVEMENTS,
                        timeAgo = "1d ago",
                        isRead = true
                    ),
                    NotificationItem(
                        id = "notif_4",
                        title = "System Update: Enhanced Offline Mode",
                        message = "Maargya can now persist all your notes, progress, and assessments offline.",
                        category = NotificationCategory.SYSTEM,
                        timeAgo = "3d ago",
                        isRead = true
                    )
                )
            } else {
                entities.map { entity ->
                    val cat = when (entity.category) {
                        "7D Journey" -> NotificationCategory.JOURNEY
                        "Learning" -> NotificationCategory.LEARNING
                        "Career" -> NotificationCategory.CAREER
                        "Achievements" -> NotificationCategory.ACHIEVEMENTS
                        else -> NotificationCategory.SYSTEM
                    }
                    NotificationItem(
                        id = entity.id,
                        title = entity.title,
                        message = entity.message,
                        category = cat,
                        timeAgo = entity.timeAgo,
                        isRead = entity.isRead
                    )
                }
            }
        }

    override val achievements: Flow<List<Achievement>> = flowOf(staticAchievements)

    override suspend fun markAsRead(id: String) {
        database.maargyaDao().markNotificationAsRead(id)
    }

    override suspend fun markAllAsRead() {
        database.maargyaDao().markAllNotificationsAsRead()
    }
}
