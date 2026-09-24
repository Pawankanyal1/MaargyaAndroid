package com.example.ui.screens.learn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.CourseCard
import com.example.ui.theme.MaargyaOrange
import com.example.viewmodel.CourseTab
import com.example.viewmodel.CourseViewModel

@Composable
fun LearnScreen(
    viewModel: CourseViewModel,
    onCourseClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("learn_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "Learning Center",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Master emerging skills with structured career courses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tabs Row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CourseTab.values()) { tab ->
                    val isSelected = uiState.selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        label = { Text(tab.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaargyaOrange,
                            selectedLabelColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

        item {
            Text(
                text = "${uiState.filteredCourses.size} Courses",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(uiState.filteredCourses) { course ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                CourseCard(
                    course = course,
                    onClick = { onCourseClick(course.id) }
                )
            }
        }
    }
}
