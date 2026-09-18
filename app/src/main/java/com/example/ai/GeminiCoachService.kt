package com.example.ai

import com.example.BuildConfig
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiCoachService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        userMessage: String,
        userContext: String
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide intelligent local cyberpunk coach guidance leveraging room database telemetry
            val reply = generateOfflineCoachAdvice(userMessage, userContext)
            return@withContext ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "model",
                text = reply
            )
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Append previous turns (up to 8 recent messages for context window efficiency)
            val recentTurns = history.takeLast(8)
            for (msg in recentTurns) {
                val role = if (msg.sender == "user") "user" else "model"
                val partObj = JSONObject().put("text", msg.text)
                val partsArr = JSONArray().put(partObj)
                val contentObj = JSONObject().put("role", role).put("parts", partsArr)
                contentsArray.put(contentObj)
            }

            // Append current user message
            val currentPart = JSONObject().put("text", userMessage)
            val currentParts = JSONArray().put(currentPart)
            val currentContent = JSONObject().put("role", "user").put("parts", currentParts)
            contentsArray.put(currentContent)

            val systemInstructionText = """
                You are CyberCoach, an elite cyberpunk-themed AI fitness and nutrition intelligence system embedded in the NeonFit app.
                You have direct real-time access to the user's local Room database telemetry:
                $userContext
                
                Guidelines:
                - Analyze their specific logged workouts, recent exercises, daily calories/protein/carbs/fats vs goals, RPG attributes (Strength, Agility, Endurance), and current Boss Quest.
                - Maintain an energetic, sci-fi cyberpunk persona (phrases like "biometric telemetry analyzed", "neural uplink active", "hypertrophy matrix", "anabolic synthesis", "attribute gains verified").
                - Provide scientifically accurate, high-impact advice on progressive overload, exercise selection, macro balancing, rest, and boss quest strategy.
                - Keep responses punchy, engaging, and formatted cleanly with markdown bullet points when prescribing workouts or meal plans.
            """.trimIndent()

            val systemInstructionObj = JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", systemInstructionObj)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                })
            }

            val body = requestJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(endpoint)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // If API fails or key quota issue, graceful fallback with helpful notice
                return@withContext ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "model",
                    text = generateOfflineCoachAdvice(userMessage, userContext) + "\n\n*(Note: Cloud neural uplink status HTTP ${response.code}; operating in Room Database local diagnostic mode.)*"
                )
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val generatedText = parts?.optJSONObject(0)?.optString("text")

            val reply = if (!generatedText.isNullOrBlank()) {
                generatedText
            } else {
                generateOfflineCoachAdvice(userMessage, userContext)
            }

            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "model",
                text = reply
            )
        } catch (_: Exception) {
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "model",
                text = generateOfflineCoachAdvice(userMessage, userContext)
            )
        }
    }

    private fun generateOfflineCoachAdvice(userMessage: String, userContext: String): String {
        val lower = userMessage.lowercase()
        return when {
            lower.contains("attribute") || lower.contains("str") || lower.contains("agi") || lower.contains("end") -> {
                """
                ⚡ **[HERO ATTRIBUTES DIAGNOSTIC]**
                *Room Database Telemetry Synced:*
                $userContext
                
                • **Strength (STR)**: Elevated through heavy Push/Pull/Legs volume (Bench, Squats, Deadlifts).
                • **Agility (AGI)**: Elevated through cardio protocols, sprint intervals, and high-cadence reps.
                • **Endurance (END)**: Built through persistent daily calorie & macro adherence.
                
                *Recommendation: Complete today's daily routines to allocate +10 bonus attribute points.*
                """.trimIndent()
            }
            lower.contains("boss") || lower.contains("quest") || lower.contains("golem") -> {
                """
                ⚔️ **[WEEKLY BOSS BATTLE ANALYSIS]**
                Target Entity: **Bench Press Golem** (5,000 kg Volume Challenge).
                
                • **Tactical Approach**: Distribute load across 2-3 chest/push sessions (e.g. 4 sets of 8 reps @ 75kg = 2,400kg per session).
                • **Mechanical Overload**: Ensure full lockout and 2-second pause reps for maximum biomechanical damage.
                • **Reward**: Defeating the Golem awards +300 XP, +15 STR, and the rare Golem Slayer badge!
                """.trimIndent()
            }
            lower.contains("protein") || lower.contains("meal") || lower.contains("snack") || lower.contains("food") || lower.contains("nutrition") -> {
                """
                🧪 **[ROOM TELEMETRY: FUEL SYNTHESIS]**
                *Current Nutrition Profile:*
                $userContext
                
                • **Anabolic Window**: Prioritize 35-40g high-leucine protein source within 90 minutes post-training.
                • **Cyber-Fuel Macro Split**: 200g Lean Chicken / Tofu + 1.5 cups Jasmine Rice + 1 tbsp Olive Oil (approx 52g P, 65g C, 14g F).
                • **Electrolyte Balance**: 500ml water with sodium and potassium citrate to maintain intracellular hydration for peak pump.
                """.trimIndent()
            }
            lower.contains("volume") || lower.contains("workout") || lower.contains("routine") || lower.contains("split") -> {
                """
                🦾 **[HYPERTROPHY & OVERLOAD MATRIX]**
                *Database Workout Status:*
                $userContext
                
                • **Progressive Micro-Loading**: Add 1.25kg to 2.5kg per working set or 1 extra repetition before increasing resistance.
                • **Working Split**:
                  - Day 1: Heavy Push (Chest/Shoulders/Triceps) -> Builds STR
                  - Day 2: Dynamic Pull (Back/Biceps/Rear Delts) -> Builds STR
                  - Day 3: Legs & Core (Squats/RDLs) -> Builds STR & END
                  - Day 4: High-Cadence HIIT / Cardio -> Builds AGI
                """.trimIndent()
            }
            else -> {
                """
                🤖 **[CYBERCOACH UPLINK ACTIVE]**
                *Local Room Database Synced:*
                $userContext
                
                I am your Gemini-powered conditioning intelligence. I can analyze:
                • **Workout Volume & Muscle Balancing**: Tailored to your STR & AGI levels.
                • **Macro Synchronization**: Caloric & protein targets based on today's logged meals.
                • **Boss Quest Tactics**: Weekly volume pacing to conquer the Bench Press Golem.
                • **Daily Routine Optimization**: Habit completion and recovery protocols.
                
                What telemetry data or routine shall we optimize today?
                """.trimIndent()
            }
        }
    }
}
