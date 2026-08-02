package com.example.analytics

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.ClickstreamEntity
import com.example.data.QuizResultEntity
import com.example.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object ClickstreamEventTypes {
    const val LOGIN = "LOGIN"
    const val READING_SECTION_VIEW = "READING_SECTION_VIEW"
    const val READING_DWELL_TIME = "READING_DWELL_TIME"
    const val MEDIA_INTERACTION = "MEDIA_INTERACTION"
    const val VOCAB_CARD_FLIP = "VOCAB_CARD_FLIP"
    const val QUIZ_START = "QUIZ_START"
    const val QUESTION_VIEW = "QUESTION_VIEW"
    const val QUESTION_ANSWER_SELECT = "QUESTION_ANSWER_SELECT"
    const val QUESTION_RESPONSE_LATENCY = "QUESTION_RESPONSE_LATENCY"
    const val QUESTION_FEEDBACK_VIEW = "QUESTION_FEEDBACK_VIEW"
    const val QUIZ_SUBMIT = "QUIZ_SUBMIT"
    const val NAVIGATION_CLICK = "NAVIGATION_CLICK"
    const val EXPORT_DATA_CLICK = "EXPORT_DATA_CLICK"
    const val LOGOUT = "LOGOUT"
}

class ClickstreamTracker private constructor(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val clickstreamDao = db.clickstreamDao()
    private val quizResultDao = db.quizResultDao()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    // Current Session State
    private var currentSessionId: String = UUID.randomUUID().toString().take(8)
    private var currentUsername: String = "Student_Guest"
    private var currentUserRole: UserRole = UserRole.STUDENT

    // Dwell Time Tracking
    private var sectionStartTimeMs: Long = 0L
    private var currentActiveSectionId: String? = null

    // Question Latency Tracking
    private var questionStartTimeMs: Long = 0L
    private var currentQuestionId: Int = -1

    private val _sessionEventCount = MutableStateFlow(0)
    val sessionEventCount: StateFlow<Int> = _sessionEventCount.asStateFlow()

    fun startSession(username: String, role: UserRole) {
        currentSessionId = "SESS-" + UUID.randomUUID().toString().take(8).uppercase()
        currentUsername = username.ifBlank { if (role == UserRole.STUDENT) "Student_9th" else "Admin_User" }
        currentUserRole = role
        _sessionEventCount.value = 0

        logEvent(
            eventType = ClickstreamEventTypes.LOGIN,
            componentId = "login_screen",
            metadata = "User logged in as ${role.name}"
        )
    }

    fun getCurrentSessionId(): String = currentSessionId
    fun getCurrentUsername(): String = currentUsername
    fun getCurrentUserRole(): UserRole = currentUserRole

    fun logEvent(
        eventType: String,
        componentId: String,
        dwellTimeMs: Long = 0L,
        responseLatencyMs: Long = 0L,
        metadata: String = ""
    ) {
        val now = System.currentTimeMillis()
        val formatted = dateFormat.format(Date(now))

        val entity = ClickstreamEntity(
            sessionId = currentSessionId,
            username = currentUsername,
            userRole = currentUserRole.name,
            eventType = eventType,
            componentId = componentId,
            timestampMs = now,
            formattedTimestamp = formatted,
            dwellTimeMs = dwellTimeMs,
            responseLatencyMs = responseLatencyMs,
            metadataPayload = metadata
        )

        scope.launch {
            clickstreamDao.insertEvent(entity)
            _sessionEventCount.value += 1
        }
    }

    // --- Granular Helpers ---

    fun onEnterSection(sectionId: String, sectionTitle: String) {
        // Record dwell time for previous section if active
        onLeaveCurrentSection()

        sectionStartTimeMs = System.currentTimeMillis()
        currentActiveSectionId = sectionId

        logEvent(
            eventType = ClickstreamEventTypes.READING_SECTION_VIEW,
            componentId = sectionId,
            metadata = "Started reading: $sectionTitle"
        )
    }

    fun onLeaveCurrentSection() {
        if (currentActiveSectionId != null && sectionStartTimeMs > 0L) {
            val dwell = System.currentTimeMillis() - sectionStartTimeMs
            if (dwell > 300) { // filter micro blips
                logEvent(
                    eventType = ClickstreamEventTypes.READING_DWELL_TIME,
                    componentId = currentActiveSectionId!!,
                    dwellTimeMs = dwell,
                    metadata = "Dwell time on section: ${dwell / 1000.0}s"
                )
            }
            sectionStartTimeMs = 0L
            currentActiveSectionId = null
        }
    }

    fun onMediaInteraction(mediaType: String, mediaTitle: String, action: String) {
        logEvent(
            eventType = ClickstreamEventTypes.MEDIA_INTERACTION,
            componentId = "media_$mediaType",
            metadata = "Media Action: $action on '$mediaTitle'"
        )
    }

    fun onVocabCardFlipped(term: String, isFlipped: Boolean) {
        logEvent(
            eventType = ClickstreamEventTypes.VOCAB_CARD_FLIP,
            componentId = "vocab_$term",
            metadata = "Flashcard '$term' ${if (isFlipped) "Flipped to Definition" else "Flipped to Front"}"
        )
    }

    fun onStartQuestion(questionId: Int, questionText: String) {
        questionStartTimeMs = System.currentTimeMillis()
        currentQuestionId = questionId

        logEvent(
            eventType = ClickstreamEventTypes.QUESTION_VIEW,
            componentId = "question_$questionId",
            metadata = "Question $questionId displayed: ${questionText.take(40)}..."
        )
    }

    fun onAnswerQuestion(
        questionId: Int,
        questionType: String,
        selectedAnswer: String,
        isCorrect: Boolean
    ): Long {
        val now = System.currentTimeMillis()
        val latency = if (questionStartTimeMs > 0L) (now - questionStartTimeMs) else 0L

        logEvent(
            eventType = ClickstreamEventTypes.QUESTION_ANSWER_SELECT,
            componentId = "question_$questionId",
            responseLatencyMs = latency,
            metadata = "Type: $questionType | Submitted: '$selectedAnswer' | Result: ${if (isCorrect) "CORRECT" else "INCORRECT"}"
        )

        logEvent(
            eventType = ClickstreamEventTypes.QUESTION_RESPONSE_LATENCY,
            componentId = "question_$questionId",
            responseLatencyMs = latency,
            metadata = "Cognitive Latency: ${latency / 1000.0}s ($questionType)"
        )

        questionStartTimeMs = 0L
        return latency
    }

    fun onQuizCompleted(
        score: Int,
        totalQuestions: Int,
        mcqAvgLatencyMs: Long,
        writtenAvgLatencyMs: Long,
        totalDurationMs: Long,
        answersSummary: String
    ) {
        logEvent(
            eventType = ClickstreamEventTypes.QUIZ_SUBMIT,
            componentId = "quiz_summary",
            dwellTimeMs = totalDurationMs,
            metadata = "Score: $score/$totalQuestions | MCQ Latency Avg: ${mcqAvgLatencyMs/1000.0}s | Written Latency Avg: ${writtenAvgLatencyMs/1000.0}s"
        )

        val formattedDate = dateFormat.format(Date())
        val quizResult = QuizResultEntity(
            sessionId = currentSessionId,
            username = currentUsername,
            timestampMs = System.currentTimeMillis(),
            formattedDate = formattedDate,
            score = score,
            totalQuestions = totalQuestions,
            mcqAvgLatencyMs = mcqAvgLatencyMs,
            writtenAvgLatencyMs = writtenAvgLatencyMs,
            totalQuizTimeMs = totalDurationMs,
            answersSummaryJson = answersSummary
        )

        scope.launch {
            quizResultDao.insertQuizResult(quizResult)
        }
    }

    fun onNavigation(destination: String) {
        onLeaveCurrentSection()
        logEvent(
            eventType = ClickstreamEventTypes.NAVIGATION_CLICK,
            componentId = "nav_$destination",
            metadata = "Navigated to $destination"
        )
    }

    fun getAllEvents(): Flow<List<ClickstreamEntity>> = clickstreamDao.getAllEvents()
    fun getAllQuizResults(): Flow<List<QuizResultEntity>> = quizResultDao.getAllQuizResults()

    suspend fun clearAllData() {
        clickstreamDao.clearAllEvents()
        quizResultDao.clearAllResults()
    }

    companion object {
        @Volatile
        private var instance: ClickstreamTracker? = null

        fun getInstance(context: Context): ClickstreamTracker {
            return instance ?: synchronized(this) {
                instance ?: ClickstreamTracker(context.applicationContext).also { instance = it }
            }
        }
    }
}
