package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clickstream_events")
data class ClickstreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sessionId: String,
    val username: String,
    val userRole: String,
    val eventType: String,
    val componentId: String,
    val timestampMs: Long,
    val formattedTimestamp: String,
    val dwellTimeMs: Long = 0L,
    val responseLatencyMs: Long = 0L,
    val metadataPayload: String = ""
)

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sessionId: String,
    val username: String,
    val timestampMs: Long,
    val formattedDate: String,
    val score: Int,
    val totalQuestions: Int = 5,
    val mcqAvgLatencyMs: Long = 0L,
    val writtenAvgLatencyMs: Long = 0L,
    val totalQuizTimeMs: Long = 0L,
    val answersSummaryJson: String = ""
)
