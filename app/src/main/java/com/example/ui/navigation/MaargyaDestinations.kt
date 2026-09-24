package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Discover : BottomNavItem("discover", "Discover", Icons.Filled.CompassCalibration, Icons.Outlined.Explore)
    object Games : BottomNavItem("games", "Games", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports)
    object Learn : BottomNavItem("learn", "Learn", Icons.Filled.School, Icons.Outlined.School)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

    companion object {
        val items = listOf(Home, Discover, Games, Learn, Profile)
    }
}

object MaargyaDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"

    const val HOME = "home"
    const val DISCOVER = "discover"
    const val GAMES = "games"
    const val LEARN = "learn"
    const val PROFILE = "profile"

    const val CAREER_LAB = "career_lab"
    const val CAREER_LAB_DAY = "career_lab_day/{dayNumber}"
    const val CAREER_LAB_SUMMARY = "career_lab_summary"

    const val HANDWRITING = "handwriting"
    const val HANDWRITING_RESULTS = "handwriting_results"

    const val GAME_MEMORY_MATCH = "game_memory_match"
    const val GAME_PATTERN_DETECTIVE = "game_pattern_detective"
    const val GAME_FOCUS_CHALLENGE = "game_focus_challenge"
    const val GAME_LOGIC_SPRINT = "game_logic_sprint"
    const val GAME_CAREER_QUEST = "game_career_quest"
    const val GAME_RESULTS = "game_results/{gameType}/{score}/{accuracy}/{timeSeconds}"

    const val CAREER_EXPLORER = "career_explorer?category={category}"
    const val CAREER_DETAIL = "career_detail/{careerId}"

    const val COURSE_DETAIL = "course_detail/{courseId}"
    const val COURSE_PLAYER = "course_player/{courseId}/{lessonId}"

    const val QUIZ = "quiz/{courseId}"
    const val QUIZ_RESULT = "quiz_result/{quizId}/{score}/{total}"

    const val EDIT_PROFILE = "edit_profile"
    const val NOTIFICATIONS = "notifications"
    const val ACHIEVEMENTS = "achievements"
    const val SETTINGS = "settings"
    const val GLOBAL_SEARCH = "global_search"
}
