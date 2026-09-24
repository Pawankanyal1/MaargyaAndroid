package com.example.ui.screens.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MaargyaTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.CourseViewModel

enum class PlayerTab(val label: String) {
    OVERVIEW("Overview"),
    RESOURCES("Resources"),
    NOTES("My Notes"),
    CURRICULUM("Curriculum")
}

@Composable
fun CoursePlayerScreen(
    courseId: String,
    lessonId: String,
    viewModel: CourseViewModel,
    onNavigateBack: () -> Unit,
    onQuizClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPlayerTab by remember { mutableStateOf(PlayerTab.OVERVIEW) }
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) {
        viewModel.loadCourseDetail(courseId)
    }

    val lesson = uiState.activeLesson ?: uiState.courseLessons.find { it.id == lessonId }

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = lesson?.title ?: "Classroom Player",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (lesson != null) {
                                viewModel.markCurrentLessonCompleted(courseId, lesson.id) {
                                    // advance
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("complete_lesson_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark Lesson Completed & Advance →", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        if (lesson == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Video / Media Player Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Black)
                        .testTag("media_player_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = MaargyaOrange,
                            modifier = Modifier
                                .size(56.dp)
                                .clickable { isPlaying = !isPlaying }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isPlaying) "Playing: ${lesson.title}" else "Tap to Play • ${lesson.durationMinutes} mins",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Bottom scrub line
                    LinearProgressIndicator(
                        progress = { if (isPlaying) 0.45f else 0.15f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(4.dp),
                        color = MaargyaOrange,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }

                // Tabs: Overview, Resources, Notes, Curriculum
                TabRow(
                    selectedTabIndex = selectedPlayerTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    PlayerTab.values().forEach { tab ->
                        Tab(
                            selected = selectedPlayerTab == tab,
                            onClick = { selectedPlayerTab = tab },
                            text = { Text(tab.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                        )
                    }
                }

                // Tab Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (selectedPlayerTab) {
                        PlayerTab.OVERVIEW -> {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "This lesson introduces foundational concepts of ${lesson.title}. You will examine core definitions, real-world case studies, and how this skill directly applies in high-growth career tracks.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Key Takeaways",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("1. Core mental models and problem framing.", style = MaterialTheme.typography.bodyMedium)
                                        Text("2. Industry toolchains & best practices.", style = MaterialTheme.typography.bodyMedium)
                                        Text("3. Hands-on application in real engineering teams.", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }

                                Button(
                                    onClick = onQuizClick,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Test Knowledge with Lesson Quiz →", color = Color.White)
                                }
                            }
                        }

                        PlayerTab.RESOURCES -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Downloadable Learning Materials",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                ResourceItemCard(title = "CheatSheet & Architecture Summary.pdf", size = "2.4 MB")
                                ResourceItemCard(title = "Sample Code Repository & Templates.zip", size = "14.8 MB")
                                ResourceItemCard(title = "Interactive Career Competency Rubric.pdf", size = "1.1 MB")
                            }
                        }

                        PlayerTab.NOTES -> {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Your Lesson Notes (Saved to Device)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedTextField(
                                    value = uiState.currentLessonNote,
                                    onValueChange = { viewModel.onNoteChanged(it) },
                                    placeholder = { Text("Write your reflections, key takeaways, and question items...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .testTag("lesson_notes_input"),
                                    maxLines = 8
                                )

                                Button(
                                    onClick = { viewModel.saveCurrentLessonNote(courseId, lesson.id) },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaargyaOrange)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (uiState.isNoteSaved) "Saved to Storage ✓" else "Save Note")
                                }
                            }
                        }

                        PlayerTab.CURRICULUM -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                uiState.courseLessons.forEachIndexed { index, l ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (l.id == lesson.id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectLesson(l) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontWeight = FontWeight.Bold,
                                                color = if (l.id == lesson.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(l.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                                Text("${l.durationMinutes} mins", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResourceItemCard(title: String, size: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null, tint = BrightBlue)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(size, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
