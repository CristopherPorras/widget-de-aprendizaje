package com.devlearn

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devlearn.models.ExerciseState
import com.devlearn.models.ExerciseType
import com.devlearn.models.ProgrammingLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ClaudeApiService()
    private val prefsManager = PreferencesManager(application)

    private val _state = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val state: StateFlow<ExerciseState> = _state.asStateFlow()

    val apiKey: StateFlow<String> = prefsManager.apiKey
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val exercisesDone: StateFlow<Int> = prefsManager.exercisesDone
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val correctAnswers: StateFlow<Int> = prefsManager.correctAnswers
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun generateExercise(language: ProgrammingLanguage, type: ExerciseType) {
        val key = apiKey.value
        if (key.isBlank()) {
            _state.value = ExerciseState.Error("Configura tu API Key de Claude primero")
            return
        }
        _state.value = ExerciseState.Loading
        viewModelScope.launch {
            apiService.generateExercise(language, type, key).fold(
                onSuccess = { exercise ->
                    _state.value = ExerciseState.Loaded(exercise)
                    prefsManager.saveLastLanguage(language.name)
                },
                onFailure = { error ->
                    _state.value = ExerciseState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun submitAnswer(selected: String) {
        val current = _state.value
        if (current is ExerciseState.Loaded) {
            val isCorrect = selected.trim() == current.exercise.correctAnswer.trim()
            _state.value = ExerciseState.Answered(current.exercise, selected, isCorrect)
            viewModelScope.launch { prefsManager.recordAnswer(isCorrect) }
        }
    }

    fun resetState() { _state.value = ExerciseState.Idle }

    fun saveApiKey(key: String) {
        viewModelScope.launch { prefsManager.saveApiKey(key) }
    }
}
