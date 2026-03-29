package com.devlearn.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devlearn.ExerciseViewModel
import com.devlearn.models.ExerciseType
import com.devlearn.models.ProgrammingLanguage
import com.devlearn.ui.theme.*

@Composable
fun HomeScreen(navController: NavController, viewModel: ExerciseViewModel) {
    val exercisesDone by viewModel.exercisesDone.collectAsState()
    val correctAnswers by viewModel.correctAnswers.collectAsState()
    var selectedLanguage by remember { mutableStateOf<ProgrammingLanguage?>(null) }
    var selectedType by remember { mutableStateOf(ExerciseType.MULTIPLE_CHOICE) }
    val accuracy = if (exercisesDone > 0) (correctAnswers * 100 / exercisesDone) else 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DevLearn",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryVariant
                    )
                    Text(
                        text = "Aprende programación con IA",
                        fontSize = 13.sp,
                        color = OnSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(
                    onClick = { navController.navigate("settings") },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Surface)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Ajustes",
                        tint = PrimaryVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Ejercicios",
                    value = exercisesDone.toString(),
                    emoji = "📚"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Correctas",
                    value = correctAnswers.toString(),
                    emoji = "✅"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Precisión",
                    value = "$accuracy%",
                    emoji = "🎯"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Language selection
            Text(
                text = "Lenguaje",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgrammingLanguage.values().chunked(2).forEach { rowLangs ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowLangs.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            val langColor = Color(lang.colorHex)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) langColor.copy(alpha = 0.25f)
                                        else Surface
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) langColor else Surface,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedLanguage = lang }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = lang.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = lang.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) langColor else OnSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        // Fill empty slot if odd number
                        if (rowLangs.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise type selection
            Text(
                text = "Tipo de ejercicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExerciseType.values().forEach { type ->
                    val isSelected = selectedType == type
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Primary.copy(alpha = 0.2f) else Surface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Primary else Surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedType = type }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = type.icon, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = type.displayName,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) PrimaryVariant else OnSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Generate button
            Button(
                onClick = {
                    selectedLanguage?.let { lang ->
                        navController.navigate("exercise/${lang.name}/${selectedType.name}")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = selectedLanguage != null,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Surface
                )
            ) {
                Text(
                    text = if (selectedLanguage != null) "Generar ejercicio con IA ✨" else "Selecciona un lenguaje",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedLanguage != null) OnPrimary else OnSurface.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, label: String, value: String, emoji: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryVariant
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = OnSurface.copy(alpha = 0.6f)
        )
    }
}
