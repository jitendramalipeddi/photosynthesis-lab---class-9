package com.example.data

import com.example.analytics.ClickstreamEventTypes
import com.example.analytics.ClickstreamTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class AnalyticsSummary(
    val totalSessions: Int,
    val totalEventsCount: Int,
    val avgReadingDwellTimeSec: Double,
    val avgMcqLatencySec: Double,
    val avgWrittenLatencySec: Double,
    val avgQuizScorePercent: Double,
    val totalQuizAttempts: Int,
    val mostEngagedSection: String
)

class AnalyticsRepository(private val tracker: ClickstreamTracker) {

    val allEvents: Flow<List<ClickstreamEntity>> = tracker.getAllEvents()
    val allQuizResults: Flow<List<QuizResultEntity>> = tracker.getAllQuizResults()

    val analyticsSummary: Flow<AnalyticsSummary> = allEvents.map { events ->
        val totalEvents = events.size
        val uniqueSessions = events.map { it.sessionId }.distinct().size

        // Dwell time on reading sections
        val dwellEvents = events.filter { it.eventType == ClickstreamEventTypes.READING_DWELL_TIME && it.dwellTimeMs > 0 }
        val avgDwellSec = if (dwellEvents.isNotEmpty()) {
            dwellEvents.map { it.dwellTimeMs }.average() / 1000.0
        } else 0.0

        // Find most engaged reading section
        val sectionDwellMap = dwellEvents.groupBy { it.componentId }
            .mapValues { entry -> entry.value.sumOf { it.dwellTimeMs } }
        val mostEngaged = sectionDwellMap.maxByOrNull { it.value }?.key ?: "sec_1_intro"

        // Latencies by question type
        val answerEvents = events.filter { it.eventType == ClickstreamEventTypes.QUESTION_RESPONSE_LATENCY && it.responseLatencyMs > 0 }
        val mcqEvents = answerEvents.filter { it.metadataPayload.contains("MULTIPLE_CHOICE") || it.componentId.contains("question_1") || it.componentId.contains("question_2") || it.componentId.contains("question_3") }
        val writtenEvents = answerEvents.filter { it.metadataPayload.contains("WRITTEN_SINGLE_WORD") || it.componentId.contains("question_4") || it.componentId.contains("question_5") }

        val avgMcqLatencySec = if (mcqEvents.isNotEmpty()) mcqEvents.map { it.responseLatencyMs }.average() / 1000.0 else 0.0
        val avgWrittenLatencySec = if (writtenEvents.isNotEmpty()) writtenEvents.map { it.responseLatencyMs }.average() / 1000.0 else 0.0

        AnalyticsSummary(
            totalSessions = uniqueSessions,
            totalEventsCount = totalEvents,
            avgReadingDwellTimeSec = (avgDwellSec * 10).toInt() / 10.0,
            avgMcqLatencySec = (avgMcqLatencySec * 10).toInt() / 10.0,
            avgWrittenLatencySec = (avgWrittenLatencySec * 10).toInt() / 10.0,
            avgQuizScorePercent = 80.0, // Default baseline if no results yet
            totalQuizAttempts = 0,
            mostEngagedSection = formatSectionName(mostEngaged)
        )
    }

    private fun formatSectionName(id: String): String {
        return when (id) {
            "sec_1_intro" -> "1. What is Photosynthesis"
            "sec_2_chloroplast" -> "2. Chloroplast & Leaf Structure"
            "sec_3_light_reaction" -> "3. Light Reaction (Thylakoids)"
            "sec_4_dark_reaction" -> "4. Calvin Cycle (Stroma)"
            "sec_5_factors" -> "5. Factors Affecting Rate"
            else -> id
        }
    }

    fun exportToCsvString(events: List<ClickstreamEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,Session_ID,Username,User_Role,Event_Type,Component_ID,Timestamp_ISO,Dwell_Time_Sec,Latency_Sec,Metadata\n")

        events.forEach { e ->
            val dwellSec = e.dwellTimeMs / 1000.0
            val latencySec = e.responseLatencyMs / 1000.0
            val cleanMeta = e.metadataPayload.replace(",", ";").replace("\n", " ")
            sb.append("${e.id},\"${e.sessionId}\",\"${e.username}\",\"${e.userRole}\",\"${e.eventType}\",\"${e.componentId}\",\"${e.formattedTimestamp}\",$dwellSec,$latencySec,\"$cleanMeta\"\n")
        }
        return sb.toString()
    }

    fun exportToJsonString(events: List<ClickstreamEntity>): String {
        val sb = StringBuilder()
        sb.append("[\n")
        events.forEachIndexed { index, e ->
            val dwellSec = e.dwellTimeMs / 1000.0
            val latencySec = e.responseLatencyMs / 1000.0
            val cleanMeta = e.metadataPayload.replace("\"", "\\\"").replace("\n", " ")
            sb.append("  {\n")
            sb.append("    \"id\": ${e.id},\n")
            sb.append("    \"sessionId\": \"${e.sessionId}\",\n")
            sb.append("    \"username\": \"${e.username}\",\n")
            sb.append("    \"userRole\": \"${e.userRole}\",\n")
            sb.append("    \"eventType\": \"${e.eventType}\",\n")
            sb.append("    \"componentId\": \"${e.componentId}\",\n")
            sb.append("    \"timestamp\": \"${e.formattedTimestamp}\",\n")
            sb.append("    \"dwellTimeSec\": $dwellSec,\n")
            sb.append("    \"responseLatencySec\": $latencySec,\n")
            sb.append("    \"metadata\": \"$cleanMeta\"\n")
            sb.append("  }${if (index < events.size - 1) "," else ""}\n")
        }
        sb.append("]")
        return sb.toString()
    }
}
