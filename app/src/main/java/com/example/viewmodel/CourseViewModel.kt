package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CourseRepository
import com.example.domain.model.CourseItem
import com.example.domain.model.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class CourseTab(val title: String) {
    CONTINUE("Continue"),
    MY_COURSES("My Courses"),
    RECOMMENDED("Recommended"),
    SAVED("Saved")
}

data class CourseUiState(
    val selectedTab: CourseTab = CourseTab.CONTINUE,
    val allCourses: List<CourseItem> = emptyList(),
    val filteredCourses: List<CourseItem> = emptyList(),
    val selectedCourseDetail: CourseItem? = null,
    val courseLessons: List<Lesson> = emptyList(),
    val activeLesson: Lesson? = null,
    val currentLessonNote: String = "",
    val isNoteSaved: Boolean = false,
    val isLoading: Boolean = false
)

class CourseViewModel(
    private val repository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseUiState(isLoading = true))
    val uiState: StateFlow<CourseUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            repository.allCourses.collect { courses ->
                _uiState.value = _uiState.value.copy(
                    allCourses = courses,
                    filteredCourses = filterByTab(courses, _uiState.value.selectedTab),
                    isLoading = false
                )
            }
        }
    }

    fun selectTab(tab: CourseTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            filteredCourses = filterByTab(_uiState.value.allCourses, tab)
        )
    }

    private fun filterByTab(courses: List<CourseItem>, tab: CourseTab): List<CourseItem> {
        return when (tab) {
            CourseTab.CONTINUE -> courses.filter { it.isEnrolled && it.progressPercent in 1..99 }
            CourseTab.MY_COURSES -> courses.filter { it.isEnrolled }
            CourseTab.RECOMMENDED -> courses
            CourseTab.SAVED -> courses.filter { it.isSaved }
        }
    }

    fun loadCourseDetail(courseId: String) {
        viewModelScope.launch {
            val course = repository.getCourseById(courseId)
            val lessons = repository.getLessonsForCourse(courseId)
            _uiState.value = _uiState.value.copy(
                selectedCourseDetail = course,
                courseLessons = lessons,
                activeLesson = lessons.firstOrNull()
            )
        }
    }

    fun selectLesson(lesson: Lesson) {
        _uiState.value = _uiState.value.copy(
            activeLesson = lesson,
            currentLessonNote = lesson.userNote,
            isNoteSaved = false
        )
    }

    fun onNoteChanged(note: String) {
        _uiState.value = _uiState.value.copy(
            currentLessonNote = note,
            isNoteSaved = false
        )
    }

    fun saveCurrentLessonNote(courseId: String, lessonId: String) {
        viewModelScope.launch {
            repository.saveLessonNote(courseId, lessonId, _uiState.value.currentLessonNote)
            _uiState.value = _uiState.value.copy(isNoteSaved = true)
        }
    }

    fun markCurrentLessonCompleted(courseId: String, lessonId: String, onAdvance: () -> Unit) {
        viewModelScope.launch {
            repository.markLessonCompleted(courseId, lessonId)
            // Reload lessons
            val updatedLessons = repository.getLessonsForCourse(courseId)
            val next = updatedLessons.find { !it.isCompleted } ?: updatedLessons.lastOrNull()
            _uiState.value = _uiState.value.copy(
                courseLessons = updatedLessons,
                activeLesson = next
            )
            onAdvance()
        }
    }

    fun toggleSaveCourse(courseId: String) {
        viewModelScope.launch {
            repository.toggleSaveCourse(courseId)
        }
    }
}
