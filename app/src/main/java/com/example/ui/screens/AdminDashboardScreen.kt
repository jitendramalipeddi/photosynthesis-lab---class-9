package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.ClickstreamEventTypes
import com.example.analytics.ClickstreamTracker
import com.example.data.AnalyticsRepository
import com.example.data.ClickstreamEntity
import com.example.ui.components.ClickstreamExportDialog
import com.example.ui.components.QuizPerformanceTrendChart

@Composable
fun AdminDashboardScreen(
    tracker: ClickstreamTracker,
    onLogoutClicked: () -> Unit
) {
    val repository = remember { AnalyticsRepository(tracker) }
    val allEvents by repository.allEvents.collectAsState(initial = emptyList())
    val allQuizResults by repository.allQuizResults.collectAsState(initial = emptyList())
    val summary by repository.analyticsSummary.collectAsState(
        initial = com.example.data.AnalyticsSummary(0, 0, 0.0, 0.0, 0.0, 80.0, 0, "1. Introduction")
    )

    var selectedFilter by remember { mutableStateOf("ALL") }
    var exportFormat by remember { mutableStateOf<String?>(null) } // "CSV" or "JSON" or null

    val filteredEvents = remember(allEvents, selectedFilter) {
        if (selectedFilter == "ALL") allEvents
        else allEvents.filter { it.eventType == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Top Header
        Surface(
            color = Color(0xFF1E293B),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF0284C7),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin Analytics Dashboard",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Admin: ${tracker.getCurrentUsername()} • Photosynthesis Analytics",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onLogoutClicked) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFEF5350)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Summary Analytics Metrics Grid
            item {
                Text(
                    text = "📊 Cognitive Engagement Summary",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMetricCard(
                            title = "Total Sessions",
                            value = "${summary.totalSessions}",
                            subtitle = "Distinct Students",
                            accentColor = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            title = "Captured Events",
                            value = "${summary.totalEventsCount}",
                            subtitle = "Clickstream Logs",
                            accentColor = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMetricCard(
                            title = "Avg Dwell Time",
                            value = "${summary.avgReadingDwellTimeSec}s",
                            subtitle = "Per Reading Topic",
                            accentColor = Color(0xFFFFD54F),
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            title = "MCQ Latency",
                            value = "${summary.avgMcqLatencySec}s",
                            subtitle = "Response Time",
                            accentColor = Color(0xFF00ADB5),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatMetricCard(
                            title = "Written Latency",
                            value = "${summary.avgWrittenLatencySec}s",
                            subtitle = "Single Word Input",
                            accentColor = Color(0xFFFF7043),
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            title = "Top Engaged Topic",
                            value = summary.mostEngagedSection.take(16),
                            subtitle = "Highest Dwell",
                            accentColor = Color(0xFFA5D6A7),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Interactive Graphical Trend Charts (Bar & Line Graphs)
            item {
                QuizPerformanceTrendChart(
                    quizResults = allQuizResults,
                    clickstreamEvents = allEvents
                )
            }

            // Export Data Action Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💾 Clickstream Data Export & Downloads",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Download raw student interaction stream with timestamps, latencies, and metadata payload.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { exportFormat = "CSV" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "CSV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { exportFormat = "EXCEL" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "EXCEL", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { exportFormat = "JSON" },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "JSON", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Clickstream Stream Inspector Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📜 Clickstream Event Log Inspector",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${filteredEvents.size} Records",
                        color = Color(0xFF81C784),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Event Type Filter Chips
                val filterList = listOf(
                    "ALL",
                    ClickstreamEventTypes.LOGIN,
                    ClickstreamEventTypes.READING_DWELL_TIME,
                    ClickstreamEventTypes.QUESTION_RESPONSE_LATENCY,
                    ClickstreamEventTypes.MEDIA_INTERACTION,
                    ClickstreamEventTypes.QUIZ_SUBMIT
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filterList) { filter ->
                        val isSel = selectedFilter == filter
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFilter = filter },
                            label = { Text(text = filter.replace("QUESTION_", "Q_").replace("READING_", "READ_"), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = null,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Filtered Event Cards List
            if (filteredEvents.isEmpty()) {
                item {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No clickstream events recorded for filter '$selectedFilter'. Interact with the app as a student to generate data points.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(filteredEvents) { event ->
                    EventItemCard(event = event)
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(exportFormat) {
        exportFormat?.let { format ->
            val exportString = when (format) {
                "JSON" -> repository.exportToJsonString(allEvents)
                else -> repository.exportToCsvString(allEvents)
            }
            val savedLocation = com.example.ui.components.saveFileToDownloads(context, format, exportString)
            if (savedLocation != null) {
                android.widget.Toast.makeText(context, "Saved directly to Downloads: $savedLocation", android.widget.Toast.LENGTH_LONG).show()
            } else {
                android.widget.Toast.makeText(context, "Failed to save file.", android.widget.Toast.LENGTH_SHORT).show()
            }
            exportFormat = null
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accentColor, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = subtitle, color = Color(0xFF64748B), fontSize = 10.sp)
        }
    }
}

@Composable
private fun EventItemCard(event: ClickstreamEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (event.eventType) {
                        ClickstreamEventTypes.LOGIN -> Color(0xFF0284C7)
                        ClickstreamEventTypes.READING_DWELL_TIME -> Color(0xFF2E7D32)
                        ClickstreamEventTypes.QUESTION_RESPONSE_LATENCY -> Color(0xFFD97706)
                        ClickstreamEventTypes.QUIZ_SUBMIT -> Color(0xFF7C3AED)
                        else -> Color(0xFF475569)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = event.eventType,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = event.formattedTimestamp.takeLast(12),
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "User: ${event.username} (${event.userRole}) • ID: ${event.componentId}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            if (event.dwellTimeMs > 0) {
                Text(
                    text = "Dwell Time: ${event.dwellTimeMs / 1000.0}s",
                    color = Color(0xFFFFD54F),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (event.responseLatencyMs > 0) {
                Text(
                    text = "Response Latency: ${event.responseLatencyMs / 1000.0}s",
                    color = Color(0xFFFF7043),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (event.metadataPayload.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.metadataPayload,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
