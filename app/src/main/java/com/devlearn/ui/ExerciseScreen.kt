package com.devlearn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.devlearn.models.ExerciseState
import com.devlearn.models.ExerciseType
import com.devlearn.models.ProgrammingLanguage
import com.devlearn.ui.theme.*

@Composable
fun ExerciseScreen(
    navController: NavController,
    viewModel: ExerciseViewModel,
    languageName: String,
    typeName: String
) {
    val state by viewModel.state.collectAsState()
    val language = remember { ProgrammingLanguage.valueOf(languageName) }
    val type = remember { ExerciseType.valueOf(typeName) }
    val langColor = Color(language.colorHex)

    LaunchedEffect(languageName, typeName) {
        if (viewModel.state.value is ExerciseState.Idle) {
            viewModel.generateExercise(language, type)
        }
    }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.resetState()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = PrimaryVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = language.emoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = language.displayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = langColor
                        )
                    }
                    Text(
                        text = "${type.icon} ${type.displayName}",
                        fontSize = 12.sp,
                        color = OnSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val s = state) {
                is ExerciseState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Generando ejercicio con IA...",
                                color = OnSurface.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                is ExerciseState.Loaded -> {
                    ExerciseContent(
                        exercise = s.exercise,
                        selectedAnswer = null,
                        isAnswered = false,
                        langColor = langColor,
                        onAnswerSelected = { viewModel.submitAnswer(it) },
                        onNext = {
                            viewModel.resetState()
                            viewModel.generateExercise(language, type)
                        }
                    )
                }

                is ExerciseState.Answered -> {
                    ExerciseContent(
                        exercise = s.exercise,
                        selectedAnswer = s.selected,
                        isAnswered = true,
                        isCorrect = s.isCorrect,
                        langColor = langColor,
                        onAnswerSelected = {},
                        onNext = {
                            viewModel.resetState()
                            viewModel.generateExercise(language, type)
                        }
                    )
                }

                is ExerciseState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(24.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = s.message,
                                color = Error,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.generateExercise(language, type) },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reintentar", color = OnPrimary)
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    exercise: com.devlearn.models.Exercise,
    selectedAnswer: String?,
    isAnswered: Boolean,
    isCorrect: Boolean = false,
    langColor: Color,
    onAnswerSelected: (String) -> Unit,
    onNext: () -> Unit
) {
    // Title card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(16.dp)
    ) {
        Text(
            text = exercise.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Description
    Text(
        text = exercise.description,
        fontSize = 15.sp,
        color = OnSurface.copy(alpha = 0.9f),
        lineHeight = 22.sp
    )

    // Code snippet if present
    exercise.codeSnippet?.let { code ->
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0D1117))
                .border(1.dp, langColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = code,
                fontSize = 13.sp,
                color = Color(0xFFE6EDF3),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                lineHeight = 20.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Options
    exercise.options.forEach { option ->
        val isThisSelected = selectedAnswer == option
        val isThisCorrect = isAnswered && option == exercise.correctAnswer
        val isThisWrong = isAnswered && isThisSelected && !isCorrect

        val bgColor = when {
            isThisCorrect -> Success.copy(alpha = 0.15f)
            isThisWrong -> Error.copy(alpha = 0.15f)
            isThisSelected -> Primary.copy(alpha = 0.2f)
            else -> Surface
        }
        val borderColor = when {
            isThisCorrect -> Success
            isThisWrong -> Error
            isThisSelected -> Primary
            else -> Color(0xFF2A2A3E)
        }
        val emoji = when {
            isThisCorrect -> "✅ "
            isThisWrong -> "❌ "
            else -> ""
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(enabled = !isAnswered) { onAnswerSelected(option) }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$emoji$option",
                fontSize = 14.sp,
                color = when {
                    isThisCorrect -> Success
                    isThisWrong -> Error
                    isThisSelected -> PrimaryVariant
                    else -> OnSurface.copy(alpha = 0.85f)
                },
                fontWeight = if (isThisSelected || isThisCorrect) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }

    // Feedback after answering
    if (isAnswered) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isCorrect) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f))
                .border(
                    1.dp,
                    if (isCorrect) Success.copy(alpha = 0.4f) else Error.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = if (isCorrect) "¡Correcto! 🎉" else "Incorrecto 😔",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) Success else Error
                )
                if (exercise.explanation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = exercise.explanation,
                        fontSize = 13.sp,
                        color = OnSurface.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                text = "Siguiente ejercicio ➡️",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = OnPrimary
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}
