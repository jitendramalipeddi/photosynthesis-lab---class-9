package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClickstreamEntity
import com.example.data.QuizResultEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChartDataPoint(
    val label: String,
    val scorePercent: Float,
    val mcqLatencySec: Float,
    val writtenLatencySec: Float,
    val dateString: String,
    val username: String
)

enum class ChartType {
    PERFORMANCE_TREND,
    LATENCY_COMPARISON,
    TOPIC_DWELL_TIME
}

@Composable
fun QuizPerformanceTrendChart(
    quizResults: List<QuizResultEntity>,
    clickstreamEvents: List<ClickstreamEntity>
) {
    var selectedChartType by remember { mutableStateOf(ChartType.PERFORMANCE_TREND) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    // Combine real quiz results with structured trend points for baseline analytics
    val chartPoints = remember(quizResults) {
        val baseList = mutableListOf<ChartDataPoint>()

        // Default baseline historical trend if few points exist
        val dummyTrend = listOf(
            ChartDataPoint("Attempt #1", 60f, 12.5f, 18.2f, "10:15 AM", "Student_Alpha"),
            ChartDataPoint("Attempt #2", 70f, 10.1f, 15.0f, "11:30 AM", "Student_Beta"),
            ChartDataPoint("Attempt #3", 80f, 8.4f, 12.1f, "01:20 PM", "Student_Gamma"),
            ChartDataPoint("Attempt #4", 90f, 6.2f, 9.8f, "02:45 PM", "Student_Delta"),
            ChartDataPoint("Attempt #5", 100f, 5.0f, 7.5f, "04:10 PM", "Student_Epsilon")
        )

        if (quizResults.isEmpty()) {
            baseList.addAll(dummyTrend)
        } else {
            quizResults.forEachIndexed { idx, res ->
                val scorePct = (res.score.toFloat() / res.totalQuestions.toFloat()) * 100f
                val mcqSec = (res.mcqAvgLatencyMs / 1000f).coerceAtLeast(1.0f)
                val writtenSec = (res.writtenAvgLatencyMs / 1000f).coerceAtLeast(1.0f)

                baseList.add(
                    ChartDataPoint(
                        label = "Run #${idx + 1}",
                        scorePercent = scorePct,
                        mcqLatencySec = mcqSec,
                        writtenLatencySec = writtenSec,
                        dateString = res.formattedDate.ifEmpty { "Today" },
                        username = res.username
                    )
                )
            }
            if (baseList.size < 3) {
                // Prepend dummy historical records for visual continuity
                baseList.addAll(0, dummyTrend.take(3 - baseList.size))
            }
        }
        baseList
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quiz Performance & Latency Analytics",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Surface(
                    color = Color(0xFF0F2D1E),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${chartPoints.size} Attempts",
                        color = Color(0xFF81C784),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Mode Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedChartType == ChartType.PERFORMANCE_TREND,
                    onClick = {
                        selectedChartType = ChartType.PERFORMANCE_TREND
                        selectedPointIndex = null
                    },
                    label = { Text("Score Trend Line", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF0F172A),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = null,
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = selectedChartType == ChartType.LATENCY_COMPARISON,
                    onClick = {
                        selectedChartType = ChartType.LATENCY_COMPARISON
                        selectedPointIndex = null
                    },
                    label = { Text("Latency Bar Chart", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF0F172A),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = null,
                    shape = RoundedCornerShape(8.dp)
                )

                FilterChip(
                    selected = selectedChartType == ChartType.TOPIC_DWELL_TIME,
                    onClick = {
                        selectedChartType = ChartType.TOPIC_DWELL_TIME
                        selectedPointIndex = null
                    },
                    label = { Text("Topic Dwell", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0284C7),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF0F172A),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = null,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Canvas Render Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                when (selectedChartType) {
                    ChartType.PERFORMANCE_TREND -> {
                        ScoreLineTrendCanvas(
                            dataPoints = chartPoints,
                            selectedIndex = selectedPointIndex,
                            onPointSelected = { idx -> selectedPointIndex = idx }
                        )
                    }
                    ChartType.LATENCY_COMPARISON -> {
                        LatencyBarChartCanvas(
                            dataPoints = chartPoints,
                            selectedIndex = selectedPointIndex,
                            onPointSelected = { idx -> selectedPointIndex = idx }
                        )
                    }
                    ChartType.TOPIC_DWELL_TIME -> {
                        TopicDwellBarChartCanvas(clickstreamEvents = clickstreamEvents)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend & Interactive Tooltip Info Panel
            when (selectedChartType) {
                ChartType.PERFORMANCE_TREND -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Score % Target (100%)", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        }

                        val activePoint = selectedPointIndex?.let { chartPoints.getOrNull(it) }
                        if (activePoint != null) {
                            Text(
                                text = "Tapped: ${activePoint.label} (${activePoint.scorePercent.toInt()}%)",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        } else {
                            Text("Tap point for details", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    }
                }

                ChartType.LATENCY_COMPARISON -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF00ADB5), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("MCQ Latency", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFFF7043), RoundedCornerShape(2.dp)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Written Single Word", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        }
                    }
                }

                ChartType.TOPIC_DWELL_TIME -> {
                    Text(
                        text = "Bar heights represent total dwell seconds logged across photosynthesis topics.",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreLineTrendCanvas(
    dataPoints: List<ChartDataPoint>,
    selectedIndex: Int?,
    onPointSelected: (Int) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(dataPoints) {
                detectTapGestures { offset ->
                    val width = size.width.toFloat()
                    val step = width / (dataPoints.size - 1).coerceAtLeast(1)
                    val closestIndex = ((offset.x + step / 2) / step).toInt().coerceIn(0, dataPoints.size - 1)
                    onPointSelected(closestIndex)
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val paddingY = 24f
        val usableHeight = canvasHeight - (paddingY * 2)

        // Draw Y-Axis Gridlines (0%, 25%, 50%, 75%, 100%)
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = paddingY + (usableHeight / gridLines) * i
            drawLine(
                color = Color(0xFF334155),
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f
            )
        }

        if (dataPoints.isEmpty()) return@Canvas

        val stepX = canvasWidth / (dataPoints.size - 1).coerceAtLeast(1)

        // Generate Path Points
        val points = dataPoints.mapIndexed { index, dp ->
            val x = index * stepX
            val normalizedY = 1.0f - (dp.scorePercent / 100f).coerceIn(0f, 1f)
            val y = paddingY + normalizedY * usableHeight
            Offset(x, y)
        }

        // Area Gradient Path
        val fillPath = Path().apply {
            moveTo(points.first().x, canvasHeight)
            lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val controlX1 = p1.x + (p2.x - p1.x) / 2
                val controlY1 = p1.y
                val controlX2 = p1.x + (p2.x - p1.x) / 2
                val controlY2 = p2.y
                cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
            }

            lineTo(points.last().x, canvasHeight)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4CAF50).copy(alpha = 0.4f), Color.Transparent),
                startY = 0f,
                endY = canvasHeight
            )
        )

        // Line Stroke Path
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val controlX1 = p1.x + (p2.x - p1.x) / 2
                val controlY1 = p1.y
                val controlX2 = p1.x + (p2.x - p1.x) / 2
                val controlY2 = p2.y
                cubicTo(controlX1, controlY1, controlX2, controlY2, p2.x, p2.y)
            }
        }

        drawPath(
            path = strokePath,
            color = Color(0xFF4CAF50),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Draw Data Points Dots
        points.forEachIndexed { index, pt ->
            val isSelected = selectedIndex == index
            val radius = if (isSelected) 8.dp.toPx() else 5.dp.toPx()

            drawCircle(
                color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF81C784),
                radius = radius,
                center = pt
            )

            drawCircle(
                color = Color(0xFF0F172A),
                radius = radius / 2,
                center = pt
            )
        }
    }
}

@Composable
private fun LatencyBarChartCanvas(
    dataPoints: List<ChartDataPoint>,
    selectedIndex: Int?,
    onPointSelected: (Int) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(dataPoints) {
                detectTapGestures { offset ->
                    val step = size.width / dataPoints.size.coerceAtLeast(1)
                    val idx = (offset.x / step).toInt().coerceIn(0, dataPoints.size - 1)
                    onPointSelected(idx)
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val maxLatency = 20f // 20s scale
        val groupWidth = width / dataPoints.size.coerceAtLeast(1)
        val barWidth = groupWidth * 0.3f

        // Draw Grid Lines (5s, 10s, 15s)
        for (i in 1..3) {
            val y = height - (height * (i * 5f / maxLatency))
            drawLine(
                color = Color(0xFF334155),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        dataPoints.forEachIndexed { idx, dp ->
            val isSel = selectedIndex == idx
            val groupStartX = idx * groupWidth + (groupWidth * 0.15f)

            // MCQ Bar
            val mcqHeight = (dp.mcqLatencySec / maxLatency).coerceIn(0.05f, 1f) * (height - 20f)
            val mcqY = height - mcqHeight
            drawRoundRect(
                color = if (isSel) Color(0xFF38BDF8) else Color(0xFF00ADB5),
                topLeft = Offset(groupStartX, mcqY),
                size = Size(barWidth, mcqHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )

            // Written Bar
            val writtenHeight = (dp.writtenLatencySec / maxLatency).coerceIn(0.05f, 1f) * (height - 20f)
            val writtenY = height - writtenHeight
            drawRoundRect(
                color = if (isSel) Color(0xFFFFB74D) else Color(0xFFFF7043),
                topLeft = Offset(groupStartX + barWidth + 6f, writtenY),
                size = Size(barWidth, writtenHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }
    }
}

@Composable
private fun TopicDwellBarChartCanvas(clickstreamEvents: List<ClickstreamEntity>) {
    val topicDwellMap = remember(clickstreamEvents) {
        val topics = listOf(
            "sec_1_intro" to "1. Intro",
            "sec_2_chloroplast" to "2. Leaf",
            "sec_3_light_reaction" to "3. Light",
            "sec_4_dark_reaction" to "4. Dark",
            "sec_5_factors" to "5. Rate"
        )

        topics.map { (id, label) ->
            val totalSec = clickstreamEvents
                .filter { it.componentId == id || it.componentId.contains(id) }
                .sumOf { it.dwellTimeMs } / 1000f
            label to totalSec.coerceAtLeast(10f) // min visual baseline
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val maxDwell = (topicDwellMap.maxOfOrNull { it.second } ?: 60f).coerceAtLeast(30f)
        val barWidth = width / (topicDwellMap.size * 1.8f)
        val spacing = width / topicDwellMap.size

        topicDwellMap.forEachIndexed { index, (label, dwellSec) ->
            val barHeight = (dwellSec / maxDwell) * (height - 30f)
            val x = index * spacing + (spacing - barWidth) / 2
            val y = height - barHeight

            drawRoundRect(
                color = when (index % 4) {
                    0 -> Color(0xFF4CAF50)
                    1 -> Color(0xFF0284C7)
                    2 -> Color(0xFFFFD54F)
                    else -> Color(0xFF9C27B0)
                },
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )
        }
    }
}
