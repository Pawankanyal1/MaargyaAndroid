package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.local.entities.GameRecordEntity
import com.example.data.remote.NetworkResult
import com.example.data.repository.GameInfo
import com.example.data.repository.GamesRepository
import com.example.domain.model.GameRecord
import com.example.domain.model.GameType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GamesRepositoryImpl(private val database: AppDatabase) : GamesRepository {

    override val gameRecords: Flow<List<GameRecord>> = database.maargyaDao().getAllGameRecords()
        .map { entities ->
            entities.map { entity ->
                GameRecord(
                    id = entity.id,
                    gameType = try { GameType.valueOf(entity.gameType) } catch (e: Exception) { GameType.MEMORY_MATCH },
                    score = entity.score,
                    accuracy = entity.accuracy,
                    timeSeconds = entity.timeSeconds,
                    level = entity.level,
                    playedAt = entity.playedAt
                )
            }
        }

    override fun getAvailableGames(): List<GameInfo> {
        return listOf(
            GameInfo(
                type = GameType.MEMORY_MATCH,
                title = "Memory Match",
                description = "Flip cards to find pairs of essential career tools, matching tech symbols with high precision.",
                difficulty = "Medium",
                targetSkills = listOf("Working Memory", "Visual Recall", "Pattern Speed")
            ),
            GameInfo(
                type = GameType.PATTERN_DETECTIVE,
                title = "Pattern Detective",
                description = "Decipher numeric, abstract, and algorithmic sequences to predict the next matching piece.",
                difficulty = "Challenging",
                targetSkills = listOf("Inductive Reasoning", "Abstract Logic", "Rule Extraction")
            ),
            GameInfo(
                type = GameType.FOCUS_CHALLENGE,
                title = "Focus Challenge",
                description = "Respond under cognitive pressure with shifting color/word rules to measure your mental inhibition.",
                difficulty = "Fast-Paced",
                targetSkills = listOf("Cognitive Inhibition", "Attentional Focus", "Reaction Speed")
            ),
            GameInfo(
                type = GameType.LOGIC_SPRINT,
                title = "Logic Sprint",
                description = "Solve timed workplace, mathematical, and situational logic dilemmas before the countdown expires.",
                difficulty = "Medium",
                targetSkills = listOf("Critical Thinking", "Deductive Logic", "Time Management")
            ),
            GameInfo(
                type = GameType.CAREER_QUEST,
                title = "Career Quest",
                description = "Interactive branching storyline where each strategic decision maps to career traits and strengths.",
                difficulty = "Exploratory",
                targetSkills = listOf("Decision Making", "Strategic Planning", "Leadership Insight")
            )
        )
    }

    override suspend fun recordGameResult(
        type: GameType,
        score: Int,
        accuracy: Int,
        timeSeconds: Int,
        level: Int
    ): NetworkResult<GameRecord> {
        val entity = GameRecordEntity(
            gameType = type.name,
            score = score,
            accuracy = accuracy,
            timeSeconds = timeSeconds,
            level = level,
            playedAt = System.currentTimeMillis()
        )
        database.maargyaDao().insertGameRecord(entity)
        return NetworkResult.Success(
            GameRecord(
                id = entity.id,
                gameType = type,
                score = score,
                accuracy = accuracy,
                timeSeconds = timeSeconds,
                level = level,
                playedAt = entity.playedAt
            )
        )
    }

    override suspend fun getBestScore(type: GameType): GameRecord? {
        val best = database.maargyaDao().getBestScoreForGame(type.name) ?: return null
        return GameRecord(
            id = best.id,
            gameType = type,
            score = best.score,
            accuracy = best.accuracy,
            timeSeconds = best.timeSeconds,
            level = best.level,
            playedAt = best.playedAt
        )
    }
}
