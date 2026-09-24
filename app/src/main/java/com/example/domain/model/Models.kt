package com.example.domain.model

enum class UserRole { STUDENT, PARENT, LEARNER, INSTITUTION }

data class UserProfile(
    val id: String = "user_default",
    val name: String = "Pawan Kanyal",
    val email: String = "pawankanyal1@gmail.com",
    val mobile: String = "+91 98765 43210",
    val dob: String = "2004-08-15",
    val classLevel: String = "Class 12 / High School",
    val targetCareer: String = "Software Architecture & AI",
    val avatarUrl: String = "",
    val isLoggedIn: Boolean = true,
    val journeyProgress: Int = 42
)

// 7D Career Lab Models
enum class QuestionType { SINGLE_CHOICE, MULTI_CHOICE, RATING, REFLECTION }

data class CareerLabQuestion(
    val id: String,
    val prompt: String,
    val type: QuestionType,
    val options: List<String> = emptyList(),
    val reflectionPlaceholder: String = ""
)

data class CareerLabDay(
    val dayNumber: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val isCompleted: Boolean,
    val isUnlocked: Boolean,
    val activitiesCount: Int = 3,
    val questions: List<CareerLabQuestion> = emptyList()
)

data class CareerDiscoverySummary(
    val completedDays: Int = 7,
    val topInterests: List<String>,
    val keyStrengths: List<String>,
    val preferredWorkStyle: String,
    val coreSkillAreas: List<String>,
    val recommendedCareerFields: List<String>,
    val learningRecommendations: List<String>,
    val generatedAt: String = "September 2026"
)

// Handwriting Models
data class HandwritingObservation(
    val categoryName: String,
    val observation: String,
    val possibleInterpretation: String,
    val reflectionQuestion: String,
    val traitIndicator: String
)

data class HandwritingReport(
    val id: String,
    val sampleUri: String,
    val dateAnalyzed: String,
    val observations: List<HandwritingObservation>,
    val summaryInsight: String
)

// Interactive Games
enum class GameType(val displayName: String, val description: String) {
    MEMORY_MATCH("Memory Match", "Challenge your memory & recall speed with career tools"),
    PATTERN_DETECTIVE("Pattern Detective", "Identify logical progressions and next sequences"),
    FOCUS_CHALLENGE("Focus Challenge", "Test rapid cognitive control under shifting rules"),
    LOGIC_SPRINT("Logic Sprint", "Solve fast-paced analytical & situational logic problems"),
    CAREER_QUEST("Career Quest", "Navigate real-world workplace dilemmas and discover traits")
}

data class GameRecord(
    val id: Long = 0L,
    val gameType: GameType,
    val score: Int,
    val accuracy: Int,
    val timeSeconds: Int,
    val level: Int,
    val playedAt: Long = System.currentTimeMillis()
)

// Career Explorer Models
data class CareerItem(
    val id: String,
    val title: String,
    val category: String,
    val shortDescription: String,
    val overview: String,
    val whatYouDo: List<String>,
    val requiredSkills: List<String>,
    val educationPathway: String,
    val typicalSalaryRange: String,
    val typicalWorkEnvironment: String,
    val keyTools: List<String>,
    val careerRoadmap: List<String>,
    val learningPath: List<String>,
    val relatedCareerTitles: List<String>,
    val isBookmarked: Boolean = false
)

data class CareerCategory(
    val id: String,
    val name: String,
    val description: String,
    val careerCount: Int,
    val iconName: String
)

// Learning & LMS Models
enum class LessonType { VIDEO, READING, QUIZ, ASSIGNMENT }

data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val durationMinutes: Int,
    val type: LessonType,
    val content: String,
    val resources: List<String> = emptyList(),
    val isCompleted: Boolean = false,
    val userNote: String = ""
)

data class CourseItem(
    val id: String,
    val title: String,
    val instructor: String,
    val durationHours: String,
    val level: String, // Beginner, Intermediate, Advanced
    val category: String,
    val progressPercent: Int = 0,
    val totalLessons: Int = 8,
    val completedLessons: Int = 0,
    val description: String,
    val skillsCovered: List<String>,
    val isSaved: Boolean = false,
    val isEnrolled: Boolean = false
)

// Quiz Engine Models
data class QuizOption(
    val label: String,
    val text: String
)

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<QuizOption>,
    val correctIndex: Int,
    val explanation: String
)

data class Quiz(
    val id: String,
    val courseId: String,
    val title: String,
    val timeLimitSeconds: Int = 180,
    val questions: List<QuizQuestion>
)

data class QuizResult(
    val quizId: String,
    val score: Int,
    val totalQuestions: Int,
    val accuracy: Int,
    val isPassed: Boolean,
    val recommendedNextLesson: String
)

// Notifications & Achievements
enum class NotificationCategory(val displayName: String) {
    ALL("All"),
    LEARNING("Learning"),
    CAREER("Career"),
    JOURNEY("7D Journey"),
    SYSTEM("System"),
    ACHIEVEMENTS("Achievements")
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val timeAgo: String,
    val isRead: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val isUnlocked: Boolean,
    val progressPercent: Int
)
