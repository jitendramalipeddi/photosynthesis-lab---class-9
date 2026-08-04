package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InteractiveChloroplastDiagram(
    onDiagramClick: (partName: String) -> Unit
) {
    var selectedPart by remember { mutableStateOf("Thylakoids (Grana)") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F2D1E)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔬 Interactive Chloroplast Diagram",
                    color = Color(0xFFA5D6A7),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = Color(0xFF2E7D32),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Tap Parts to Inspect",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas drawing of Chloroplast
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF071E13))
                    .clickable {
                        onDiagramClick("chloroplast_canvas")
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(280.dp, 180.dp)) {
                    val w = size.width
                    val h = size.height

                    // Outer Double Membrane
                    drawOval(
                        color = Color(0xFF2E7D32),
                        topLeft = Offset(10f, 10f),
                        size = Size(w - 20f, h - 20f),
                        style = Stroke(width = 8f)
                    )
                    drawOval(
                        color = Color(0xFF81C784),
                        topLeft = Offset(18f, 18f),
                        size = Size(w - 36f, h - 36f),
                        style = Stroke(width = 4f)
                    )

                    // Stroma Fluid background
                    drawOval(
                        color = Color(0xFF1B4D3E).copy(alpha = 0.6f),
                        topLeft = Offset(20f, 20f),
                        size = Size(w - 40f, h - 40f)
                    )

                    // Thylakoid Stacks (Grana - green discs)
                    val granaCenters = listOf(
                        Offset(w * 0.3f, h * 0.45f),
                        Offset(w * 0.5f, h * 0.55f),
                        Offset(w * 0.7f, h * 0.42f)
                    )

                    granaCenters.forEach { center ->
                        for (i in -2..2) {
                            drawRoundRect(
                                color = Color(0xFF4CAF50),
                                topLeft = Offset(center.x - 22f, center.y + (i * 12f) - 5f),
                                size = Size(44f, 10f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                            )
                        }
                    }

                    // Stroma Lamellae connecting grana
                    drawLine(
                        color = Color(0xFF81C784),
                        start = Offset(w * 0.3f + 20f, h * 0.45f),
                        end = Offset(w * 0.5f - 20f, h * 0.55f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color(0xFF81C784),
                        start = Offset(w * 0.5f + 20f, h * 0.55f),
                        end = Offset(w * 0.7f - 20f, h * 0.42f),
                        strokeWidth = 3f
                    )
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PartChip("Outer Membrane", selectedPart == "Outer Membrane") {
                    selectedPart = "Outer Membrane"
                    onDiagramClick("outer_membrane")
                }
                PartChip("Thylakoids (Grana)", selectedPart == "Thylakoids (Grana)") {
                    selectedPart = "Thylakoids (Grana)"
                    onDiagramClick("thylakoids")
                }
                PartChip("Stroma Fluid", selectedPart == "Stroma Fluid") {
                    selectedPart = "Stroma Fluid"
                    onDiagramClick("stroma")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explanation box for selected part
            Surface(
                color = Color(0xFF1B4D3E),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (selectedPart) {
                            "Outer Membrane" -> "Outer & Inner Membrane: Semi-permeable double boundary regulating molecule transport in/out of chloroplast."
                            "Thylakoids (Grana)" -> "Thylakoid Membrane: Contains chlorophyll pigments. Site of Light-Dependent Reaction and Photolysis of water."
                            "Stroma Fluid" -> "Stroma: Gel-like matrix rich in enzymes (like RuBisCO). Site of Dark Reaction (Calvin Cycle) producing glucose."
                            else -> "Tap any part in the diagram to view its physiological function."
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PartChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFFD54F) else Color(0xDD000000)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.White
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StomataInteractiveSimulator(
    onStomataToggled: (isOpen: Boolean) -> Unit
) {
    var isOpen by remember { mutableStateOf(true) }

    // Infinite animation transition for gas flow
    val infiniteTransition = rememberInfiniteTransition(label = "gas_flow")
    val gasOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132A1D)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌱 Stomata Gas Exchange Simulator",
                    color = Color(0xFF81C784),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = {
                        isOpen = !isOpen
                        onStomataToggled(isOpen)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOpen) Color(0xFFE53935) else Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Toggle",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOpen) "Close Stoma" else "Open Stoma",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Guard Cell Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A1B12)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(240.dp, 140.dp)) {
                    val w = size.width
                    val h = size.height

                    // Left Guard Cell (bean-shaped curve)
                    val gap = if (isOpen) 35f else 2f

                    drawOval(
                        color = Color(0xFF4CAF50),
                        topLeft = Offset(w * 0.5f - 50f - gap, h * 0.15f),
                        size = Size(45f, h * 0.7f)
                    )
                    // Right Guard Cell
                    drawOval(
                        color = Color(0xFF4CAF50),
                        topLeft = Offset(w * 0.5f + gap, h * 0.15f),
                        size = Size(45f, h * 0.7f)
                    )

                    // Stoma Pore Opening Center
                    if (isOpen) {
                        drawOval(
                            color = Color(0xFF0A1B12),
                            topLeft = Offset(w * 0.5f - 15f, h * 0.3f),
                            size = Size(30f, h * 0.4f)
                        )
                    }
                }

                // Gas particles floating in/out when open
                if (isOpen) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = gasOffsetY.dp)
                    ) {
                        Surface(
                            color = Color(0xFF29B6F6),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "CO₂ IN ⬇",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFFF7043),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "O₂ & H₂O OUT ⬆",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Stoma Closed\n(Gas exchange & transpiration halted)",
                        color = Color.Yellow,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isOpen)
                    "Turgid Guard Cells: Swollen with water, bending outward to create an open stomatal pore for CO₂ intake."
                else
                    "Flaccid Guard Cells: Lose water during stress/night, closing the stoma to prevent water loss.",
                color = Color(0xFFB0BEC5),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InteractiveChemicalEquationBuilder(
    onEquationInteracted: (step: String) -> Unit
) {
    var activeMolecule by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Balanced Chemical Reaction",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Equation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MoleculeBadge("6 CO₂", "Carbon Dioxide", activeMolecule == "CO₂") {
                    activeMolecule = "CO₂"
                    onEquationInteracted("CO2_clicked")
                }
                Text(" + ", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                MoleculeBadge("6 H₂O", "Water", activeMolecule == "H₂O") {
                    activeMolecule = "H₂O"
                    onEquationInteracted("H2O_clicked")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reaction Arrow
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sunlight", color = Color(0xFFFFD54F), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("────────►", color = Color(0xFF81C784), fontSize = 14.sp)
                    Text("Chlorophyll", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                MoleculeBadge("C₆H₁₂O₆", "Glucose (Sugar)", activeMolecule == "Glucose") {
                    activeMolecule = "Glucose"
                    onEquationInteracted("Glucose_clicked")
                }
                Text(" + ", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                MoleculeBadge("6 O₂", "Oxygen Gas", activeMolecule == "O₂") {
                    activeMolecule = "O₂"
                    onEquationInteracted("O2_clicked")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (activeMolecule) {
                        "CO₂" -> "6 Carbon Dioxide molecules enter through leaf stomata from the air."
                        "H₂O" -> "6 Water molecules are absorbed by roots and transported via Xylem vessels."
                        "Glucose" -> "1 Glucose molecule is produced, providing energy and building cellulose/starch."
                        "O₂" -> "6 Oxygen molecules are released as a byproduct through stomata."
                        else -> "Tap any chemical reactant or product above to inspect its origin and role."
                    },
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MoleculeBadge(
    formula: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formula,
                color = if (isSelected) Color.Black else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
            Text(
                text = label,
                color = if (isSelected) Color.Black.copy(alpha = 0.8f) else Color(0xFF94A3B8),
                fontSize = 9.sp
            )
        }
    }
}
