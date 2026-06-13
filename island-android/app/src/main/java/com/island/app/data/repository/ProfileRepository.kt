package com.island.app.data.repository

import android.content.Context
import com.island.app.data.model.PlayerProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileRepository(context: Context) {
    private val prefs = context.getSharedPreferences("island_profile", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(load())
    val profile: StateFlow<PlayerProfile> = _profile

    private fun load() = PlayerProfile(
        name = prefs.getString("name", "Player") ?: "Player",
        flagIndex = prefs.getInt("flagIndex", 0),
        coins = prefs.getInt("coins", 100),
        keys = prefs.getInt("keys", 54),
        gems = prefs.getInt("gems", 0)
    )

    fun save(profile: PlayerProfile) {
        prefs.edit()
            .putString("name", profile.name)
            .putInt("flagIndex", profile.flagIndex)
            .putInt("coins", profile.coins)
            .putInt("keys", profile.keys)
            .putInt("gems", profile.gems)
            .apply()
        _profile.value = profile
    }
}
