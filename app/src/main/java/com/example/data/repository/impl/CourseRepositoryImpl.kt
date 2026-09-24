package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.local.entities.CourseProgressEntity
import com.example.data.local.entities.LessonNoteEntity
import com.example.data.repository.CourseRepository
import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CourseRepositoryImpl(private val database: AppDatabase) : CourseRepository {

    private val staticCourses = listOf(
        CourseItem(
            id = "course_ai_intro",
            title = "AI & Modern Software Systems",
            instructor = "Dr. Priya Sharma (AI Research Fellow)",
            durationHours = "6.5 Hours",
            level = "Intermediate",
            category = "Technology",
            progressPercent = 37,
            totalLessons = 6,
            completedLessons = 2,
            description = "Master the architectural foundations of modern intelligent applications. Learn how large language models, vector embeddings, and real-time APIs fit into production Android and Cloud software.",
            skillsCovered = listOf("Generative AI", "Vector Embeddings", "Kotlin API Integration", "LLM Orchestration", "Prompt Engineering"),
            isSaved = true,
            isEnrolled = true
        ),
        CourseItem(
            id = "course_ui_ux",
            title = "Human-Centered UI/UX & Design Systems",
            instructor = "Alex Rivera (Design Lead)",
            durationHours = "5.0 Hours",
            level = "Beginner",
            category = "Design",
            progressPercent = 70,
            totalLessons = 5,
            completedLessons = 3,
            description = "A practical masterclass on crafting modern, intuitive mobile interfaces. Understand design tokens, accessibility standards (WCAG), typography balance, and Figma component libraries.",
            skillsCovered = listOf("Design Systems", "Figma Components", "Accessibility Guidelines", "Mobile Ergonomics", "Micro-Interactions"),
            isSaved = false,
            isEnrolled = true
        ),
        CourseItem(
            id = "course_python_data",
            title = "Data Intelligence & Decision Science",
            instructor = "Siddharth Rao (Chief Data Strategist)",
            durationHours = "8.0 Hours",
            level = "Intermediate",
            category = "Technology",
            progressPercent = 0,
            totalLessons = 7,
            completedLessons = 0,
            description = "Turn raw datasets into strategic advantage. Dive into SQL warehousing, Python data transformation, visual exploratory analysis, and predictive trend modeling.",
            skillsCovered = listOf("SQL Querying", "Python Pandas", "Data Visualization", "Hypothesis Testing", "Executive Dashboards"),
            isSaved = true,
            isEnrolled = false
        ),
        CourseItem(
            id = "course_entrepreneurship",
            title = "Zero to One: Technology Venture Building",
            instructor = "Meera Nambiar (Venture Partner & Founder)",
            durationHours = "4.5 Hours",
            level = "All Levels",
            category = "Business & Finance",
            progressPercent = 100,
            totalLessons = 4,
            completedLessons = 4,
            description = "From customer discovery to minimum viable product (MVP). Discover how to validate product-market fit, unit economics, and pitch to angel investors.",
            skillsCovered = listOf("Product Validation", "Unit Economics", "Team Building", "Go-To-Market", "Fundraising"),
            isSaved = false,
            isEnrolled = true
        )
    )

    private val staticLessonsByCourse = mapOf(
        "course_ai_intro" to listOf(
            Lesson(
                id = "lesson_ai_1",
                courseId = "course_ai_intro",
                title = "Lesson 1: Evolution of Intelligent Systems",
                durationMinutes = 18,
                type = LessonType.VIDEO,
                content = "Understanding the trajectory from rule-based expert systems to statistical machine learning and modern transformer architectures. Learn why deep contextual representations changed how software operates.",
                resources = listOf("Transformer Architecture Whitepaper", "Course Cheatsheet PDF"),
                isCompleted = true
            ),
            Lesson(
                id = "lesson_ai_2",
                courseId = "course_ai_intro",
                title = "Lesson 2: Tokenization, Vectors & Semantic Space",
                durationMinutes = 22,
                type = LessonType.READING,
                content = "How computers represent human knowledge geometrically. Vector embeddings translate tokens, sentences, and code snippets into dense vectors where semantic similarity corresponds to cosine distance.",
                resources = listOf("Interactive Vector Playground Link", "Code Exercises in Python"),
                isCompleted = true
            ),
            Lesson(
                id = "lesson_ai_3",
                courseId = "course_ai_intro",
                title = "Lesson 3: Knowledge Check Quiz",
                durationMinutes = 15,
                type = LessonType.QUIZ,
                content = "Test your grasp of foundational AI architecture concepts, vector spaces, and transformer self-attention mechanisms before advancing.",
                resources = emptyList(),
                isCompleted = false
            ),
            Lesson(
                id = "lesson_ai_4",
                courseId = "course_ai_intro",
                title = "Lesson 4: Retrieval-Augmented Generation (RAG)",
                durationMinutes = 25,
                type = LessonType.VIDEO,
                content = "Discover how to augment foundation models with private knowledge bases. Learn the complete pipeline: chunking, embedding generation, vector indexing, retrieval, and synthesis.",
                resources = listOf("RAG Architecture Blueprint", "Sample Schema Setup"),
                isCompleted = false
            ),
            Lesson(
                id = "lesson_ai_5",
                courseId = "course_ai_intro",
                title = "Lesson 5: Building Mobile AI Features in Android",
                durationMinutes = 30,
                type = LessonType.VIDEO,
                content = "Architecting clean Android applications that communicate securely with AI services via Retrofit, Kotlin Coroutines, and ViewModel StateFlow while handling latency gracefully.",
                resources = listOf("Android Starter Template Code", "Best Practices Checklist"),
                isCompleted = false
            ),
            Lesson(
                id = "lesson_ai_6",
                courseId = "course_ai_intro",
                title = "Lesson 6: Capstone Project & Ethics",
                durationMinutes = 20,
                type = LessonType.ASSIGNMENT,
                content = "Deploy a complete career assistant prototype. Review safety considerations, bias mitigation, and responsible AI deployment principles.",
                resources = listOf("Capstone Submission Portal", "Evaluation Rubric"),
                isCompleted = false
            )
        ),
        "course_ui_ux" to listOf(
            Lesson(
                id = "lesson_ui_1",
                courseId = "course_ui_ux",
                title = "Lesson 1: Foundations of User Mental Models",
                durationMinutes = 15,
                type = LessonType.VIDEO,
                content = "Why affordance and feedback dictate user intuition. How cognitive load influences interface satisfaction.",
                isCompleted = true
            ),
            Lesson(
                id = "lesson_ui_2",
                courseId = "course_ui_ux",
                title = "Lesson 2: Typography & Spacing Systems",
                durationMinutes = 20,
                type = LessonType.READING,
                content = "The 8dp grid, line heights, font scale tokens, and visual contrast hierarchy.",
                isCompleted = true
            ),
            Lesson(
                id = "lesson_ui_3",
                courseId = "course_ui_ux",
                title = "Lesson 3: Component Architecture in Figma",
                durationMinutes = 25,
                type = LessonType.VIDEO,
                content = "Creating nested variants, auto-layout cards, and responsive component libraries.",
                isCompleted = true
            ),
            Lesson(
                id = "lesson_ui_4",
                courseId = "course_ui_ux",
                title = "Lesson 4: Mobile Accessibility & Touch Targets",
                durationMinutes = 18,
                type = LessonType.VIDEO,
                content = "Enforcing 48dp minimum targets, WCAG color contrast ratios, and TalkBack content descriptions.",
                isCompleted = false
            ),
            Lesson(
                id = "lesson_ui_5",
                courseId = "course_ui_ux",
                title = "Lesson 5: UI/UX Assessment Quiz",
                durationMinutes = 12,
                type = LessonType.QUIZ,
                content = "Evaluate your knowledge of design systems and accessibility tokens.",
                isCompleted = false
            )
        )
    )

    private val staticQuizzes = mapOf(
        "course_ai_intro" to Quiz(
            id = "quiz_ai_foundation",
            courseId = "course_ai_intro",
            title = "AI Architecture & Fundamentals Quiz",
            timeLimitSeconds = 180,
            questions = listOf(
                QuizQuestion(
                    id = "q1",
                    questionText = "What mathematical concept enables vector embeddings to measure semantic similarity?",
                    options = listOf(
                        QuizOption("A", "Cosine similarity between high-dimensional vectors"),
                        QuizOption("B", "Alphabetical character sorting algorithms"),
                        QuizOption("C", "Binary tree traversal depth"),
                        QuizOption("D", "Random hash code collisions")
                    ),
                    correctIndex = 0,
                    explanation = "Cosine similarity measures the cosine of the angle between two non-zero vectors in multi-dimensional space, accurately reflecting semantic conceptual closeness."
                ),
                QuizQuestion(
                    id = "q2",
                    questionText = "What is the primary role of Retrieval-Augmented Generation (RAG)?",
                    options = listOf(
                        QuizOption("A", "To retrain the neural model completely from scratch"),
                        QuizOption("B", "To provide grounded external context to a model without retraining"),
                        QuizOption("C", "To compress the model weights into 8-bit integers"),
                        QuizOption("D", "To remove all punctuation from input prompts")
                    ),
                    correctIndex = 1,
                    explanation = "RAG allows an LLM to query a verified knowledge base dynamically, retrieving relevant facts to generate grounded, accurate answers without expensive parameter retraining."
                ),
                QuizQuestion(
                    id = "q3",
                    questionText = "Why should Android apps avoid making direct Gemini / API calls with hardcoded keys?",
                    options = listOf(
                        QuizOption("A", "It slows down phone battery charging"),
                        QuizOption("B", "API keys can be easily extracted from decompiled APKs"),
                        QuizOption("C", "Android operating system does not support HTTPS"),
                        QuizOption("D", "Keyboards cannot type special characters")
                    ),
                    correctIndex = 1,
                    explanation = "Hardcoding keys exposes secrets to reverse engineering. In production, credentials should be injected securely via server proxies or build configuration secrets."
                ),
                QuizQuestion(
                    id = "q4",
                    questionText = "Which mechanism in modern Transformer models computes relationship weights between words in a sequence?",
                    options = listOf(
                        QuizOption("A", "Self-Attention mechanism"),
                        QuizOption("B", "Bubble Sort algorithm"),
                        QuizOption("C", "Linear Regression intercept"),
                        QuizOption("D", "Disk I/O caching")
                    ),
                    correctIndex = 0,
                    explanation = "Self-attention computes dynamic weights allowing each token to attend to other relevant tokens across the entire input sequence simultaneously."
                )
            )
        )
    )

    override val allCourses: Flow<List<CourseItem>> = database.maargyaDao().getAllCourseProgress()
        .map { progressList ->
            val progressMap = progressList.associateBy { it.courseId }
            staticCourses.map { course ->
                val localProgress = progressMap[course.id]
                if (localProgress != null) {
                    course.copy(
                        progressPercent = localProgress.progressPercent,
                        completedLessons = localProgress.completedLessons,
                        isSaved = localProgress.isSaved,
                        isEnrolled = localProgress.isEnrolled
                    )
                } else course
            }
        }

    override val myCourses: Flow<List<CourseItem>> = allCourses.map { courses ->
        courses.filter { it.isEnrolled }
    }

    override val savedCourses: Flow<List<CourseItem>> = allCourses.map { courses ->
        courses.filter { it.isSaved }
    }

    override suspend fun getCourseById(id: String): CourseItem? {
        return staticCourses.find { it.id == id }
    }

    override suspend fun getLessonsForCourse(courseId: String): List<Lesson> {
        return staticLessonsByCourse[courseId] ?: staticLessonsByCourse["course_ai_intro"] ?: emptyList()
    }

    override suspend fun getLesson(lessonId: String): Lesson? {
        for (lessons in staticLessonsByCourse.values) {
            val found = lessons.find { it.id == lessonId }
            if (found != null) {
                // Fetch any saved note from Room
                return found
            }
        }
        return null
    }

    override suspend fun markLessonCompleted(courseId: String, lessonId: String) {
        val course = staticCourses.find { it.id == courseId } ?: return
        val currentProgress = database.maargyaDao().getAllCourseProgress()
        val newCompleted = (course.completedLessons + 1).coerceAtMost(course.totalLessons)
        val newPercent = (newCompleted * 100) / course.totalLessons
        database.maargyaDao().insertOrUpdateCourseProgress(
            CourseProgressEntity(
                courseId = courseId,
                title = course.title,
                completedLessons = newCompleted,
                totalLessons = course.totalLessons,
                progressPercent = newPercent,
                isCompleted = newCompleted >= course.totalLessons,
                isSaved = course.isSaved,
                isEnrolled = true
            )
        )
    }

    override suspend fun saveLessonNote(courseId: String, lessonId: String, note: String) {
        database.maargyaDao().insertOrUpdateLessonNote(
            LessonNoteEntity(
                lessonId = lessonId,
                courseId = courseId,
                noteContent = note,
                isCompleted = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun toggleSaveCourse(courseId: String): Boolean {
        val course = staticCourses.find { it.id == courseId } ?: return false
        val newSaved = !course.isSaved
        database.maargyaDao().insertOrUpdateCourseProgress(
            CourseProgressEntity(
                courseId = courseId,
                title = course.title,
                completedLessons = course.completedLessons,
                totalLessons = course.totalLessons,
                progressPercent = course.progressPercent,
                isCompleted = course.progressPercent >= 100,
                isSaved = newSaved,
                isEnrolled = course.isEnrolled
            )
        )
        return newSaved
    }

    override suspend fun getQuizForCourse(courseId: String): Quiz? {
        return staticQuizzes[courseId] ?: staticQuizzes["course_ai_intro"]
    }
}
