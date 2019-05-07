package es.etologic.mahjongscoring2.app.utils;

import android.media.AudioManager;
import android.media.SoundPool;

public final class PreLollipopSoundPool {
    @SuppressWarnings("deprecation")
    public static SoundPool NewSoundPool() {
        return new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    }
}
