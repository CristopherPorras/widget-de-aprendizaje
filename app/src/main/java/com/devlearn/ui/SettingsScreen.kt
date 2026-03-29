package com.devlearn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.devlearn.ExerciseViewModel
import com.devlearn.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController, viewModel: ExerciseViewModel) {
    val currentKey by viewModel.apiKey.collectAsState()
    var keyInput by remember(currentKey) { mutableStateOf(currentKey) }
    var showKey by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

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
                    onClick = { navController.popBackStack() },
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
                Text(
                    text = "Ajustes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // API Key section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "🔑 API Key de Claude",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Necesitas una API Key de Anthropic para generar ejercicios con IA. Obtenla en console.anthropic.com",
                        fontSize = 12.sp,
                        color = OnSurface.copy(alpha = 0.6f),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = {
                            keyInput = it
                            saved = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "sk-ant-...",
                                color = OnSurface.copy(alpha = 0.3f)
                            )
                        },
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { showKey = !showKey }) {
                                Icon(
                                    imageVector = if (showKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showKey) "Ocultar" else "Mostrar",
                                    tint = PrimaryVariant
                                )
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = SurfaceVariant,
                            focusedTextColor = OnBackground,
                            unfocusedTextColor = OnBackground,
                            cursorColor = PrimaryVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            viewModel.saveApiKey(keyInput)
                            saved = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (saved) Success else Primary
                        )
                    ) {
                        Text(
                            text = if (saved) "✅ Guardado" else "Guardar API Key",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Info section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "ℹ️ Acerca de DevLearn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Versión", "1.0.0")
                    InfoRow("Modelo IA", "claude-sonnet-4-20250514")
                    InfoRow("Lenguajes", "Python, JS, Kotlin, HTML/CSS, SQL")
                    InfoRow("Tipos", "4 tipos de ejercicio")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = OnSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = OnSurface.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )
    }
}
