package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GameInfo
import com.example.data.repository.GamesRepository
import com.example.domain.model.GameRecord
import com.example.domain.model.GameType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Memory Match State
data class MemoryCard(
    val id: Int,
    val iconName: String,
    val label: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

data class MemoryMatchState(
    val cards: List<MemoryCard> = emptyList(),
    val moves: Int = 0,
    val matchesFound: Int = 0,
    val totalPairs: Int = 6,
    val timeSeconds: Int = 0,
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val accuracy: Int = 100
)

// Pattern Detective Question
data class PatternQuestion(
    val id: String,
    val sequence: List<String>,
    val options: List<String>,
    val correctIndex: Int,
    val ruleExplanation: String
)

data class PatternDetectiveState(
    val currentQuestionIndex: Int = 0,
    val questions: List<PatternQuestion> = emptyList(),
    val score: Int = 0,
    val streak: Int = 0,
    val isFinished: Boolean = false,
    val selectedOptionIndex: Int? = null,
    val isCorrect: Boolean? = null
)

// Focus Challenge State
data class FocusStimulus(
    val text: String,
    val colorHex: Long, // ARGB
    val isMatchRule: Boolean
)

data class FocusChallengeState(
    val round: Int = 0,
    val maxRounds: Int = 10,
    val currentStimulus: FocusStimulus? = null,
    val ruleInstruction: String = "MATCH INK COLOR",
    val score: Int = 0,
    val correctAnswers: Int = 0,
    val streak: Int = 0,
    val isFinished: Boolean = false
)

// Logic Sprint Question
data class LogicQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class LogicSprintState(
    val currentQuestionIndex: Int = 0,
    val questions: List<LogicQuestion> = emptyList(),
    val score: Int = 0,
    val timeRemainingSeconds: Int = 60,
    val isFinished: Boolean = false,
    val userAnswers: MutableList<Int> = mutableListOf()
)

// Career Quest Scenario
data class QuestScenario(
    val id: String,
    val title: String,
    val situation: String,
    val choices: List<QuestChoice>
)

data class QuestChoice(
    val text: String,
    val primaryTrait: String, // "Analytical", "Creative", "Leadership", "Technical", "Empathetic"
    val outcomeNote: String
)

data class CareerQuestState(
    val currentScenarioIndex: Int = 0,
    val scenarios: List<QuestScenario> = emptyList(),
    val traitScores: Map<String, Int> = mapOf(
        "Analytical" to 0,
        "Creative" to 0,
        "Leadership" to 0,
        "Technical" to 0,
        "Empathetic" to 0
    ),
    val isFinished: Boolean = false
)

data class GamesUiState(
    val availableGames: List<GameInfo> = emptyList(),
    val pastRecords: List<GameRecord> = emptyList(),
    val memoryMatch: MemoryMatchState = MemoryMatchState(),
    val patternDetective: PatternDetectiveState = PatternDetectiveState(),
    val focusChallenge: FocusChallengeState = FocusChallengeState(),
    val logicSprint: LogicSprintState = LogicSprintState(),
    val careerQuest: CareerQuestState = CareerQuestState(),
    val lastRecordedResult: GameRecord? = null
)

class GamesViewModel(
    private val repository: GamesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadGamesAndHistory()
    }

    private fun loadGamesAndHistory() {
        val games = repository.getAvailableGames()
        _uiState.value = _uiState.value.copy(availableGames = games)
        viewModelScope.launch {
            repository.gameRecords.collect { records ->
                _uiState.value = _uiState.value.copy(pastRecords = records)
            }
        }
    }

    // --- MEMORY MATCH ---
    fun initMemoryMatch() {
        timerJob?.cancel()
        val symbols = listOf(
            Pair("Code", "Software"),
            Pair("DesignServices", "Design"),
            Pair("MedicalServices", "Health"),
            Pair("Analytics", "Data"),
            Pair("RocketLaunch", "Startup"),
            Pair("Psychology", "Cognition")
        )
        val deck = (symbols + symbols).shuffled().mapIndexed { index, (icon, label) ->
            MemoryCard(id = index, iconName = icon, label = label)
        }
        _uiState.value = _uiState.value.copy(
            memoryMatch = MemoryMatchState(
                cards = deck,
                moves = 0,
                matchesFound = 0,
                totalPairs = symbols.size,
                timeSeconds = 0,
                isGameOver = false
            )
        )
        // Start timer
        timerJob = viewModelScope.launch {
            while (!_uiState.value.memoryMatch.isGameOver) {
                delay(1000)
                val current = _uiState.value.memoryMatch
                if (!current.isGameOver) {
                    _uiState.value = _uiState.value.copy(
                        memoryMatch = current.copy(timeSeconds = current.timeSeconds + 1)
                    )
                }
            }
        }
    }

    private var firstFlippedCardIndex: Int? = null
    private var isEvaluatingPair = false

    fun flipMemoryCard(cardId: Int) {
        if (isEvaluatingPair) return
        val current = _uiState.value.memoryMatch
        val cardIndex = current.cards.indexOfFirst { it.id == cardId }
        if (cardIndex == -1 || current.cards[cardIndex].isFlipped || current.cards[cardIndex].isMatched) return

        val newCards = current.cards.toMutableList()
        newCards[cardIndex] = newCards[cardIndex].copy(isFlipped = true)

        if (firstFlippedCardIndex == null) {
            firstFlippedCardIndex = cardIndex
            _uiState.value = _uiState.value.copy(memoryMatch = current.copy(cards = newCards))
        } else {
            val firstIndex = firstFlippedCardIndex!!
            firstFlippedCardIndex = null
            val newMoves = current.moves + 1
            isEvaluatingPair = true

            if (newCards[firstIndex].iconName == newCards[cardIndex].iconName) {
                // Match
                newCards[firstIndex] = newCards[firstIndex].copy(isMatched = true)
                newCards[cardIndex] = newCards[cardIndex].copy(isMatched = true)
                val newMatches = current.matchesFound + 1
                val isWon = newMatches >= current.totalPairs
                val finalScore = if (isWon) {
                    val base = 1000
                    val timePenalty = current.timeSeconds * 5
                    val movesPenalty = newMoves * 15
                    (base - timePenalty - movesPenalty).coerceAtLeast(200)
                } else 0
                val accuracy = ((newMatches.toFloat() / newMoves.coerceAtLeast(1)) * 100).toInt().coerceAtMost(100)

                _uiState.value = _uiState.value.copy(
                    memoryMatch = current.copy(
                        cards = newCards,
                        moves = newMoves,
                        matchesFound = newMatches,
                        isGameOver = isWon,
                        score = finalScore,
                        accuracy = accuracy
                    )
                )
                isEvaluatingPair = false
                if (isWon) {
                    saveGameScore(GameType.MEMORY_MATCH, finalScore, accuracy, current.timeSeconds, 1)
                }
            } else {
                // Mismatch - flip back after brief pause
                _uiState.value = _uiState.value.copy(memoryMatch = current.copy(cards = newCards, moves = newMoves))
                viewModelScope.launch {
                    delay(700)
                    val resetCards = _uiState.value.memoryMatch.cards.toMutableList()
                    resetCards[firstIndex] = resetCards[firstIndex].copy(isFlipped = false)
                    resetCards[cardIndex] = resetCards[cardIndex].copy(isFlipped = false)
                    _uiState.value = _uiState.value.copy(
                        memoryMatch = _uiState.value.memoryMatch.copy(cards = resetCards)
                    )
                    isEvaluatingPair = false
                }
            }
        }
    }

    // --- PATTERN DETECTIVE ---
    fun initPatternDetective() {
        val questions = listOf(
            PatternQuestion(
                id = "p1",
                sequence = listOf("2", "4", "8", "16", "?"),
                options = listOf("24", "32", "30", "64"),
                correctIndex = 1,
                ruleExplanation = "Geometric doubling sequence: each term is multiplied by 2 (16 × 2 = 32)."
            ),
            PatternQuestion(
                id = "p2",
                sequence = listOf("▲", "■", "▲▲", "■■", "▲▲▲", "?"),
                options = listOf("▲▲▲▲", "■■■", "◆◆", "●●●"),
                correctIndex = 1,
                ruleExplanation = "Alternating shape sets with incrementing count: triangles increment, followed by incrementing squares."
            ),
            PatternQuestion(
                id = "p3",
                sequence = listOf("1", "1", "2", "3", "5", "8", "?"),
                options = listOf("11", "12", "13", "15"),
                correctIndex = 2,
                ruleExplanation = "Fibonacci sequence: each number is the sum of the two preceding numbers (5 + 8 = 13)."
            ),
            PatternQuestion(
                id = "p4",
                sequence = listOf("Code", "Compile", "Test", "Deploy", "?"),
                options = listOf("Monitor", "Format", "Undo", "Shutdown"),
                correctIndex = 0,
                ruleExplanation = "Modern DevOps lifecycle: Code → Compile → Test → Deploy → Monitor & Iterate."
            )
        )
        _uiState.value = _uiState.value.copy(
            patternDetective = PatternDetectiveState(
                questions = questions,
                currentQuestionIndex = 0,
                score = 0,
                streak = 0,
                isFinished = false
            )
        )
    }

    fun answerPatternQuestion(optionIndex: Int) {
        val state = _uiState.value.patternDetective
        if (state.isFinished || state.selectedOptionIndex != null) return

        val currentQ = state.questions[state.currentQuestionIndex]
        val isCorrect = optionIndex == currentQ.correctIndex
        val newScore = if (isCorrect) state.score + 250 else state.score
        val newStreak = if (isCorrect) state.streak + 1 else 0

        _uiState.value = _uiState.value.copy(
            patternDetective = state.copy(
                selectedOptionIndex = optionIndex,
                isCorrect = isCorrect,
                score = newScore,
                streak = newStreak
            )
        )

        viewModelScope.launch {
            delay(1200)
            if (state.currentQuestionIndex + 1 < state.questions.size) {
                _uiState.value = _uiState.value.copy(
                    patternDetective = _uiState.value.patternDetective.copy(
                        currentQuestionIndex = state.currentQuestionIndex + 1,
                        selectedOptionIndex = null,
                        isCorrect = null
                    )
                )
            } else {
                val finalAccuracy = (newScore / (state.questions.size * 250f) * 100).toInt()
                _uiState.value = _uiState.value.copy(
                    patternDetective = _uiState.value.patternDetective.copy(
                        isFinished = true,
                        selectedOptionIndex = null,
                        isCorrect = null
                    )
                )
                saveGameScore(GameType.PATTERN_DETECTIVE, newScore, finalAccuracy, 35, 2)
            }
        }
    }

    // --- FOCUS CHALLENGE ---
    fun initFocusChallenge() {
        val stimuli = listOf(
            FocusStimulus("BLUE", 0xFF2563EB, true),
            FocusStimulus("ORANGE", 0xFF2563EB, false),
            FocusStimulus("GREEN", 0xFF10B981, true),
            FocusStimulus("PURPLE", 0xFFF97316, false),
            FocusStimulus("RED", 0xFFEF4444, true),
            FocusStimulus("NAVY", 0xFF0A2540, true),
            FocusStimulus("YELLOW", 0xFF10B981, false),
            FocusStimulus("CYAN", 0xFF0284C7, true)
        )
        _uiState.value = _uiState.value.copy(
            focusChallenge = FocusChallengeState(
                round = 0,
                maxRounds = 8,
                currentStimulus = stimuli.first(),
                ruleInstruction = "Does the COLOR of the text match the WORD?",
                score = 0,
                correctAnswers = 0,
                streak = 0,
                isFinished = false
            )
        )
    }

    fun submitFocusAnswer(userSaysMatch: Boolean) {
        val state = _uiState.value.focusChallenge
        if (state.isFinished) return

        val stimulus = state.currentStimulus ?: return
        val isCorrect = userSaysMatch == stimulus.isMatchRule
        val newCorrect = if (isCorrect) state.correctAnswers + 1 else state.correctAnswers
        val newScore = if (isCorrect) state.score + 120 + (state.streak * 20) else state.score
        val newStreak = if (isCorrect) state.streak + 1 else 0

        val stimuli = listOf(
            FocusStimulus("BLUE", 0xFF2563EB, true),
            FocusStimulus("ORANGE", 0xFF2563EB, false),
            FocusStimulus("GREEN", 0xFF10B981, true),
            FocusStimulus("PURPLE", 0xFFF97316, false),
            FocusStimulus("RED", 0xFFEF4444, true),
            FocusStimulus("NAVY", 0xFF0A2540, true),
            FocusStimulus("YELLOW", 0xFF10B981, false),
            FocusStimulus("CYAN", 0xFF0284C7, true)
        )

        val nextRound = state.round + 1
        if (nextRound < state.maxRounds) {
            _uiState.value = _uiState.value.copy(
                focusChallenge = state.copy(
                    round = nextRound,
                    currentStimulus = stimuli[nextRound % stimuli.size],
                    score = newScore,
                    correctAnswers = newCorrect,
                    streak = newStreak
                )
            )
        } else {
            val accuracy = ((newCorrect.toFloat() / state.maxRounds) * 100).toInt()
            _uiState.value = _uiState.value.copy(
                focusChallenge = state.copy(
                    round = nextRound,
                    score = newScore,
                    correctAnswers = newCorrect,
                    streak = newStreak,
                    isFinished = true
                )
            )
            saveGameScore(GameType.FOCUS_CHALLENGE, newScore, accuracy, 20, 2)
        }
    }

    // --- LOGIC SPRINT ---
    fun initLogicSprint() {
        timerJob?.cancel()
        val questions = listOf(
            LogicQuestion(
                id = "ls1",
                prompt = "A software system handles 120 queries/second. After optimizing database indexing, query latency is halved. Assuming linear capacity, how many queries can it now handle per second?",
                options = listOf("180", "240", "360", "480"),
                correctIndex = 1,
                explanation = "Halving latency doubles throughput capacity: 120 × 2 = 240 queries/second."
            ),
            LogicQuestion(
                id = "ls2",
                prompt = "All Product Managers understand User Research. Some Designers are Product Managers. Which statement MUST be true?",
                options = listOf(
                    "All Designers understand User Research",
                    "Some Designers understand User Research",
                    "No Designers understand User Research",
                    "Product Managers are all Designers"
                ),
                correctIndex = 1,
                explanation = "Since the designers who are PMs understand user research, at least some designers understand user research."
            ),
            LogicQuestion(
                id = "ls3",
                prompt = "If Server A is faster than Server B, and Server C is slower than Server B, which server processes jobs quickest?",
                options = listOf("Server A", "Server B", "Server C", "Cannot be determined"),
                correctIndex = 0,
                explanation = "A > B and B > C, hence Server A is fastest."
            )
        )
        _uiState.value = _uiState.value.copy(
            logicSprint = LogicSprintState(
                questions = questions,
                currentQuestionIndex = 0,
                score = 0,
                timeRemainingSeconds = 45,
                isFinished = false
            )
        )

        timerJob = viewModelScope.launch {
            while (!_uiState.value.logicSprint.isFinished && _uiState.value.logicSprint.timeRemainingSeconds > 0) {
                delay(1000)
                val curr = _uiState.value.logicSprint
                if (curr.timeRemainingSeconds <= 1) {
                    _uiState.value = _uiState.value.copy(
                        logicSprint = curr.copy(timeRemainingSeconds = 0, isFinished = true)
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        logicSprint = curr.copy(timeRemainingSeconds = curr.timeRemainingSeconds - 1)
                    )
                }
            }
        }
    }

    fun submitLogicAnswer(optionIndex: Int) {
        val state = _uiState.value.logicSprint
        if (state.isFinished) return

        val currentQ = state.questions[state.currentQuestionIndex]
        val isCorrect = optionIndex == currentQ.correctIndex
        val newScore = if (isCorrect) state.score + 300 else state.score

        if (state.currentQuestionIndex + 1 < state.questions.size) {
            _uiState.value = _uiState.value.copy(
                logicSprint = state.copy(
                    currentQuestionIndex = state.currentQuestionIndex + 1,
                    score = newScore
                )
            )
        } else {
            val total = state.questions.size * 300
            val accuracy = ((newScore.toFloat() / total) * 100).toInt()
            _uiState.value = _uiState.value.copy(
                logicSprint = state.copy(
                    score = newScore,
                    isFinished = true
                )
            )
            val timeTaken = 45 - state.timeRemainingSeconds
            saveGameScore(GameType.LOGIC_SPRINT, newScore, accuracy, timeTaken, 3)
        }
    }

    // --- CAREER QUEST ---
    fun initCareerQuest() {
        val scenarios = listOf(
            QuestScenario(
                id = "cq1",
                title = "The Scale Surge Dilemma",
                situation = "Your team's mobile application just went viral with 1 million new downloads in 48 hours. The cloud database is beginning to bottleneck. As project lead, what do you prioritize?",
                choices = listOf(
                    QuestChoice("Profile query logs and implement Redis caching to resolve latency mathematically", "Analytical", "Deep analytical rigor applied to root causes."),
                    QuestChoice("Coordinate emergency cross-functional standup and delegate triage responsibilities", "Leadership", "Decisive orchestration under pressure."),
                    QuestChoice("Design a graceful queue loading animation and transparent user status banner", "Creative", "Preserving human brand empathy and clarity."),
                    QuestChoice("Spin up horizontal replica clusters with automated load balancers", "Technical", "Direct hands-on infrastructural mastery.")
                )
            ),
            QuestScenario(
                id = "cq2",
                title = "The Product Pivot",
                situation = "User feedback reveals that high school students love your career quizzes, but parents find the career roadmap confusing. How do you respond?",
                choices = listOf(
                    QuestChoice("Host user discovery calls with 15 parents to map their emotional expectations", "Empathetic", "Human-centered active listening."),
                    QuestChoice("A/B test 3 distinct infographic summary cards against milestone completion metrics", "Analytical", "Data-driven statistical validation."),
                    QuestChoice("Redesign the roadmap as an interactive visual game board with badges", "Creative", "Divergent visual innovation."),
                    QuestChoice("Align the executive team on a dual-dashboard parent-student portal roadmap", "Leadership", "Strategic alignment and governance.")
                )
            ),
            QuestScenario(
                id = "cq3",
                title = "The Breakthrough Pitch",
                situation = "You have 3 minutes to present your team's innovative AI career discovery prototype to venture investors. What is your focal message?",
                choices = listOf(
                    QuestChoice("Demonstrate proprietary accuracy metrics and semantic vector retrieval speed", "Technical", "Engineering superiority."),
                    QuestChoice("Tell the emotional narrative of a student finding their life calling through Maargya", "Empathetic", "Storytelling and emotional resonance."),
                    QuestChoice("Highlight unit economics, addressable market scale, and global expansion strategy", "Leadership", "Commercial viability and ambition."),
                    QuestChoice("Present interactive prototype with micro-animations and unique brand identity", "Creative", "Unforgettable aesthetic craftsmanship.")
                )
            )
        )

        _uiState.value = _uiState.value.copy(
            careerQuest = CareerQuestState(
                scenarios = scenarios,
                currentScenarioIndex = 0,
                traitScores = mapOf(
                    "Analytical" to 0,
                    "Creative" to 0,
                    "Leadership" to 0,
                    "Technical" to 0,
                    "Empathetic" to 0
                ),
                isFinished = false
            )
        )
    }

    fun selectQuestChoice(choice: QuestChoice) {
        val state = _uiState.value.careerQuest
        if (state.isFinished) return

        val currentScores = state.traitScores.toMutableMap()
        val prev = currentScores[choice.primaryTrait] ?: 0
        currentScores[choice.primaryTrait] = prev + 35

        if (state.currentScenarioIndex + 1 < state.scenarios.size) {
            _uiState.value = _uiState.value.copy(
                careerQuest = state.copy(
                    currentScenarioIndex = state.currentScenarioIndex + 1,
                    traitScores = currentScores
                )
            )
        } else {
            _uiState.value = _uiState.value.copy(
                careerQuest = state.copy(
                    traitScores = currentScores,
                    isFinished = true
                )
            )
            saveGameScore(GameType.CAREER_QUEST, 950, 100, 60, 1)
        }
    }

    private fun saveGameScore(type: GameType, score: Int, accuracy: Int, timeSeconds: Int, level: Int) {
        viewModelScope.launch {
            val result = repository.recordGameResult(type, score, accuracy, timeSeconds, level)
            if (result is com.example.data.remote.NetworkResult.Success) {
                _uiState.value = _uiState.value.copy(lastRecordedResult = result.data)
            }
        }
    }
}
