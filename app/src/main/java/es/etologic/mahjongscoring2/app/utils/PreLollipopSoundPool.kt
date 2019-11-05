package es.etologic.mahjongscoring2.app.utils

import android.media.AudioManager
import android.media.SoundPool

object PreLollipopSoundPool {
    fun newSoundPool(): SoundPool {
        return SoundPool(1, AudioManager.STREAM_MUSIC, 0)
    }
}
