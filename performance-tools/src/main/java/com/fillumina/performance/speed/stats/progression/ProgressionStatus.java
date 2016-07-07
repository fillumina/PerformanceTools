package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TableFormatter;
import java.util.Arrays;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionStatus {

    private final String message;
    private final int sample;
    private final int totalSamples;
    private final int repetition;
    private final int[] iterations;
    private final SpeedStats lastStats;

    public ProgressionStatus(String message,
            int sample,
            int totalSamples,
            int repetition,
            int[] iterations,
            SpeedStats lastStats) {
        this.message = message;
        this.sample = sample;
        this.totalSamples = totalSamples;
        this.repetition = repetition;
        this.iterations = Arrays.copyOf(iterations, iterations.length);
        this.lastStats = lastStats;
    }

    public String getMessage() {
        return message;
    }

    public int getSample() {
        return sample;
    }

    public int getRepetition() {
        return repetition;
    }

    public SpeedStats getLastStats() {
        return lastStats;
    }

    public int getTotalSamples() {
        return totalSamples;
    }

    public int getRepetitions() {
        return repetition;
    }

    public int[] getIterations() {
        return iterations;
    }

    @Override
    public String toString() {
        return new TableFormatter()
                .header("Progression Status", '-')
                .param("message", message)
                .param("sample", sample)
                .param("total samples", totalSamples)
                .param("repetition", repetition)
                .param("iterations", Arrays.toString(iterations))
                .toString() +
                System.lineSeparator() +
                TableFormatter.title("Last Statistics:", '-') +
                lastStats.toString();
    }
}
