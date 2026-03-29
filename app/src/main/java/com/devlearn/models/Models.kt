package com.devlearn.models

data class Exercise(
    val id: String = System.currentTimeMillis().toString(),
    val language: ProgrammingLanguage,
    val type: ExerciseType,
    val title: String,
    val description: String,
    val codeSnippet: String? = null,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String = ""
)

enum class ProgrammingLanguage(
    val displayName: String,
    val emoji: String,
    val colorHex: Long
) {
    PYTHON("Python", "🐍", 0xFF3776AB),
    JAVASCRIPT("JavaScript", "⚡", 0xFFF0B429),
    KOTLIN("Kotlin / Java", "☕", 0xFF7F52FF),
    HTML_CSS("HTML / CSS", "🎨", 0xFFE34F26),
    SQL("SQL", "🗄️", 0xFF336791)
}

enum class ExerciseType(val displayName: String, val icon: String) {
    MULTIPLE_CHOICE("Opción múltiple", "📋"),
    WHAT_OUTPUTS("¿Qué imprime?", "🖥️"),
    FILL_BLANK("Completar código", "✏️"),
    DEBUG("Encontrar el error", "🐛")
}

sealed class ExerciseState {
    object Idle : ExerciseState()
    object Loading : ExerciseState()
    data class Loaded(val exercise: Exercise) : ExerciseState()
    data class Answered(val exercise: Exercise, val selected: String, val isCorrect: Boolean) : ExerciseState()
    data class Error(val message: String) : ExerciseState()
}
