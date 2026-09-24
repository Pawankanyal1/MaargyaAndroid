package com.example.ui.screens.career

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.CareerCard
import com.example.ui.components.MaargyaTopAppBar
import com.example.ui.theme.MaargyaOrange
import com.example.viewmodel.CareerViewModel

@Composable
fun CareerExplorerScreen(
    viewModel: CareerViewModel,
    onNavigateBack: () -> Unit,
    onCareerClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = "Career Explorer",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("career_explorer_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search field
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search careers, skills, or domains...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("career_explorer_search_field")
                )
            }

            // Category filter chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("All Domains") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaargyaOrange,
                                selectedLabelColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    items(uiState.categories) { cat ->
                        val isSelected = uiState.selectedCategory == cat.name
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(cat.name) },
                            label = { Text(cat.name) },
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
                    text = "${uiState.filteredCareers.size} Careers Found",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(uiState.filteredCareers) { career ->
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
}
