package com.yacoo.rpg.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

// ── HapticManager ─────────────────────────────────────────────────────

class HapticManager(context: Context) {

    private val vibrator: Vibrator? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }.getOrNull()

    private fun safeVibrate(block: () -> Unit) {
        try { block() } catch (_: Exception) { /* 권한 없거나 기기 미지원 시 무시 */ }
    }

    /** Short tick — dice held/unheld */
    fun tick() = safeVibrate {
        vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Thump — dice thrown */
    fun thump() = safeVibrate {
        vibrator?.vibrate(VibrationEffect.createOneShot(40, 180))
    }

    /** Double knock — attack hit */
    fun hit() = safeVibrate {
        val pattern   = longArrayOf(0, 30, 60, 30)
        val amplitudes = intArrayOf(0, 200, 0, 140)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
    }

    /** Long rumble — victory */
    fun victory() = safeVibrate {
        val pattern   = longArrayOf(0, 60, 40, 80, 40, 120)
        val amplitudes = intArrayOf(0, 160, 0, 200, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
    }

    fun cancel() { try { vibrator?.cancel() } catch (_: Exception) { } }
}

@Composable
fun rememberHapticManager(): HapticManager {
    val ctx = LocalContext.current
    return remember(ctx) { HapticManager(ctx) }
}

class SoundManager {

    fun playDiceRoll()  {}
    fun playAttackHit() {}
    fun playVictory()   {}
    fun playDefeat()    {}

    fun release() {}
}

@Composable
fun rememberSoundManager(): SoundManager {
    return remember { SoundManager() }
}
