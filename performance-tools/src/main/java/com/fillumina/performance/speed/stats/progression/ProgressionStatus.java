package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TableFormatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionStatus {

    private final String message;
    private final int sample;
    private final int repetitions;
    private final SpeedStats lastStats;

    public ProgressionStatus(String message, int sample, int repetition,
            SpeedStats lastStats) {
        this.message = message;
        this.sample = sample;
        this.repetitions = repetition;
        this.lastStats = lastStats;
    }

    public String getMessage() {
        return message;
    }

    public int getSample() {
        return sample;
    }

    public int getRepetition() {
        return repetitions;
    }

    public SpeedStats getLastStats() {
        return lastStats;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(TableFormatter.title("ProgressionStatus:", '-'));
        if (message != null && !message.isEmpty()) {
            buf.append("message:\t")
                    .append(message)
                    .append(System.lineSeparator());
        }
        buf.append("sample:\t\t")
                .append(sample)
                .append(System.lineSeparator());
        buf.append("repetitions:\t")
                .append(repetitions)
                .append(System.lineSeparator());
        if (lastStats != null) {
            buf.append(System.lineSeparator())
                .append(TableFormatter.title("Last Statistics:", '-'))
                .append(lastStats.toString());
        }
        return buf.toString();
    }
}
