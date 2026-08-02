package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.analytics.ClickstreamTracker
import com.example.model.PhotosynthesisData
import com.example.model.ReadingSection
import com.example.ui.components.InteractiveChemicalEquationBuilder
import com.example.ui.components.InteractiveChloroplastDiagram
import com.example.ui.components.StomataInteractiveSimulator
import com.example.ui.components.VocabularyFlipCard
import com.example.ui.components.YouTubeVideoCard

@Composable
fun ReadingMaterialScreen(
    tracker: ClickstreamTracker,
    onStartQuizClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    var selectedSectionIndex by remember { mutableStateOf(0) }
    val currentSection = PhotosynthesisData.readingSections[selectedSectionIndex]
    val listState = rememberLazyListState()

    // Log section entrance & dwell tracking & reset scroll position to top
    LaunchedEffect(selectedSectionIndex) {
        listState.scrollToItem(0)
        tracker.onEnterSection(currentSection.id, currentSection.title)
    }

    DisposableEffect(Unit) {
        onDispose {
            tracker.onLeaveCurrentSection()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    tracker.onNavigation("quiz_screen")
                    onStartQuizClicked()
                },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Quiz, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Start Quiz (5 Qs)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Bar
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
                    Column {
                        Text(
                            text = "🌿 Photosynthesis Lab",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Student: ${tracker.getCurrentUsername()} • Session: ${tracker.getCurrentSessionId()}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Topic Selector Horizontal Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PhotosynthesisData.readingSections.size) { index ->
                    val sec = PhotosynthesisData.readingSections[index]
                    val isSelected = index == selectedSectionIndex

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedSectionIndex = index
                            tracker.onNavigation("section_${sec.id}")
                        },
                        label = {
                            Text(
                                text = sec.title.take(18) + "...",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4CAF50),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF334155),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        border = null,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // Topic Main Content Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Section Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = currentSection.title,
                                color = Color(0xFF81C784),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = currentSection.subtitle,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                color = Color(0xFF0F2D1E),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "💡 Key Concept: ${currentSection.keyConcept}",
                                    color = Color(0xFFA5D6A7),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Interactive Component Embedded based on section
                item {
                    when (currentSection.id) {
                        "sec_1_intro" -> {
                            InteractiveChemicalEquationBuilder { step ->
                                tracker.onMediaInteraction("EQUATION", "Chemical Equation Builder", step)
                            }
                        }
                        "sec_2_chloroplast" -> {
                            Column {
                                InteractiveChloroplastDiagram { part ->
                                    tracker.onMediaInteraction("DIAGRAM", "Chloroplast Anatomy", "Tapped $part")
                                }
                                StomataInteractiveSimulator { isOpen ->
                                    tracker.onMediaInteraction("SIMULATOR", "Stomata Guard Cells", if (isOpen) "Opened Pore" else "Closed Pore")
                                }
                            }
                        }
                        "sec_3_light_reaction", "sec_4_dark_reaction", "sec_5_factors" -> {
                            currentSection.videoUrl?.let { url ->
                                YouTubeVideoCard(
                                    videoTitle = currentSection.videoTitle ?: "Photosynthesis Video",
                                    videoUrl = url
                                ) { title, targetUrl ->
                                    tracker.onMediaInteraction("VIDEO", title, "Launched YouTube $targetUrl")
                                }
                            }
                        }
                    }
                }

                // Markdown / Paragraph Content
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📖 Detailed Explanation",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentSection.contentMarkdown,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Key Exam Points:",
                                color = Color(0xFF81C784),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            currentSection.bulletPoints.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = point,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Vocabulary Flashcard Section Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📚 Essential Vocabulary",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Tap cards to flip",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Vocabulary Flashcards
                items(PhotosynthesisData.vocabularyTerms) { term ->
                    VocabularyFlipCard(term = term) { termName, isFlipped ->
                        tracker.onVocabCardFlipped(termName, isFlipped)
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) } // spacing for FAB
            }
        }
    }
}
