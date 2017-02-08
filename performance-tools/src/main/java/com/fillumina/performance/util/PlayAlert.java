package com.fillumina.performance.util;

/**
 * Plays default sounds.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PlayAlert {
    // http://www.mediacollege.com/downloads/sound-effects/audience/cheer-hooter-01.wav
    private static final String BOMB =
            "/com/fillumina/performance/util/wav/bomb.wav";
    private static final String CHEER =
            "/com/fillumina/performance/util/wav/cheer.wav";

    public static void success() {
        SoundUtils.playResource(CHEER);
    }

    public static void error() {
        SoundUtils.playResource(BOMB);
    }
}
