package com.example.ui.screens.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.MaargyaTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.GamesViewModel

@Composable
fun CareerQuestScreen(
    viewModel: GamesViewModel,
    onNavigateBack: () -> Unit,
    onGameCompleted: (Int, Int, Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val questState = uiState.careerQuest

    LaunchedEffect(Unit) {
        viewModel.initCareerQuest()
    }

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = "Career Quest",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!questState.isFinished && questState.scenarios.isNotEmpty()) {
                val currentScenario = questState.scenarios[questState.currentScenarioIndex]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Scenario ${questState.currentScenarioIndex + 1} of ${questState.scenarios.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "Workplace Dilemma",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TechNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = currentScenario.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentScenario.situation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftSkyBlue
                        )
                    }
                }

                Text(
                    text = "What is your strategic reaction?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentScenario.choices.forEachIndexed { index, choice ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectQuestChoice(choice) }
                                .testTag("quest_choice_$index")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = choice.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = BrightBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Tests: ${choice.primaryTrait}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrightBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (questState.isFinished) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TechNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌟 Career Quest Complete!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Your decisions reveal strong inclination toward collaborative technical leadership & systems design.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftSkyBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Trait summary
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            questState.traitScores.forEach { (trait, score) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(trait, color = Color.White, style = MaterialTheme.typography.bodySmall)
                                    Text("$score pts", color = MaargyaAmber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onGameCompleted(950, 100, 60)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaargyaOrange)
                        ) {
                            Text("Complete Quest", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
