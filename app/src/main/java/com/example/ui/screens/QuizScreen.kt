package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.ClickstreamTracker
import com.example.model.PhotosynthesisData
import com.example.model.QuestionType
import com.example.model.QuizQuestion

@Composable
fun QuizScreen(
    tracker: ClickstreamTracker,
    onQuizFinished: () -> Unit,
    onBackToReading: () -> Unit
) {
    val questions = PhotosynthesisData.quizQuestions
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val currentQuestion = questions[currentQuestionIndex]

    // User inputs
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var writtenAnswerText by remember { mutableStateOf("") }

    // Answer State
    var isSubmitted by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var lastLatencyMs by remember { mutableStateOf(0L) }

    // Quiz Completion Totals
    var totalScore by remember { mutableStateOf(0) }
    var isQuizCompleted by remember { mutableStateOf(false) }

    val mcqLatencies = remember { mutableStateListOf<Long>() }
    val writtenLatencies = remember { mutableStateListOf<Long>() }
    val quizStartTimeMs = remember { mutableStateOf(System.currentTimeMillis()) }

    // Record Overall Quiz Start
    LaunchedEffect(Unit) {
        tracker.onQuizStarted()
    }

    // Record Question Start timestamp whenever question index changes
    LaunchedEffect(currentQuestionIndex) {
        if (!isQuizCompleted) {
            tracker.onStartQuestion(currentQuestion.id, currentQuestion.questionText)
            selectedOption = null
            writtenAnswerText = ""
            isSubmitted = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Top Bar
        Surface(
            color = Color(0xFF1E293B),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    tracker.onNavigation("reading_material_screen")
                    onBackToReading()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Photosynthesis Assessment Quiz",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        if (!isQuizCompleted) {
            // Quiz Active View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress Bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            color = Color(0xFF81C784),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE) "MCQ Format" else "Written Single Word",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1) / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFF334155)
                    )
                }

                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQuestion.questionText,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Render options or written text field
                        if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE) {
                            currentQuestion.options.forEach { option ->
                                val isSelected = selectedOption == option
                                Surface(
                                    color = if (isSelected) Color(0xFF0F2D1E) else Color(0xFF0F172A),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color(0xFF4CAF50) else Color(0xFF334155),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = !isSubmitted) {
                                            selectedOption = option
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { if (!isSubmitted) selectedOption = option },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF4CAF50))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = option,
                                            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        } else {
                            val keyboardController = LocalSoftwareKeyboardController.current
                            // Written Single Word Input
                            OutlinedTextField(
                                value = writtenAnswerText,
                                onValueChange = { if (!isSubmitted) writtenAnswerText = it },
                                label = { Text("Type Single Word Answer", color = Color(0xFF94A3B8)) },
                                placeholder = { Text("e.g. Oxygen or Stomata", color = Color.Gray) },
                                singleLine = true,
                                enabled = !isSubmitted,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedContainerColor = Color(0xFF0F172A),
                                    unfocusedContainerColor = Color(0xFF0F172A)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Answer Button
                        if (!isSubmitted) {
                            val canSubmit = if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE)
                                selectedOption != null
                            else
                                writtenAnswerText.trim().isNotEmpty()

                            Button(
                                onClick = {
                                    val answerString = if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE)
                                        selectedOption ?: ""
                                    else
                                        writtenAnswerText.trim()

                                    // Verify Answer Correctness
                                    isAnswerCorrect = if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE) {
                                        answerString.equals(currentQuestion.correctAnswer, ignoreCase = true)
                                    } else {
                                        answerString.equals(currentQuestion.correctAnswer, ignoreCase = true) ||
                                                (currentQuestion.correctAnswer.contains("Oxygen") && answerString.equals("O2", ignoreCase = true)) ||
                                                (currentQuestion.correctAnswer.contains("Stomata") && answerString.equals("Stoma", ignoreCase = true))
                                    }

                                    if (isAnswerCorrect) totalScore += 1

                                    // Log Latency & Answer Clickstream
                                    lastLatencyMs = tracker.onAnswerQuestion(
                                        questionId = currentQuestion.id,
                                        questionType = currentQuestion.type.name,
                                        selectedAnswer = answerString,
                                        isCorrect = isAnswerCorrect
                                    )

                                    if (currentQuestion.type == QuestionType.MULTIPLE_CHOICE) {
                                        mcqLatencies.add(lastLatencyMs)
                                    } else {
                                        writtenLatencies.add(lastLatencyMs)
                                    }

                                    isSubmitted = true
                                },
                                enabled = canSubmit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Submit Answer & Record Latency ➔",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Instant Feedback Card after submission
                if (isSubmitted) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAnswerCorrect) Color(0xFF0F2D1E) else Color(0xFF3F1315)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (isAnswerCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAnswerCorrect) "Correct Answer!" else "Incorrect",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Latency UI removed to hide from user
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Correct Answer: ${currentQuestion.correctAnswer}",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentQuestion.explanation,
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex += 1
                                    } else {
                                        // Final Submit
                                        isQuizCompleted = true
                                        val totalDuration = System.currentTimeMillis() - quizStartTimeMs.value
                                        val mcqAvg = if (mcqLatencies.isNotEmpty()) mcqLatencies.average().toLong() else 0L
                                        val writtenAvg = if (writtenLatencies.isNotEmpty()) writtenLatencies.average().toLong() else 0L

                                        tracker.onQuizCompleted(
                                            score = totalScore,
                                            totalQuestions = questions.size,
                                            mcqAvgLatencyMs = mcqAvg,
                                            writtenAvgLatencyMs = writtenAvg,
                                            totalDurationMs = totalDuration,
                                            answersSummary = "Score $totalScore/5"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (currentQuestionIndex < questions.size - 1) "Next Question ➔" else "View Assessment Results 🏆",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Quiz Completed Result Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = Color(0xFF2E7D32),
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🏆", fontSize = 40.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Assessment Completed!",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Score: $totalScore / ${questions.size} (${(totalScore * 100) / questions.size}%)",
                    color = Color(0xFF81C784),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cognitive Metrics Breakdown (Hidden from user)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        tracker.onQuizExit(totalScore)
                        tracker.onNavigation("reading_material_screen")
                        onBackToReading()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Return to Reading Material 🌿", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFFCBD5E1), fontSize = 12.sp)
        Text(text = value, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
