package com.example.model

enum class UserRole {
    STUDENT,
    ADMIN
}

data class UserSession(
    val username: String,
    val role: UserRole,
    val sessionId: String,
    val loginTimeMs: Long = System.currentTimeMillis()
)

data class VocabularyTerm(
    val id: String,
    val term: String,
    val pronunciation: String,
    val definition: String,
    val example: String
)

data class ReadingSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val keyConcept: String,
    val contentMarkdown: String,
    val bulletPoints: List<String>,
    val videoUrl: String? = null,
    val videoTitle: String? = null,
    val mediaType: String? = "DIAGRAM" // DIAGRAM, ANIMATION, VIDEO
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    WRITTEN_SINGLE_WORD
}

data class QuizQuestion(
    val id: Int,
    val type: QuestionType,
    val questionText: String,
    val options: List<String> = emptyList(), // Only for MULTIPLE_CHOICE
    val correctAnswer: String, // Clean string or option text
    val explanation: String,
    val hint: String
)
