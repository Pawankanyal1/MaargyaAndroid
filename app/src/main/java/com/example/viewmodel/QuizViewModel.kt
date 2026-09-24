package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CourseRepository
import com.example.domain.model.Quiz
import com.example.domain.model.QuizResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val quiz: Quiz? = null,
    val currentQuestionIndex: Int = 0,
    val selectedOptionByQuestion: Map<Int, Int> = emptyMap(),
    val timeRemainingSeconds: Int = 180,
    val isSubmitted: Boolean = false,
    val result: QuizResult? = null
)

class QuizViewModel(
    private val repository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun loadQuiz(courseId: String) {
        timerJob?.cancel()
        viewModelScope.launch {
            val quiz = repository.getQuizForCourse(courseId)
            _uiState.value = QuizUiState(
                quiz = quiz,
                timeRemainingSeconds = quiz?.timeLimitSeconds ?: 180
            )

            // Start countdown
            timerJob = launch {
                while (!_uiState.value.isSubmitted && _uiState.value.timeRemainingSeconds > 0) {
                    delay(1000)
                    val curr = _uiState.value
                    if (curr.timeRemainingSeconds <= 1) {
                        submitQuiz()
                    } else {
                        _uiState.value = curr.copy(timeRemainingSeconds = curr.timeRemainingSeconds - 1)
                    }
                }
            }
        }
    }

    fun selectOption(questionIndex: Int, optionIndex: Int) {
        val updated = _uiState.value.selectedOptionByQuestion.toMutableMap()
        updated[questionIndex] = optionIndex
        _uiState.value = _uiState.value.copy(selectedOptionByQuestion = updated)
    }

    fun goToNextQuestion() {
        val qSize = _uiState.value.quiz?.questions?.size ?: 0
        if (_uiState.value.currentQuestionIndex + 1 < qSize) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = _uiState.value.currentQuestionIndex + 1
            )
        }
    }

    fun goToPreviousQuestion() {
        if (_uiState.value.currentQuestionIndex > 0) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = _uiState.value.currentQuestionIndex - 1
            )
        }
    }

    fun submitQuiz() {
        timerJob?.cancel()
        val quiz = _uiState.value.quiz ?: return
        var correctCount = 0
        quiz.questions.forEachIndexed { index, question ->
            val userPick = _uiState.value.selectedOptionByQuestion[index]
            if (userPick == question.correctIndex) {
                correctCount++
            }
        }
        val total = quiz.questions.size
        val accuracy = if (total > 0) (correctCount * 100) / total else 0
        val isPassed = accuracy >= 60

        val result = QuizResult(
            quizId = quiz.id,
            score = correctCount * 25,
            totalQuestions = total,
            accuracy = accuracy,
            isPassed = isPassed,
            recommendedNextLesson = "Lesson 4: Retrieval-Augmented Generation (RAG)"
        )

        _uiState.value = _uiState.value.copy(
            isSubmitted = true,
            result = result
        )
    }
}
