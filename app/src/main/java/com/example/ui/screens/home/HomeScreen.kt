package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CareerItem
import com.example.domain.model.CourseItem
import com.example.ui.components.CareerCard
import com.example.ui.components.CourseCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateTo7DLab: () -> Unit,
    onNavigateTo7DDay: (Int) -> Unit,
    onNavigateToHandwriting: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToCareerDetail: (String) -> Unit,
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top App Bar / Greeting Area
        item {
            HomeHeader(
                userName = uiState.user.name,
                classLevel = uiState.user.classLevel,
                unreadCount = uiState.unreadNotificationsCount,
                onSearchClick = onNavigateToSearch,
                onNotificationsClick = onNavigateToNotifications
            )
        }

        // Hero Blueprint Progress Card
        item {
            HeroBlueprintCard(
                progress = uiState.journeyProgress,
                activeDayTitle = uiState.currentLabDay?.title ?: "Strengths",
                activeDayNumber = uiState.currentLabDay?.dayNumber ?: 3,
                onContinueClick = {
                    onNavigateTo7DDay(uiState.currentLabDay?.dayNumber ?: 3)
                }
            )
        }

        // 4 Core Feature Cards Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "Self-Discovery & Growth Tools",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureTile(
                        title = "7D Career Lab",
                        subtitle = "Day 3 Unlocked",
                        icon = Icons.Default.Explore,
                        accentColor = BrightBlue,
                        onClick = onNavigateTo7DLab,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureTile(
                        title = "Handwriting",
                        subtitle = "Observational Insight",
                        icon = Icons.Default.Draw,
                        accentColor = PurpleAccent,
                        onClick = onNavigateToHandwriting,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureTile(
                        title = "Cognitive Games",
                        subtitle = "5 Interactive Tests",
                        icon = Icons.Default.SportsEsports,
                        accentColor = MaargyaOrange,
                        onClick = onNavigateToGames,
                        modifier = Modifier.weight(1f)
                    )
                    FeatureTile(
                        title = "Learning Hub",
                        subtitle = "Courses & Quizzes",
                        icon = Icons.Default.School,
                        accentColor = EmeraldGreen,
                        onClick = onNavigateToLearn,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Resume Active Course Card
        item {
            uiState.continueCourse?.let { course ->
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SectionHeader(
                        title = "Continue Learning",
                        ctaText = "View Hub",
                        onCtaClick = onNavigateToLearn,
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                    CourseCard(
                        course = course,
                        onClick = { onNavigateToCourseDetail(course.id) }
                    )
                }
            }
        }

        // High Growth Career Paths Section
        item {
            SectionHeader(
                title = "Recommended For You",
                subtitle = "Tailored paths based on your analytical profile",
                ctaText = "Explore All",
                onCtaClick = onNavigateTo7DLab
            )
        }

        items(uiState.recommendedCareers) { career ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                CareerCard(
                    career = career,
                    onClick = { onNavigateToCareerDetail(career.id) },
                    onBookmarkClick = { /* toggled in detail */ }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    classLevel: String,
    unreadCount: Int,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hi, ${userName.substringBefore(" ")} 👋",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = classLevel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("home_search_btn")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search careers and skills",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Box {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("home_notifications_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (unreadCount > 0) {
                    Surface(
                        color = MaargyaOrange,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun HeroBlueprintCard(
    progress: Int,
    activeDayTitle: String,
    activeDayNumber: Int,
    onContinueClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("hero_blueprint_card")
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(TechNavy, TechBlue)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "7D CAREER LAB",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "$progress% Complete",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaargyaAmber
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Day $activeDayNumber: $activeDayTitle",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Evaluate your standout cognitive strengths and analytical abilities.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftSkyBlue,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaargyaOrange,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaargyaOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue Day $activeDayNumber Assessment →",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("feature_tile_$title")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
