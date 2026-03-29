package com.devlearn

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "devlearn_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val EXERCISES_DONE = intPreferencesKey("exercises_done")
        val CORRECT_ANSWERS = intPreferencesKey("correct_answers")
        val LAST_LANGUAGE = stringPreferencesKey("last_language")
    }

    val apiKey: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[API_KEY] ?: "" }

    val exercisesDone: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[EXERCISES_DONE] ?: 0 }

    val correctAnswers: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[CORRECT_ANSWERS] ?: 0 }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { prefs -> prefs[API_KEY] = key.trim() }
    }

    suspend fun recordAnswer(correct: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[EXERCISES_DONE] = (prefs[EXERCISES_DONE] ?: 0) + 1
            if (correct) prefs[CORRECT_ANSWERS] = (prefs[CORRECT_ANSWERS] ?: 0) + 1
        }
    }

    suspend fun saveLastLanguage(language: String) {
        context.dataStore.edit { prefs -> prefs[LAST_LANGUAGE] = language }
    }
}
