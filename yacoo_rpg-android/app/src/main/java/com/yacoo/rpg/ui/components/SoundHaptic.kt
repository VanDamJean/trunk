package com.yacoo.rpg.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.yacoo.rpg.R

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

// ── SoundManager ─────────────────────────────────────────────────────

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    
    private var sfxClickId = -1
    private var sfxHitId = -1
    private var sfxVictoryId = -1
    private var sfxDefeatId = -1
    private var sfxUpgradeId = -1

    init {
        // Initialize MediaPlayer for BGM
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.bgm_lobby).apply {
                isLooping = true
                setVolume(0.2f, 0.2f)
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Initialize SoundPool for SFX
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        // Load SFX files
        soundPool?.let { pool ->
            sfxClickId = pool.load(context, R.raw.sfx_click, 1)
            sfxHitId = pool.load(context, R.raw.sfx_hit, 1)
            sfxVictoryId = pool.load(context, R.raw.sfx_victory, 1)
            sfxDefeatId = pool.load(context, R.raw.sfx_defeat, 1)
            sfxUpgradeId = pool.load(context, R.raw.sfx_upgrade, 1)
        }
    }

    private fun playSfx(soundId: Int) {
        if (soundId != -1) {
            soundPool?.play(soundId, 0.6f, 0.6f, 1, 0, 1.0f)
        }
    }

    fun playClick() {
        playSfx(sfxClickId)
    }

    fun playDiceRoll() {
        playSfx(sfxClickId)
    }

    fun playAttackHit() {
        playSfx(sfxHitId)
    }

    fun playVictory() {
        // Dim BGM temporarily if desired, play victory
        playSfx(sfxVictoryId)
    }

    fun playDefeat() {
        playSfx(sfxDefeatId)
    }

    fun playUpgrade() {
        playSfx(sfxUpgradeId)
    }

    fun release() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            
            soundPool?.release()
            soundPool = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current.applicationContext
    val soundManager = remember(context) { SoundManager(context) }
    
    DisposableEffect(soundManager) {
        onDispose {
            soundManager.release()
        }
    }
    
    return soundManager
}
