package com.gemsin.app.data

data class OnboardingItem(val id: String, val badge: String, val title: String, val description: String)
data class Game(val id: String, val title: String, val mode: String, val difficulty: String, val desc: String, val emoji: String)
data class LeaderboardEntry(val name: String, val score: Int, val initials: String, val isMe: Boolean = false)
data class Pack(val key: String, val name: String, val credit: Int, val price: String)
data class Friend(val name: String, val status: String, val initials: String, val following: Boolean)
data class HistoryEntry(val game: String, val time: String, val score: Int, val range: String, val icon: String)

object MockData {
    val onboarding = listOf(
        OnboardingItem("onboarding-1", "GEMSIN", "Games Are More Fun!", "We'll help you find your next favorite game. Save your progress and track your achievements as you level up."),
        OnboardingItem("onboarding-2", "LEVEL UP", "Compete & Earn", "Climb the leaderboard, collect diamonds, and unlock rewards as you complete daily challenges."),
        OnboardingItem("onboarding-3", "COMMUNITY", "Play Together", "Connect with friends, share your game history, and join a community built for casual and competitive players."),
    )

    val games = listOf(
        Game("flappy-bird", "Flappy Bird", "Single Player", "Easy", "Tap to fly and avoid obstacles. Short-session game for quick score runs.", "🐦"),
        Game("ular-angka", "Ular Angka", "Single Player", "Medium", "Number-snake puzzle style game focused on timing and pattern recognition.", "🐍"),
        Game("tetris", "Tetris", "Multiplayer", "Medium", "Stack blocks efficiently and compete for high lines and speed control.", "🧱"),
        Game("jump-man", "Jump Man", "Single Player", "Easy", "Arcade jumping challenge with simple controls and score progression.", "🏃"),
    )

    val leaderboard = listOf(
        LeaderboardEntry("bobbybow", 2400, "BB"),
        LeaderboardEntry("tiojunior", 1980, "TJ"),
        LeaderboardEntry("garryjake", 1890, "GJ"),
        LeaderboardEntry("MiraZone", 1530, "MZ"),
        LeaderboardEntry("KangPixel", 1420, "KP"),
        LeaderboardEntry("Levronix", 1318, "LX"),
        LeaderboardEntry("hrmandays", 1000, "HR"),
        LeaderboardEntry("You", 982, "YOU", isMe = true),
        LeaderboardEntry("madynguzna", 900, "MG"),
        LeaderboardEntry("gloryaaaaaa", 850, "GL"),
        LeaderboardEntry("katespade", 632, "KS"),
        LeaderboardEntry("callmegrandma", 591, "CG"),
    )

    val packs = listOf(
        Pack("buy-starter", "Starter Pack", 50, "\$0.99"),
        Pack("buy-pro", "Pro Pack", 250, "\$3.99"),
        Pack("buy-elite", "Elite Pack", 700, "\$8.99"),
    )

    val friends = listOf(
        Friend("KarinAyu", "Playing Tetris", "KA", false),
        Friend("RamaM", "Online 3m ago", "RM", true),
        Friend("LinaN", "Playing Flappy Bird", "LN", false),
    )

    val history = listOf(
        HistoryEntry("Flappy Bird", "Today, 09:12", 247, "today", "🐦"),
        HistoryEntry("Ular Angka", "Today, 08:44", 190, "today", "🐍"),
        HistoryEntry("Tetris", "Yesterday, 21:20", 305, "yesterday", "🧱"),
        HistoryEntry("Jump Man", "Yesterday, 18:53", 128, "yesterday", "🏃"),
    )
}
