package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.local.entities.BookmarkEntity
import com.example.data.repository.CareerRepository
import com.example.domain.model.CareerCategory
import com.example.domain.model.CareerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CareerRepositoryImpl(private val database: AppDatabase) : CareerRepository {

    private val staticCategories = listOf(
        CareerCategory("cat_tech", "Technology", "Software, AI, Cloud, Cybersecurity & Data", 18, "Memory"),
        CareerCategory("cat_health", "Healthcare", "Medicine, Biotechnology, Nursing & Research", 14, "LocalHospital"),
        CareerCategory("cat_design", "Design", "UI/UX, Product, Architecture & Visual Arts", 12, "Palette"),
        CareerCategory("cat_business", "Business & Finance", "Fintech, Strategy, Analytics & Investment", 15, "TrendingUp"),
        CareerCategory("cat_eng", "Engineering", "Robotics, Aerospace, Civil & Clean Energy", 16, "Engineering"),
        CareerCategory("cat_edu", "Education", "Teaching, EdTech, Research & Curriculum", 9, "School"),
        CareerCategory("cat_media", "Media & Arts", "Digital Content, Journalism & Film Production", 11, "VideoLibrary"),
        CareerCategory("cat_entre", "Entrepreneurship", "Startups, Venture Building & Product Strategy", 8, "RocketLaunch"),
        CareerCategory("cat_science", "Science & Research", "Astrophysics, Genetics, Quantum & Environment", 10, "Science"),
        CareerCategory("cat_gov", "Government & Policy", "Public Policy, Civil Services & Law", 7, "AccountBalance"),
        CareerCategory("cat_creative", "Creative Arts", "Game Design, Animation & Sound Engineering", 9, "Brush"),
        CareerCategory("cat_hospitality", "Hospitality", "Tourism, Event Planning & Global Management", 6, "Hotel")
    )

    private val staticCareers = listOf(
        CareerItem(
            id = "career_sw_dev",
            title = "Software Developer",
            category = "Technology",
            shortDescription = "Design, construct, and deploy reliable software platforms, APIs, and mobile applications.",
            overview = "Software Developers architect and write the code that powers modern civilization — from smartphone operating systems to planetary-scale cloud infrastructures.",
            whatYouDo = listOf(
                "Write clean, modular Kotlin, TypeScript, or Python code",
                "Design scalable distributed databases and microservice architectures",
                "Collaborate with product designers, QA engineers, and DevOps specialists",
                "Debug complex concurrent systems and optimize computational performance"
            ),
            requiredSkills = listOf("Data Structures & Algorithms", "Kotlin / Java", "System Design", "Git & CI/CD", "Cloud Architecture"),
            educationPathway = "Bachelor's degree in Computer Science, Software Engineering, or demonstrable open-source portfolio with accredited certifications.",
            typicalSalaryRange = "₹8,00,000 – ₹35,00,000+ per annum (Global remote: $70k - $180k+)",
            typicalWorkEnvironment = "Modern tech hubs, high-autonomy distributed remote teams, agile collaborative sprints.",
            keyTools = listOf("Android Studio / VS Code", "Git / GitHub", "Docker & Kubernetes", "PostgreSQL", "Figma"),
            careerRoadmap = listOf(
                "Junior Engineer: Focus on code craft, unit testing, and component architecture",
                "Senior Engineer: Architect complex multi-tier services and mentor peers",
                "Staff / Principal Engineer or Tech Lead: Drive technical roadmap and cross-team strategy"
            ),
            learningPath = listOf(
                "1. Foundations of Algorithmic Thinking",
                "2. Modern Android App Development with Jetpack Compose",
                "3. Cloud Native Microservices & Databases"
            ),
            relatedCareerTitles = listOf("AI Engineer", "Cloud Solutions Architect", "UI/UX Designer", "DevOps Specialist")
        ),
        CareerItem(
            id = "career_ui_ux",
            title = "UI/UX Designer",
            category = "Design",
            shortDescription = "Transform complex digital workflows into intuitive, beautiful, and accessible human experiences.",
            overview = "UI/UX Designers merge psychology, visual aesthetics, typography, and interaction patterns to ensure digital products are joyfully usable.",
            whatYouDo = listOf(
                "Conduct user research interviews and synthesize behavioral insights",
                "Create wireframes, high-fidelity mockups, and clickable prototypes in Figma",
                "Develop comprehensive design systems with tokens and accessibility guidelines",
                "Collaborate closely with frontend engineers during design implementation"
            ),
            requiredSkills = listOf("Design Systems", "Figma & Prototyping", "User Research", "Interaction Design", "Accessibility (WCAG)"),
            educationPathway = "Degree in Design, Human-Computer Interaction (HCI), Fine Arts, or an exceptional portfolio of shipped case studies.",
            typicalSalaryRange = "₹6,50,000 – ₹28,00,000+ per annum",
            typicalWorkEnvironment = "Design studios, tech scale-ups, multidisciplinary product pods.",
            keyTools = listOf("Figma", "FigJam", "Adobe Creative Suite", "Framer", "Lottie"),
            careerRoadmap = listOf(
                "Product Designer: Master craft, wireframes, and design system components",
                "Senior UX Lead: Drive end-to-end product discovery and user testing",
                "Head of Design: Lead brand visual identity, design vision, and design ops"
            ),
            learningPath = listOf(
                "1. Human-Centered Design Thinking",
                "2. Mastering Figma & Design Token Architectures",
                "3. Mobile Accessibility & Micro-Interactions"
            ),
            relatedCareerTitles = listOf("Product Manager", "Design Technologist", "Creative Director", "UX Researcher")
        ),
        CareerItem(
            id = "career_data_analyst",
            title = "Data Analyst",
            category = "Technology",
            shortDescription = "Synthesize voluminous raw datasets into actionable strategic intelligence and interactive dashboards.",
            overview = "Data Analysts serve as the strategic compass of organizations, uncovering trends, anomalies, and opportunities hidden within millions of data records.",
            whatYouDo = listOf(
                "Author performant SQL queries to extract data across data warehouses",
                "Construct automated executive dashboards using PowerBI, Tableau, or Looker",
                "Run statistical regression models and A/B test experiments",
                "Present narrative data presentations to leadership for decisive actions"
            ),
            requiredSkills = listOf("Advanced SQL", "Python / R (Pandas, NumPy)", "Tableau / PowerBI", "Statistical Inference", "Business Strategy"),
            educationPathway = "Degree in Mathematics, Statistics, Computer Science, Economics, or Data Analytics certification.",
            typicalSalaryRange = "₹6,00,000 – ₹22,00,000+ per annum",
            typicalWorkEnvironment = "Analytics consultancies, tech companies, banking, enterprise healthcare.",
            keyTools = listOf("PostgreSQL / BigQuery", "Python & Jupyter", "Tableau / Looker", "Excel Power Query", "dbt"),
            careerRoadmap = listOf(
                "Junior Analyst: Master data cleansing, ETL hygiene, and reporting dashboards",
                "Senior Analytics Partner: Lead domain-level data investigations and predictive models",
                "Analytics Director: Guide institutional data governance and intelligence strategy"
            ),
            learningPath = listOf(
                "1. SQL for High-Volume Data Analysis",
                "2. Statistical Analysis & Python",
                "3. Visual Data Storytelling & Dashboard Design"
            ),
            relatedCareerTitles = listOf("Data Scientist", "Business Intelligence Engineer", "Financial Analyst", "AI Engineer")
        ),
        CareerItem(
            id = "career_ai_engineer",
            title = "AI & Machine Learning Engineer",
            category = "Technology",
            shortDescription = "Build, train, and deploy generative AI models, neural networks, and computer vision systems.",
            overview = "AI Engineers develop the models and algorithmic pipelines that enable software to understand natural language, perceive visual scenes, and generate solutions.",
            whatYouDo = listOf(
                "Fine-tune Large Language Models (LLMs) and build Retrieval-Augmented Generation (RAG) systems",
                "Train supervised and reinforcement learning models on multi-modal datasets",
                "Optimize model inference latency on edge devices and GPU clusters",
                "Implement safety evaluations, alignment guardrails, and automated benchmarking"
            ),
            requiredSkills = listOf("PyTorch / TensorFlow", "Python & C++", "Linear Algebra & Calculus", "LLM Orchestration & Embeddings", "MLOps"),
            educationPathway = "Degree in Computer Science, Artificial Intelligence, Computational Mathematics, or demonstrable ML research publications.",
            typicalSalaryRange = "₹12,00,000 – ₹45,00,000+ per annum",
            typicalWorkEnvironment = "AI research labs, frontier tech companies, autonomous mobility startups.",
            keyTools = listOf("PyTorch", "HuggingFace", "LangChain / LlamaIndex", "NVIDIA CUDA", "Weights & Biases"),
            careerRoadmap = listOf(
                "ML Engineer: Model pipeline development and feature engineering",
                "Senior AI Systems Architect: Model fine-tuning, latency optimization, and infrastructure",
                "Chief AI Scientist: Pioneering new architectures and foundational models"
            ),
            learningPath = listOf(
                "1. Mathematical Foundations of Machine Learning",
                "2. Deep Learning with PyTorch",
                "3. Building Production LLM Applications"
            ),
            relatedCareerTitles = listOf("Software Developer", "Data Scientist", "Research Scientist", "Robotics Engineer")
        ),
        CareerItem(
            id = "career_digital_marketer",
            title = "Digital Marketing Strategist",
            category = "Business & Finance",
            shortDescription = "Drive customer acquisition, brand storytelling, and algorithmic performance across digital channels.",
            overview = "Digital Marketing Strategists use data-driven experimentation, creative messaging, and marketing technology to build global brands and communities.",
            whatYouDo = listOf(
                "Plan multi-channel performance marketing campaigns across search and social",
                "Optimize conversion funnels, search engine ranking (SEO), and content reach",
                "Analyze user acquisition costs, lifetime value, and return on ad spend",
                "Develop viral storytelling campaigns and creator partnerships"
            ),
            requiredSkills = listOf("SEO & Growth Hacking", "Google Analytics 4", "Performance Advertising", "Copywriting & Storytelling", "A/B Testing"),
            educationPathway = "Degree in Marketing, Communications, Business, or proven track record of growing digital audiences.",
            typicalSalaryRange = "₹5,50,000 – ₹20,00,000+ per annum",
            typicalWorkEnvironment = "Digital marketing agencies, direct-to-consumer startups, fast-growing tech brands.",
            keyTools = listOf("Google Ads", "Meta Ads Manager", "SEMrush", "Google Analytics 4", "HubSpot"),
            careerRoadmap = listOf(
                "Growth Specialist: Hands-on campaign execution and ad optimization",
                "Growth Marketing Lead: Strategic budget allocation and full-funnel expansion",
                "Chief Marketing Officer (CMO): Overall brand narrative, growth, and market expansion"
            ),
            learningPath = listOf(
                "1. Digital Acquisition & Algorithmic Growth",
                "2. Advanced SEO & Content Strategy",
                "3. Marketing Analytics & Funnel Optimization"
            ),
            relatedCareerTitles = listOf("Brand Strategist", "Product Marketing Manager", "Content Director", "Entrepreneur")
        ),
        CareerItem(
            id = "career_entrepreneur",
            title = "Tech Entrepreneur / Founder",
            category = "Entrepreneurship",
            shortDescription = "Identify unmet human needs, assemble exceptional teams, and build enduring innovative ventures.",
            overview = "Entrepreneurs are visionaries who turn nascent ideas into functional businesses, managing product, capital, talent, and growth against all odds.",
            whatYouDo = listOf(
                "Identify high-conviction market opportunities and customer pain points",
                "Build initial minimum viable products (MVP) and validate product-market fit",
                "Recruit world-class early talent and instill vibrant culture",
                "Fundraise from venture capital, angels, or bootstrap via customer revenue"
            ),
            requiredSkills = listOf("Venture Ideation", "Unit Economics & Finance", "Team Leadership", "Product Discovery", "Resilience & Grit"),
            educationPathway = "Any educational background; fueled by multidisciplinary curiosity, problem solving, and relentless execution.",
            typicalSalaryRange = "Variable: Equity ownership, founder distributions, and enterprise valuation creation.",
            typicalWorkEnvironment = "Startup incubators, collaborative maker spaces, dynamic market fronts.",
            keyTools = listOf("Notion", "Linear", "Stripe", "Figma", "AWS / Google Cloud"),
            careerRoadmap = listOf(
                "First-Time Founder: Customer discovery, prototyping, and early survival",
                "Growth-Stage CEO: Scaling operations, hiring leaders, and capital allocation",
                "Serial Entrepreneur & Investor: Founding new ventures and backing upcoming founders"
            ),
            learningPath = listOf(
                "1. Zero to One: Validating Startup Ideas",
                "2. Financial Modeling & Venture Capital Basics",
                "3. Building and Leading High-Performance Teams"
            ),
            relatedCareerTitles = listOf("Product Manager", "Software Developer", "Venture Capitalist", "Strategy Consultant")
        )
    )

    override val allCareers: Flow<List<CareerItem>> = flowOf(staticCareers)

    override val bookmarkedCareers: Flow<List<CareerItem>> = database.maargyaDao().getAllBookmarks()
        .map { bookmarks ->
            val bookmarkedIds = bookmarks.filter { it.itemType == "CAREER" }.map { it.itemId }.toSet()
            staticCareers.filter { it.id in bookmarkedIds }.map { it.copy(isBookmarked = true) }
        }

    override fun getCategories(): List<CareerCategory> = staticCategories

    override suspend fun getCareerById(id: String): CareerItem? {
        val item = staticCareers.find { it.id == id } ?: return null
        return item
    }

    override suspend fun toggleBookmark(careerId: String, title: String, subtitle: String): Boolean {
        // Toggle in Room
        val existing = database.maargyaDao().getCareerLabDay(1) // simple check or delete
        val bookmarkedId = "career_$careerId"
        database.maargyaDao().insertBookmark(
            BookmarkEntity(
                id = bookmarkedId,
                itemType = "CAREER",
                itemId = careerId,
                title = title,
                subtitle = subtitle,
                savedAt = System.currentTimeMillis()
            )
        )
        return true
    }

    override fun isCareerBookmarked(id: String): Flow<Boolean> {
        return database.maargyaDao().isItemBookmarked(id)
    }

    override fun searchCareers(query: String, category: String?): List<CareerItem> {
        return staticCareers.filter { item ->
            val matchesCategory = category.isNullOrBlank() || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.shortDescription.contains(query, ignoreCase = true) ||
                    item.requiredSkills.any { it.contains(query, ignoreCase = true) } ||
                    item.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }
}
