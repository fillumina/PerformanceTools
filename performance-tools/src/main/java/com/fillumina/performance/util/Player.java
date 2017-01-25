package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Player {
    private static final String BOMB =
            "/com/fillumina/performance/util/wav/bomb.wav";
    private static final String CHEER =
            "/com/fillumina/performance/util/wav/cheer.wav";

    public static void playSuccess() {
        SoundUtils.playResource(CHEER);
    }

    public static void playError() {
        SoundUtils.playResource(BOMB);
    }
}
