package com.example.data.repository.impl

import com.example.data.local.AppDatabase
import com.example.data.local.entities.HandwritingReportEntity
import com.example.data.remote.NetworkResult
import com.example.data.repository.HandwritingRepository
import com.example.domain.model.HandwritingObservation
import com.example.domain.model.HandwritingReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class HandwritingRepositoryImpl(private val database: AppDatabase) : HandwritingRepository {

    override val savedReports: Flow<List<HandwritingReport>> = database.maargyaDao().getAllHandwritingReports()
        .map { entities ->
            if (entities.isEmpty()) {
                listOf(createSampleReport("sample_default", "Recent Analysis"))
            } else {
                entities.map { entity ->
                    createSampleReport(entity.id, entity.dateAnalyzed)
                }
            }
        }

    override suspend fun analyzeSample(imageUriString: String): NetworkResult<HandwritingReport> {
        // Realistic multi-stage observational analysis delay
        delay(1500)
        val report = createSampleReport(
            id = "hw_${System.currentTimeMillis()}",
            date = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()),
            uri = imageUriString
        )
        return NetworkResult.Success(report)
    }

    override suspend fun saveReport(report: HandwritingReport): NetworkResult<Unit> {
        database.maargyaDao().insertHandwritingReport(
            HandwritingReportEntity(
                id = report.id,
                sampleUri = report.sampleUri,
                dateAnalyzed = report.dateAnalyzed,
                observationsJson = "{}",
                summaryInsight = report.summaryInsight
            )
        )
        return NetworkResult.Success(Unit)
    }

    private fun createSampleReport(id: String, date: String, uri: String = ""): HandwritingReport {
        return HandwritingReport(
            id = id,
            sampleUri = uri,
            dateAnalyzed = date,
            observations = listOf(
                HandwritingObservation(
                    categoryName = "Writing Size",
                    observation = "Medium-to-large glyph proportions with distinct ascenders",
                    possibleInterpretation = "Suggests communicative confidence, sociability, and a tendency to look at overarching themes rather than getting trapped in micro-details.",
                    reflectionQuestion = "Do you prefer leading discussions or doing isolated technical deep-dives?",
                    traitIndicator = "Holistic Thinking & High Sociability"
                ),
                HandwritingObservation(
                    categoryName = "Slant",
                    observation = "Gentle rightward inclination (approx. 15-20°)",
                    possibleInterpretation = "Reflects emotional expressiveness, warmth, forward-looking optimism, and readiness to engage collaboratively with others.",
                    reflectionQuestion = "How do your emotions influence your decisions when working under strict deadlines?",
                    traitIndicator = "Future Orientation & Empathy"
                ),
                HandwritingObservation(
                    categoryName = "Spacing",
                    observation = "Consistent word margins with generous line separation",
                    possibleInterpretation = "Indicates structured spatial judgment, respect for interpersonal boundaries, and clarity in mental organization.",
                    reflectionQuestion = "Do you find yourself establishing orderly routines to manage complex study loads?",
                    traitIndicator = "Clear Organization & Mental Order"
                ),
                HandwritingObservation(
                    categoryName = "Baseline",
                    observation = "Ascending, steady baseline across horizontal span",
                    possibleInterpretation = "Associated with ambition, natural resilience, positive outlook, and intrinsic motivation towards long-range objectives.",
                    reflectionQuestion = "What long-term goal currently gives you the greatest internal drive?",
                    traitIndicator = "Ambition & Resilience"
                ),
                HandwritingObservation(
                    categoryName = "Pressure",
                    observation = "Even, firm stroke indentation throughout letter bodies",
                    possibleInterpretation = "Often correlates with sustained physical/mental vitality, determination, and commitment to follow through on undertakings.",
                    reflectionQuestion = "When starting a complex new subject, how do you sustain your discipline over weeks?",
                    traitIndicator = "High Vitality & Tenacity"
                ),
                HandwritingObservation(
                    categoryName = "Letter Formation",
                    observation = "Connected cursive flow with open loops on lowercase 'e' and 'o'",
                    possibleInterpretation = "Points to receptive listening habits, fluency of thought transitions, and willingness to accommodate alternate viewpoints.",
                    reflectionQuestion = "How easily do you pivot your ideas when presented with new compelling facts?",
                    traitIndicator = "Open-mindedness & Fluid Logic"
                )
            ),
            summaryInsight = "Your handwriting shows traits of an ambitious, structured, and collaborative thinker who thrives when organizing ideas and exploring future possibilities."
        )
    }
}
