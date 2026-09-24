package com.example.ui.screens.discover

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.CareerCategory
import com.example.ui.components.CareerCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.viewmodel.CareerViewModel

@Composable
fun DiscoverScreen(
    viewModel: CareerViewModel,
    onNavigateToSearch: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCareerClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("discover_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "Discover Pathways",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Explore industry clusters and emerging professions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Bar mock
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(onClick = onNavigateToSearch)
                    .testTag("discover_search_bar"),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search careers, tools, or future skills...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Future Skills Carousel
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                SectionHeader(
                    title = "Future Skills in Demand",
                    subtitle = "High-leverage capabilities for the next decade",
                    ctaText = null
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FutureSkillBadge(
                            title = "AI Prompting & Engineering",
                            level = "High Growth",
                            color = BrightBlue,
                            icon = Icons.Default.Memory
                        )
                    }
                    item {
                        FutureSkillBadge(
                            title = "UI/UX Design Systems",
                            level = "Creative Tech",
                            color = PurpleAccent,
                            icon = Icons.Default.Palette
                        )
                    }
                    item {
                        FutureSkillBadge(
                            title = "Data Storytelling & BI",
                            level = "Analytical",
                            color = EmeraldGreen,
                            icon = Icons.Default.QueryStats
                        )
                    }
                    item {
                        FutureSkillBadge(
                            title = "Cloud Infrastructure & DevOps",
                            level = "Engineering",
                            color = MaargyaOrange,
                            icon = Icons.Default.Cloud
                        )
                    }
                }
            }
        }

        // 12 Career Clusters Grid
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                SectionHeader(
                    title = "Industry Clusters",
                    subtitle = "Browse curated careers by domain",
                    ctaText = null
                )

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.categories.chunked(2).forEach { rowCats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowCats.forEach { category ->
                                ClusterCard(
                                    category = category,
                                    onClick = { onCategoryClick(category.name) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowCats.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Featured Careers list
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                SectionHeader(
                    title = "Spotlight Careers",
                    subtitle = "Top modern opportunities with verified paths",
                    ctaText = null
                )
            }
        }

        items(uiState.careers.take(4)) { career ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                CareerCard(
                    career = career,
                    onClick = { onCareerClick(career.id) },
                    onBookmarkClick = { viewModel.toggleBookmark(career) }
                )
            }
        }
    }
}

@Composable
private fun FutureSkillBadge(
    title: String,
    level: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = level,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ClusterCard(
    category: CareerCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("cluster_card_${category.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getCategoryIcon(category.iconName),
                        contentDescription = category.name,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${category.careerCount} Careers",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Memory" -> Icons.Default.Memory
        "MedicalServices" -> Icons.Default.MedicalServices
        "Palette" -> Icons.Default.Palette
        "TrendingUp" -> Icons.Default.TrendingUp
        "Biotech" -> Icons.Default.Biotech
        "Eco" -> Icons.Default.Eco
        "AccountBalance" -> Icons.Default.AccountBalance
        "Videocam" -> Icons.Default.Videocam
        "School" -> Icons.Default.School
        "Engineering" -> Icons.Default.Engineering
        "Flight" -> Icons.Default.Flight
        "Psychology" -> Icons.Default.Psychology
        else -> Icons.Default.Work
    }
}
