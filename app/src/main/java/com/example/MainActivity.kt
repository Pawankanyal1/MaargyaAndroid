package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.domain.model.GameType
import com.example.ui.components.MaargyaBottomBar
import com.example.ui.components.OfflineBanner
import com.example.ui.navigation.BottomNavItem
import com.example.ui.navigation.MaargyaDestinations
import com.example.ui.screens.achievements.AchievementsScreen
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.RegisterScreen
import com.example.ui.screens.career.CareerDetailScreen
import com.example.ui.screens.career.CareerExplorerScreen
import com.example.ui.screens.careerlab.CareerLabDayDetailScreen
import com.example.ui.screens.careerlab.CareerLabScreen
import com.example.ui.screens.careerlab.CareerLabSummaryScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.games.*
import com.example.ui.screens.handwriting.HandwritingScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.learn.*
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.EditProfileScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.MaargyaTheme
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as MaargyaApplication).container
        val viewModelFactory = MaargyaViewModelFactory(appContainer)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
            val settingsState by settingsViewModel.uiState.collectAsState()

            val isDark = when (settingsState.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MaargyaTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Determine if bottom bar should be displayed
                val topLevelRoutes = listOf(
                    MaargyaDestinations.HOME,
                    MaargyaDestinations.DISCOVER,
                    MaargyaDestinations.GAMES,
                    MaargyaDestinations.LEARN,
                    MaargyaDestinations.PROFILE
                )
                val showBottomBar = currentRoute in topLevelRoutes

                // Shared ViewModels for navigation graph
                val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                val careerLabViewModel: CareerLabViewModel = viewModel(factory = viewModelFactory)
                val handwritingViewModel: HandwritingViewModel = viewModel(factory = viewModelFactory)
                val gamesViewModel: GamesViewModel = viewModel(factory = viewModelFactory)
                val careerViewModel: CareerViewModel = viewModel(factory = viewModelFactory)
                val courseViewModel: CourseViewModel = viewModel(factory = viewModelFactory)
                val quizViewModel: QuizViewModel = viewModel(factory = viewModelFactory)
                val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
                val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            MaargyaBottomBar(
                                currentRoute = currentRoute,
                                onNavigate = { destination: String ->
                                    navController.navigate(destination) {
                                        popUpTo(MaargyaDestinations.HOME) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        OfflineBanner(isOnline = settingsState.isOnline)

                        NavHost(
                            navController = navController,
                            startDestination = MaargyaDestinations.SPLASH,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 1. Splash Screen
                            composable(MaargyaDestinations.SPLASH) {
                                SplashScreen(
                                    onSplashFinished = {
                                        navController.navigate(MaargyaDestinations.ONBOARDING) {
                                            popUpTo(MaargyaDestinations.SPLASH) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 2. Onboarding Screen
                            composable(MaargyaDestinations.ONBOARDING) {
                                OnboardingScreen(
                                    onFinishOnboarding = {
                                        navController.navigate(MaargyaDestinations.HOME) {
                                            popUpTo(MaargyaDestinations.ONBOARDING) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // 3. Auth Screens
                            composable(MaargyaDestinations.LOGIN) {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onLoginSuccess = {
                                        navController.navigate(MaargyaDestinations.HOME) {
                                            popUpTo(MaargyaDestinations.LOGIN) { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate(MaargyaDestinations.REGISTER)
                                    },
                                    onNavigateToForgotPassword = {
                                        navController.navigate(MaargyaDestinations.FORGOT_PASSWORD)
                                    }
                                )
                            }

                            composable(MaargyaDestinations.REGISTER) {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    onRegisterSuccess = {
                                        navController.navigate(MaargyaDestinations.HOME) {
                                            popUpTo(MaargyaDestinations.LOGIN) { inclusive = true }
                                        }
                                    },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(MaargyaDestinations.FORGOT_PASSWORD) {
                                ForgotPasswordScreen(
                                    viewModel = authViewModel,
                                    onResetSuccess = {
                                        navController.navigate(MaargyaDestinations.LOGIN) {
                                            popUpTo(MaargyaDestinations.FORGOT_PASSWORD) { inclusive = true }
                                        }
                                    },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // 4. Primary Home Screen
                            composable(MaargyaDestinations.HOME) {
                                HomeScreen(
                                    viewModel = homeViewModel,
                                    onNavigateTo7DLab = {
                                        navController.navigate(MaargyaDestinations.CAREER_LAB)
                                    },
                                    onNavigateTo7DDay = { dayNum ->
                                        navController.navigate("career_lab_day/$dayNum")
                                    },
                                    onNavigateToHandwriting = {
                                        navController.navigate(MaargyaDestinations.HANDWRITING)
                                    },
                                    onNavigateToGames = {
                                        navController.navigate(MaargyaDestinations.GAMES)
                                    },
                                    onNavigateToLearn = {
                                        navController.navigate(MaargyaDestinations.LEARN)
                                    },
                                    onNavigateToCareerDetail = { careerId ->
                                        navController.navigate("career_detail/$careerId")
                                    },
                                    onNavigateToCourseDetail = { courseId ->
                                        navController.navigate("course_detail/$courseId")
                                    },
                                    onNavigateToNotifications = {
                                        navController.navigate(MaargyaDestinations.NOTIFICATIONS)
                                    },
                                    onNavigateToSearch = {
                                        navController.navigate(MaargyaDestinations.GLOBAL_SEARCH)
                                    }
                                )
                            }

                            // 5. 7D Career Lab Screens
                            composable(MaargyaDestinations.CAREER_LAB) {
                                CareerLabScreen(
                                    viewModel = careerLabViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onDayClick = { dayNumber ->
                                        navController.navigate("career_lab_day/$dayNumber")
                                    },
                                    onViewSummaryClick = {
                                        navController.navigate(MaargyaDestinations.CAREER_LAB_SUMMARY)
                                    }
                                )
                            }

                            composable(
                                route = MaargyaDestinations.CAREER_LAB_DAY,
                                arguments = listOf(navArgument("dayNumber") { type = NavType.IntType })
                            ) { backStackEntry ->
                                val dayNum = backStackEntry.arguments?.getInt("dayNumber") ?: 1
                                CareerLabDayDetailScreen(
                                    dayNumber = dayNum,
                                    viewModel = careerLabViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onDayCompleted = {
                                        homeViewModel.refreshHome()
                                        if (dayNum < 7) {
                                            navController.popBackStack()
                                        } else {
                                            navController.navigate(MaargyaDestinations.CAREER_LAB_SUMMARY) {
                                                popUpTo(MaargyaDestinations.CAREER_LAB)
                                            }
                                        }
                                    }
                                )
                            }

                            composable(MaargyaDestinations.CAREER_LAB_SUMMARY) {
                                CareerLabSummaryScreen(
                                    viewModel = careerLabViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onExploreCareersClick = {
                                        navController.navigate(MaargyaDestinations.CAREER_EXPLORER)
                                    },
                                    onExploreCoursesClick = {
                                        navController.navigate(MaargyaDestinations.LEARN)
                                    }
                                )
                            }

                            // 6. Handwriting Analysis Screen
                            composable(MaargyaDestinations.HANDWRITING) {
                                HandwritingScreen(
                                    viewModel = handwritingViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onContinueJourney = {
                                        homeViewModel.refreshHome()
                                        navController.navigate(MaargyaDestinations.CAREER_LAB)
                                    }
                                )
                            }

                            // 7. Cognitive Games Screens
                            composable(MaargyaDestinations.GAMES) {
                                GamesDashboardScreen(
                                    viewModel = gamesViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onPlayGame = { gameType ->
                                        when (gameType) {
                                            GameType.MEMORY_MATCH -> navController.navigate(MaargyaDestinations.GAME_MEMORY_MATCH)
                                            GameType.PATTERN_DETECTIVE -> navController.navigate(MaargyaDestinations.GAME_PATTERN_DETECTIVE)
                                            GameType.FOCUS_CHALLENGE -> navController.navigate(MaargyaDestinations.GAME_FOCUS_CHALLENGE)
                                            GameType.LOGIC_SPRINT -> navController.navigate(MaargyaDestinations.GAME_LOGIC_SPRINT)
                                            GameType.CAREER_QUEST -> navController.navigate(MaargyaDestinations.GAME_CAREER_QUEST)
                                        }
                                    }
                                )
                            }

                            composable(MaargyaDestinations.GAME_MEMORY_MATCH) {
                                MemoryMatchScreen(
                                    viewModel = gamesViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onGameCompleted = { score, acc, time ->
                                        navController.navigate("game_results/MemoryMatch/$score/$acc/$time")
                                    }
                                )
                            }

                            composable(MaargyaDestinations.GAME_PATTERN_DETECTIVE) {
                                PatternDetectiveScreen(
                                    viewModel = gamesViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onGameCompleted = { score, acc, time ->
                                        navController.navigate("game_results/PatternDetective/$score/$acc/$time")
                                    }
                                )
                            }

                            composable(MaargyaDestinations.GAME_FOCUS_CHALLENGE) {
                                FocusChallengeScreen(
                                    viewModel = gamesViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onGameCompleted = { score, acc, time ->
                                        navController.navigate("game_results/FocusChallenge/$score/$acc/$time")
                                    }
                                )
                            }

                            composable(MaargyaDestinations.GAME_LOGIC_SPRINT) {
                                LogicSprintScreen(
                                    viewModel = gamesViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onGameCompleted = { score, acc, time ->
                                        navController.navigate("game_results/LogicSprint/$score/$acc/$time")
                                    }
                                )
                            }

                            composable(MaargyaDestinations.GAME_CAREER_QUEST) {
                                CareerQuestScreen(
                                    viewModel = gamesViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onGameCompleted = { score, acc, time ->
                                        navController.navigate("game_results/CareerQuest/$score/$acc/$time")
                                    }
                                )
                            }

                            composable(
                                route = MaargyaDestinations.GAME_RESULTS,
                                arguments = listOf(
                                    navArgument("gameType") { type = NavType.StringType },
                                    navArgument("score") { type = NavType.IntType },
                                    navArgument("accuracy") { type = NavType.IntType },
                                    navArgument("timeSeconds") { type = NavType.IntType }
                                )
                            ) { backStack ->
                                val score = backStack.arguments?.getInt("score") ?: 0
                                val acc = backStack.arguments?.getInt("accuracy") ?: 0
                                val time = backStack.arguments?.getInt("timeSeconds") ?: 0
                                GameResultsScreen(
                                    score = score,
                                    accuracy = acc,
                                    timeSeconds = time,
                                    onNavigateBack = {
                                        navController.popBackStack(MaargyaDestinations.GAMES, false)
                                    },
                                    onPlayAgain = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // 8. Discover Screen
                            composable(MaargyaDestinations.DISCOVER) {
                                DiscoverScreen(
                                    viewModel = careerViewModel,
                                    onNavigateToSearch = {
                                        navController.navigate(MaargyaDestinations.GLOBAL_SEARCH)
                                    },
                                    onCategoryClick = { category ->
                                        navController.navigate("career_explorer?category=$category")
                                    },
                                    onCareerClick = { careerId ->
                                        navController.navigate("career_detail/$careerId")
                                    }
                                )
                            }

                            // 9. Career Explorer & Details
                            composable(
                                route = MaargyaDestinations.CAREER_EXPLORER,
                                arguments = listOf(navArgument("category") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                })
                            ) { backStackEntry ->
                                val cat = backStackEntry.arguments?.getString("category")
                                LaunchedEffect(cat) {
                                    if (cat != null) {
                                        careerViewModel.selectCategory(cat)
                                    }
                                }
                                CareerExplorerScreen(
                                    viewModel = careerViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onCareerClick = { careerId ->
                                        navController.navigate("career_detail/$careerId")
                                    }
                                )
                            }

                            composable(
                                route = MaargyaDestinations.CAREER_DETAIL,
                                arguments = listOf(navArgument("careerId") { type = NavType.StringType })
                            ) { backStack ->
                                val careerId = backStack.arguments?.getString("careerId") ?: ""
                                CareerDetailScreen(
                                    careerId = careerId,
                                    viewModel = careerViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToCourse = { courseId ->
                                        navController.navigate("course_detail/$courseId")
                                    }
                                )
                            }

                            // 10. Learn (LMS) & Player Screens
                            composable(MaargyaDestinations.LEARN) {
                                LearnScreen(
                                    viewModel = courseViewModel,
                                    onCourseClick = { courseId ->
                                        navController.navigate("course_detail/$courseId")
                                    }
                                )
                            }

                            composable(
                                route = MaargyaDestinations.COURSE_DETAIL,
                                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                            ) { backStack ->
                                val courseId = backStack.arguments?.getString("courseId") ?: ""
                                CourseDetailScreen(
                                    courseId = courseId,
                                    viewModel = courseViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToLesson = { cId, lId ->
                                        navController.navigate("course_player/$cId/$lId")
                                    },
                                    onNavigateToQuiz = { cId ->
                                        navController.navigate("quiz/$cId")
                                    }
                                )
                            }

                            composable(
                                route = MaargyaDestinations.COURSE_PLAYER,
                                arguments = listOf(
                                    navArgument("courseId") { type = NavType.StringType },
                                    navArgument("lessonId") { type = NavType.StringType }
                                )
                            ) { backStack ->
                                val courseId = backStack.arguments?.getString("courseId") ?: ""
                                val lessonId = backStack.arguments?.getString("lessonId") ?: ""
                                CoursePlayerScreen(
                                    courseId = courseId,
                                    lessonId = lessonId,
                                    viewModel = courseViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onQuizClick = {
                                        navController.navigate("quiz/$courseId")
                                    }
                                )
                            }

                            composable(
                                route = MaargyaDestinations.QUIZ,
                                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                            ) { backStack ->
                                val courseId = backStack.arguments?.getString("courseId") ?: ""
                                QuizScreen(
                                    courseId = courseId,
                                    viewModel = quizViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onQuizSubmitted = {
                                        navController.navigate("quiz_result/$courseId")
                                    }
                                )
                            }

                            composable(
                                route = "quiz_result/{courseId}",
                                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                            ) { backStack ->
                                val courseId = backStack.arguments?.getString("courseId") ?: ""
                                QuizResultScreen(
                                    viewModel = quizViewModel,
                                    onNavigateBack = {
                                        navController.popBackStack("course_detail/$courseId", false)
                                    },
                                    onRetakeQuiz = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // 11. Profile & Settings Screens
                            composable(MaargyaDestinations.PROFILE) {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onNavigateToEditProfile = {
                                        navController.navigate(MaargyaDestinations.EDIT_PROFILE)
                                    },
                                    onNavigateToAchievements = {
                                        navController.navigate(MaargyaDestinations.ACHIEVEMENTS)
                                    },
                                    onNavigateToSettings = {
                                        navController.navigate(MaargyaDestinations.SETTINGS)
                                    },
                                    onNavigateToCareerDetail = { careerId ->
                                        navController.navigate("career_detail/$careerId")
                                    },
                                    onNavigateToCourseDetail = { courseId ->
                                        navController.navigate("course_detail/$courseId")
                                    },
                                    onLogout = {
                                        authViewModel.logout {
                                            navController.navigate(MaargyaDestinations.LOGIN) {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }

                            composable(MaargyaDestinations.EDIT_PROFILE) {
                                EditProfileScreen(
                                    viewModel = profileViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(MaargyaDestinations.NOTIFICATIONS) {
                                NotificationsScreen(
                                    viewModel = notificationViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(MaargyaDestinations.ACHIEVEMENTS) {
                                AchievementsScreen(
                                    viewModel = profileViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(MaargyaDestinations.SETTINGS) {
                                SettingsScreen(
                                    viewModel = settingsViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // 12. Global Search Screen
                            composable(MaargyaDestinations.GLOBAL_SEARCH) {
                                GlobalSearchScreen(
                                    careerViewModel = careerViewModel,
                                    courseViewModel = courseViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onCareerClick = { careerId ->
                                        navController.navigate("career_detail/$careerId")
                                    },
                                    onCourseClick = { courseId ->
                                        navController.navigate("course_detail/$courseId")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

