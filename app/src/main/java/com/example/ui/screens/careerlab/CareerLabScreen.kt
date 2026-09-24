package com.example.ui.screens.careerlab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.CareerLabDay
import com.example.ui.components.DisclaimerCard
import com.example.ui.components.MaargyaTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CareerLabViewModel

@Composable
fun CareerLabScreen(
    viewModel: CareerLabViewModel,
    onNavigateBack: () -> Unit,
    onDayClick: (Int) -> Unit,
    onViewSummaryClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val completedCount = uiState.days.count { it.isCompleted }
    val progressPercent = if (uiState.days.isNotEmpty()) (completedCount * 100) / uiState.days.size else 0

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = "7D Career Lab",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(onClick = onViewSummaryClick) {
                        Text(
                            text = "Blueprint",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("career_lab_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Explainer Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Self-Discovery Pathway",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "$completedCount of 7 Days Complete",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Journey through 7 sequential reflective milestones to map your innate passions, cognitive strengths, and personalized career roadmaps.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaargyaOrange,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // Days Timeline
            itemsIndexed(uiState.days) { index, day ->
                CareerLabDayRow(
                    day = day,
                    isLast = index == uiState.days.lastIndex,
                    onClick = {
                        if (day.isUnlocked) {
                            onDayClick(day.dayNumber)
                        }
                    }
                )
            }

            // Summary CTA Button
            item {
                Button(
                    onClick = onViewSummaryClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("career_lab_view_summary_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaargyaOrange)
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Career Discovery Summary", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            item {
                DisclaimerCard(
                    text = "Career discovery results are designed for guidance and exploratory self-reflection, not definitive predictive outcomes."
                )
            }
        }
    }
}

@Composable
private fun CareerLabDayRow(
    day: CareerLabDay,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = day.isUnlocked, onClick = onClick)
            .testTag("career_lab_day_${day.dayNumber}")
    ) {
        // Vertical Timeline Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(44.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = when {
                    day.isCompleted -> EmeraldGreen
                    day.isUnlocked -> MaargyaOrange
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (day.isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(20.dp))
                    } else if (day.isUnlocked) {
                        Text(
                            text = "${day.dayNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(56.dp)
                        .background(
                            if (day.isCompleted) EmeraldGreen else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (day.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = if (day.isUnlocked) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Day ${day.dayNumber}: ${day.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = when {
                            day.isCompleted -> EmeraldGreenLight
                            day.isUnlocked -> MaargyaOrangeLight
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = when {
                                day.isCompleted -> "Done"
                                day.isUnlocked -> "In Progress"
                                else -> "Locked"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                day.isCompleted -> EmeraldGreen
                                day.isUnlocked -> MaargyaOrangeDark
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = day.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
