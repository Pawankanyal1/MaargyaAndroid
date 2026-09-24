package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.local.entities.CareerLabDayEntity
import com.example.data.remote.NetworkResult
import com.example.data.repository.CareerLabRepository
import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CareerLabRepositoryImpl(private val database: AppDatabase) : CareerLabRepository {

    private val staticDaysInfo = mapOf(
        1 to Pair(
            "Know Yourself",
            "Identify personality patterns, learning tendencies, and environmental preferences"
        ),
        2 to Pair(
            "Interests",
            "Map your organic curiosities, creative pursuits, and intellectual fascinations"
        ),
        3 to Pair(
            "Strengths",
            "Assess cognitive, communicative, analytical, and problem-solving powers"
        ),
        4 to Pair(
            "Skills",
            "Evaluate present competencies against emerging technological and global skills"
        ),
        5 to Pair(
            "Values",
            "Define work ethics, societal contribution, work-life equilibrium, and financial goals"
        ),
        6 to Pair(
            "Career Exploration",
            "Match your personalized matrix against multidisciplinary modern career clusters"
        ),
        7 to Pair(
            "Build Your Career Roadmap",
            "Synthesize milestones, higher education degrees, certifications, and 3-year objectives"
        )
    )

    private val questionsByDay = mapOf(
        1 to listOf(
            CareerLabQuestion(
                id = "q1_1",
                prompt = "How do you recharge energy after an intense project or school week?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Deep solitary reading, coding, or reflection",
                    "Collaborative brainstorming with friends & peers",
                    "Hands-on building, experimenting, or sports",
                    "Organizing notes, journaling, or planning next steps"
                )
            ),
            CareerLabQuestion(
                id = "q1_2",
                prompt = "When faced with an unexpected obstacle, what is your instinctive approach?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Break it down into root causes logically",
                    "Consult mentors, peers, and gather perspectives",
                    "Try multiple trial-and-error prototypes rapidly",
                    "Step back to understand the broader big picture"
                )
            ),
            CareerLabQuestion(
                id = "q1_3",
                prompt = "Write a sentence describing what kind of problems ignite your genuine curiosity:",
                type = QuestionType.REFLECTION,
                reflectionPlaceholder = "e.g., Automating repetitive processes, designing human-friendly products, or understanding nature's systems..."
            )
        ),
        2 to listOf(
            CareerLabQuestion(
                id = "q2_1",
                prompt = "Which of the following activities feels effortless and deeply engaging?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Investigating tech gadgets, algorithms, and logic puzzles",
                    "Designing graphics, visual layouts, and artistic themes",
                    "Analyzing stock markets, business models, and strategy",
                    "Mentoring, teaching, and helping others grow",
                    "Scientific research, lab experiments, and biology"
                )
            ),
            CareerLabQuestion(
                id = "q2_2",
                prompt = "Select your top curiosity domain:",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "AI, Software, and Digital Systems",
                    "Sustainable Energy, Clean Tech & Space",
                    "Digital Media, Storytelling & Film",
                    "Healthcare, Biotechnology & Genetics",
                    "Financial Markets & Tech Entrepreneurship"
                )
            )
        ),
        3 to listOf(
            CareerLabQuestion(
                id = "q3_1",
                prompt = "What is your standout analytical or interpersonal strength?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Pattern recognition and rapid technical comprehension",
                    "Empathy, articulation, and persuasive communication",
                    "Strategic foresight, prioritization, and organization",
                    "Creative visual thinking and divergent ideation"
                )
            ),
            CareerLabQuestion(
                id = "q3_2",
                prompt = "In team assignments, what role do people naturally look to you for?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "The Technical Architect / Problem Solver",
                    "The Project Coordinator / Presenter",
                    "The Designer / Visual Formulator",
                    "The Researcher / Data Validator"
                )
            )
        ),
        4 to listOf(
            CareerLabQuestion(
                id = "q4_1",
                prompt = "Which foundational skill would you love to master in the next 12 months?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Programming, Machine Learning, and Cloud Systems",
                    "Product UI/UX, Interaction Design & Prototyping",
                    "Data Science, Financial Modeling & Statistics",
                    "Public Speaking, Negotiation, and Leadership",
                    "Content Production, Journalism, and Media"
                )
            )
        ),
        5 to listOf(
            CareerLabQuestion(
                id = "q5_1",
                prompt = "Which work environment value matters most to your long-term fulfillment?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "High autonomy, intellectual freedom & remote flexibility",
                    "High societal impact, helping communities & teaching",
                    "Fast career velocity, high earning potential & leadership",
                    "Creative self-expression and artistic originality"
                )
            )
        ),
        6 to listOf(
            CareerLabQuestion(
                id = "q6_1",
                prompt = "Reviewing your interests & strengths, which career cluster inspires you most?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Technology & Autonomous Software Engineering",
                    "Applied Data Science & Artificial Intelligence",
                    "Human-Centered Product Design (UI/UX)",
                    "Fintech, Venture Capital & Entrepreneurship",
                    "Scientific Research & Bio-Engineering"
                )
            )
        ),
        7 to listOf(
            CareerLabQuestion(
                id = "q7_1",
                prompt = "What is your immediate 1-year educational or milestone target?",
                type = QuestionType.SINGLE_CHOICE,
                options = listOf(
                    "Gain admission to a premier undergraduate program in Computer Science / Engineering",
                    "Build 3 production-grade portfolio projects and earn industry certifications",
                    "Master foundational mathematics, statistics, and programming",
                    "Complete internships and participate in national hackathons"
                )
            ),
            CareerLabQuestion(
                id = "q7_2",
                prompt = "Write your personal vision statement for the next 3 years:",
                type = QuestionType.REFLECTION,
                reflectionPlaceholder = "e.g., Become a proficient software engineer developing intelligent tools that solve societal challenges..."
            )
        )
    )

    override val labDays: Flow<List<CareerLabDay>> = database.maargyaDao().getAllCareerLabDays()
        .map { entities ->
            if (entities.isEmpty()) {
                // Return default 7 days
                (1..7).map { day ->
                    val (title, subtitle) = staticDaysInfo[day] ?: Pair("Day $day", "")
                    CareerLabDay(
                        dayNumber = day,
                        title = title,
                        subtitle = subtitle,
                        description = subtitle,
                        isCompleted = day <= 2,
                        isUnlocked = day <= 3,
                        questions = questionsByDay[day] ?: emptyList()
                    )
                }
            } else {
                entities.map { entity ->
                    val (title, subtitle) = staticDaysInfo[entity.dayNumber] ?: Pair(entity.title, entity.subtitle)
                    CareerLabDay(
                        dayNumber = entity.dayNumber,
                        title = title,
                        subtitle = subtitle,
                        description = subtitle,
                        isCompleted = entity.isCompleted,
                        isUnlocked = entity.isUnlocked,
                        questions = questionsByDay[entity.dayNumber] ?: emptyList()
                    )
                }
            }
        }

    override suspend fun getDay(dayNumber: Int): CareerLabDay? {
        val (title, subtitle) = staticDaysInfo[dayNumber] ?: return null
        val entity = database.maargyaDao().getCareerLabDay(dayNumber)
        return CareerLabDay(
            dayNumber = dayNumber,
            title = title,
            subtitle = subtitle,
            description = subtitle,
            isCompleted = entity?.isCompleted ?: (dayNumber <= 2),
            isUnlocked = entity?.isUnlocked ?: (dayNumber <= 3),
            questions = questionsByDay[dayNumber] ?: emptyList()
        )
    }

    override suspend fun completeDay(dayNumber: Int, answers: Map<String, String>): NetworkResult<Unit> {
        val json = answers.entries.joinToString(prefix = "{", postfix = "}") { "\"${it.key}\":\"${it.value.replace("\"", "\\\"")}\"" }
        database.maargyaDao().markDayCompleted(dayNumber, json, System.currentTimeMillis())
        if (dayNumber < 7) {
            database.maargyaDao().unlockDay(dayNumber + 1)
        }
        return NetworkResult.Success(Unit)
    }

    override suspend fun getDiscoverySummary(): CareerDiscoverySummary {
        return CareerDiscoverySummary(
            completedDays = 7,
            topInterests = listOf("Artificial Intelligence & Software", "Digital Product Architecture", "System Design & Algorithms"),
            keyStrengths = listOf("Analytical Problem Decomposition", "Rapid Technical Comprehension", "Creative Structural Thinking"),
            preferredWorkStyle = "High autonomy with collaborative cross-functional synergy",
            coreSkillAreas = listOf("Data Structures & Algorithms", "Full-Stack Development", "UI/UX Systems Thinking", "Cloud Architecture"),
            recommendedCareerFields = listOf(
                "Software Engineer / Systems Architect",
                "Machine Learning / AI Engineer",
                "Product UI/UX Designer",
                "Data & Analytics Strategist"
            ),
            learningRecommendations = listOf(
                "AI & Modern Software Systems (Recommended Course)",
                "Foundations of UI/UX & Human-Centered Design",
                "Discrete Mathematics & Algorithmic Problem Solving",
                "Cloud Computing & Distributed Systems"
            )
        )
    }
}
