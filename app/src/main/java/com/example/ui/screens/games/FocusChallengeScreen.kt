package com.example.ui.screens.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.viewmodel.GamesViewModel

@Composable
fun FocusChallengeScreen(
    viewModel: GamesViewModel,
    onNavigateBack: () -> Unit,
    onGameCompleted: (Int, Int, Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusState = uiState.focusChallenge

    LaunchedEffect(Unit) {
        viewModel.initFocusChallenge()
    }

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = "Focus Challenge",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Round ${focusState.round + 1} / ${focusState.maxRounds}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score: ${focusState.score}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaargyaOrange
                )
            }

            if (!focusState.isFinished && focusState.currentStimulus != null) {
                val stimulus = focusState.currentStimulus!!

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = focusState.ruleInstruction,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stimulus.text,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(stimulus.colorHex)
                            )
                        }
                    }
                }

                // Two rapid action buttons: "NO (Mismatch)" vs "YES (Match)"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.submitFocusAnswer(false) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("focus_no_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("MISMATCH", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.submitFocusAnswer(true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("focus_yes_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("MATCH", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            } else if (focusState.isFinished) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TechNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡ Focus Challenge Finished!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Final Score: ${focusState.score} • Correct: ${focusState.correctAnswers}/${focusState.maxRounds}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftSkyBlue
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val acc = ((focusState.correctAnswers.toFloat() / focusState.maxRounds) * 100).toInt()
                                onGameCompleted(focusState.score, acc, 20)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaargyaOrange)
                        ) {
                            Text("See Analysis", color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
