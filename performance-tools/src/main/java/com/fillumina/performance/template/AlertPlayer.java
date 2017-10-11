package com.fillumina.performance.template;

import com.fillumina.performance.util.PlayAlert;
import com.fillumina.performance.util.SoundUtils;
import java.io.File;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AlertPlayer {

    public interface Configuration {
        String getFailureAudioFilename();
        String getSuccessAudioFilename();
        boolean isAlertActive();
    }

    private final Configuration config;

    public AlertPlayer(Configuration config) {
        this.config = config;
    }

    public void onSuccess() {
        if (config.isAlertActive()) {
            String filenameOk = config.getSuccessAudioFilename();
            if (filenameOk == null) {
                PlayAlert.success();
            } else {
                playFilename(filenameOk);
            }
        }
    }

    public void onFailure() {
        if (config.isAlertActive()) {
            String filenameErr = config.getFailureAudioFilename();
            if (filenameErr == null) {
                PlayAlert.error();
            } else {
                playFilename(filenameErr);
            }
        }
    }

    private void playFilename(String filename) {
        final File file = new File(filename);
        SoundUtils.play(file);
    }
}
