package com.example.ui.screens.games

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MaargyaTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.GamesViewModel
import com.example.viewmodel.MemoryCard

@Composable
fun MemoryMatchScreen(
    viewModel: GamesViewModel,
    onNavigateBack: () -> Unit,
    onGameCompleted: (Int, Int, Int) -> Unit // score, accuracy, timeSeconds
) {
    val uiState by viewModel.uiState.collectAsState()
    val matchState = uiState.memoryMatch

    LaunchedEffect(Unit) {
        viewModel.initMemoryMatch()
    }

    Scaffold(
        topBar = {
            MaargyaTopAppBar(
                title = "Memory Match",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { viewModel.initMemoryMatch() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Restart")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stats Row: Timer, Moves, Matches
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Moves", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${matchState.moves}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Matches", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${matchState.matchesFound}/${matchState.totalPairs}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaargyaOrange)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${matchState.timeSeconds}s", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4x3 Grid of Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f).testTag("memory_match_grid"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(matchState.cards) { card ->
                    MemoryCardView(
                        card = card,
                        onClick = { viewModel.flipMemoryCard(card.id) }
                    )
                }
            }

            // Win Dialog / Overlay
            AnimatedVisibility(visible = matchState.isGameOver) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TechNavy),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉 Memory Challenge Cleared!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Score: ${matchState.score} • Accuracy: ${matchState.accuracy}% • Time: ${matchState.timeSeconds}s",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftSkyBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.initMemoryMatch() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Replay", color = Color.White)
                            }
                            Button(
                                onClick = {
                                    onGameCompleted(matchState.score, matchState.accuracy, matchState.timeSeconds)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaargyaOrange)
                            ) {
                                Text("View Results", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoryCardView(
    card: MemoryCard,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = when {
            card.isMatched -> EmeraldGreen.copy(alpha = 0.2f)
            card.isFlipped -> MaterialTheme.colorScheme.primaryContainer
            else -> TechNavy
        },
        shadowElevation = 2.dp,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !card.isFlipped && !card.isMatched, onClick = onClick)
            .testTag("memory_card_${card.id}")
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (card.isFlipped || card.isMatched) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = getCardIcon(card.iconName),
                        contentDescription = card.label,
                        tint = if (card.isMatched) EmeraldGreen else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Text(
                    text = "M",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaargyaOrange
                )
            }
        }
    }
}

private fun getCardIcon(name: String): ImageVector {
    return when (name) {
        "Code" -> Icons.Default.Code
        "DesignServices" -> Icons.Default.DesignServices
        "MedicalServices" -> Icons.Default.MedicalServices
        "Analytics" -> Icons.Default.Analytics
        "RocketLaunch" -> Icons.Default.RocketLaunch
        "Psychology" -> Icons.Default.Psychology
        else -> Icons.Default.Extension
    }
}
