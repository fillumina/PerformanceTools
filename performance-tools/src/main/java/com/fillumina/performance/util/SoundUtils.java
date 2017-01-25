package com.fillumina.performance.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SoundUtils {

    public static void playResource(final String filename) {
        URL url = SoundUtils.class.getResource(filename);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        SoundUtils.play(file);
    }

    public static void play(File yourFile) {
        try {
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            long length = clip.getMicrosecondLength();
            clip.start();
            clip.drain();
        } catch (IOException | UnsupportedAudioFileException |
                LineUnavailableException ex) {
            throw new RuntimeException(ex);
        }
    }
}
