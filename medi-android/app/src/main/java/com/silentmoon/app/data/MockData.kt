package com.silentmoon.app.data

import com.silentmoon.app.R

data class MeditationSession(
    val id: Int,
    val title: String,
    val description: String,
    val duration: String,
    val icon: String,
    val category: String,
    val audioRes: Int = R.raw.waves
)

object MockData {
    val meditationSessions = listOf(
        MeditationSession(1, "Reduce Anxiety",    "Calm your mind and body", "10 MIN", "😌", "morning",   R.raw.waves),
        MeditationSession(2, "Improve Happiness", "Boost your mood",         "15 MIN", "😊", "afternoon", R.raw.bowl),
        MeditationSession(3, "Personal Growth",   "Develop yourself",        "20 MIN", "🌱", "morning",   R.raw.bowl),
        MeditationSession(4, "Better Sleep",      "Rest well tonight",       "30 MIN", "😴", "evening",   R.raw.sleep),
        MeditationSession(5, "Deep Focus",        "Get into the zone",       "25 MIN", "🎯", "afternoon", R.raw.waves),
    )

    val sleepSessions = listOf(
        MeditationSession(10, "Night Island",      "A peaceful journey to sleep", "45 MIN", "🏝️", "evening", R.raw.waves),
        MeditationSession(11, "Sweet Dreams",      "Drift into restful sleep",    "30 MIN", "☁️",  "evening", R.raw.sleep),
        MeditationSession(12, "Moonlight Stories", "Bedtime tales for adults",    "20 MIN", "📖",  "evening", R.raw.bowl),
        MeditationSession(13, "Sleep Music",       "Ambient sounds for rest",     "60 MIN", "🎵",  "evening", R.raw.sleep),
    )

    val topics = listOf(
        "Reduce Stress",
        "Reduce Anxiety",
        "Better Sleep",
        "Improve Focus",
        "Increase Happiness",
        "Personal Growth"
    )

    fun getSessionById(id: Int): MeditationSession? =
        (meditationSessions + sleepSessions).find { it.id == id }
}
