package com.example.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.CareerCard
import com.example.ui.components.CourseCard
import com.example.ui.components.MaargyaTopAppBar
import com.example.ui.components.SectionHeader
import com.example.viewmodel.CareerViewModel
import com.example.viewmodel.CourseViewModel

@Composable
fun GlobalSearchScreen(
    careerViewModel: CareerViewModel,
    courseViewModel: CourseViewModel,
    onNavigateBack: () -> Unit,
    onCareerClick: (String) -> Unit,
    onCourseClick: (String) -> Unit
) {
    val careerState by careerViewModel.uiState.collectAsState()
    val courseState by courseViewModel.uiState.collectAsState()

    var query by remember { mutableStateOf("") }

    val matchedCareers = remember(query, careerState.careers) {
        if (query.isBlank()) emptyList()
        else careerState.careers.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.requiredSkills.any { s -> s.contains(query, ignoreCase = true) }
        }
    }

    val matchedCourses = remember(query, courseState.allCourses) {
        if (query.isBlank()) emptyList()
        else courseState.allCourses.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = "Search Maargya",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("global_search_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search careers, courses, tools, or skills...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("global_search_input")
                )
            }

            if (query.isBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search across 100+ careers, masterclasses, and skills",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (matchedCareers.isEmpty() && matchedCourses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"$query\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                if (matchedCareers.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Careers (${matchedCareers.size})",
                            subtitle = null,
                            ctaText = null
                        )
                    }
                    items(matchedCareers) { career ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            CareerCard(
                                career = career,
                                onClick = { onCareerClick(career.id) },
                                onBookmarkClick = { careerViewModel.toggleBookmark(career) }
                            )
                        }
                    }
                }

                if (matchedCourses.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Courses (${matchedCourses.size})",
                            subtitle = null,
                            ctaText = null
                        )
                    }
                    items(matchedCourses) { course ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            CourseCard(
                                course = course,
                                onClick = { onCourseClick(course.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
