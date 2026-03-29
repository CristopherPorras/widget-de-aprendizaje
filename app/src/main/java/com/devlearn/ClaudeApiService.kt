package com.devlearn

import com.devlearn.models.Exercise
import com.devlearn.models.ExerciseType
import com.devlearn.models.ProgrammingLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ClaudeApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateExercise(
        language: ProgrammingLanguage,
        type: ExerciseType,
        apiKey: String
    ): Result<Exercise> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = """Eres un tutor experto en programación. Genera ejercicios educativos breves y concisos.
IMPORTANTE: Responde ÚNICAMENTE con un objeto JSON válido. Sin texto antes ni después. Sin bloques de código markdown.
El JSON debe tener exactamente esta estructura:
{
  "title": "Título corto del ejercicio (máx 60 caracteres)",
  "description": "Pregunta o enunciado claro del ejercicio",
  "codeSnippet": "código de ejemplo si aplica, usa \\n para saltos de línea, o null si no aplica",
  "options": ["Opción A", "Opción B", "Opción C", "Opción D"],
  "correctAnswer": "exactamente igual a una de las opciones",
  "explanation": "Explicación breve y clara de la respuesta correcta (máx 150 caracteres)"
}"""

            val userPrompt = buildUserPrompt(language, type)

            val requestJson = JSONObject().apply {
                put("model", "claude-sonnet-4-20250514")
                put("max_tokens", 800)
                put("system", systemPrompt)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userPrompt)
                    })
                })
            }

            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: throw Exception("Respuesta vacía del servidor")

            if (!response.isSuccessful) {
                val errorMsg = try {
                    JSONObject(body).optJSONObject("error")?.optString("message") ?: "Error ${response.code}"
                } catch (e: Exception) { "Error HTTP ${response.code}" }
                throw Exception(errorMsg)
            }

            val responseJson = JSONObject(body)
            val contentText = responseJson
                .getJSONArray("content")
                .getJSONObject(0)
                .getString("text")
                .trim()
                .removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

            val exerciseJson = JSONObject(contentText)
            val optionsArray = exerciseJson.getJSONArray("options")
            val options = (0 until optionsArray.length()).map { optionsArray.getString(it) }
            val codeSnippet = exerciseJson.optString("codeSnippet", "")
                .let { if (it.isBlank() || it == "null") null else it }

            Result.success(Exercise(
                language = language,
                type = type,
                title = exerciseJson.getString("title"),
                description = exerciseJson.getString("description"),
                codeSnippet = codeSnippet,
                options = options,
                correctAnswer = exerciseJson.getString("correctAnswer"),
                explanation = exerciseJson.getString("explanation")
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildUserPrompt(language: ProgrammingLanguage, type: ExerciseType): String {
        val lang = language.displayName
        return when (type) {
            ExerciseType.MULTIPLE_CHOICE ->
                "Genera un ejercicio de OPCIÓN MÚLTIPLE sobre sintaxis o conceptos de $lang. Nivel: principiante a intermedio. Incluye 4 opciones, solo una es correcta."
            ExerciseType.WHAT_OUTPUTS ->
                "Genera un ejercicio '¿QUÉ IMPRIME ESTE CÓDIGO?' en $lang. Muestra un snippet corto de 4-7 líneas en codeSnippet. Las 4 opciones deben ser posibles salidas del código."
            ExerciseType.FILL_BLANK ->
                "Genera un ejercicio de COMPLETAR CÓDIGO en $lang. El código debe tener un hueco marcado como ___. Las 4 opciones son fragmentos de código que pueden ir en el hueco."
            ExerciseType.DEBUG ->
                "Genera un ejercicio de ENCONTRAR EL ERROR en código $lang. El snippet tiene un bug sutil. Las 4 opciones describen el error, solo una es correcta."
        }
    }
}
