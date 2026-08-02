package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClickstreamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ClickstreamEntity): Long

    @Query("SELECT * FROM clickstream_events ORDER BY timestampMs DESC")
    fun getAllEvents(): Flow<List<ClickstreamEntity>>

    @Query("SELECT * FROM clickstream_events WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun getEventsBySession(sessionId: String): Flow<List<ClickstreamEntity>>

    @Query("SELECT COUNT(*) FROM clickstream_events")
    fun getEventCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT sessionId) FROM clickstream_events")
    fun getUniqueSessionCount(): Flow<Int>

    @Query("DELETE FROM clickstream_events")
    suspend fun clearAllEvents()
}

@Dao
interface QuizResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResultEntity): Long

    @Query("SELECT * FROM quiz_results ORDER BY timestampMs DESC")
    fun getAllQuizResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getQuizResultBySession(sessionId: String): QuizResultEntity?

    @Query("DELETE FROM quiz_results")
    suspend fun clearAllResults()
}
