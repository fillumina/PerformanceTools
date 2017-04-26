package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.Arrays;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleProgressionStatus {

    private final String rejectionMessage;
    private final int sample;
    private final int totalSamples;
    private final int repetition;
    private final int[] iterations;
    private final SpeedSample speedSample;
    private final SpeedStats lastStats;
    private final int timeSpentCoolingCpuMs;

    public SampleProgressionStatus(String rejectionMessage,
            int sample,
            int totalSamples,
            int repetition,
            int[] iterations,
            SpeedSample speedSample,
            SpeedStats lastStats,
            int timeSpentCoolingCpuMs) {
        this.rejectionMessage = rejectionMessage;
        this.sample = sample;
        this.totalSamples = totalSamples;
        this.repetition = repetition;
        this.iterations = Arrays.copyOf(iterations, iterations.length);
        this.speedSample = speedSample;
        this.lastStats = lastStats;
        this.timeSpentCoolingCpuMs = timeSpentCoolingCpuMs;
    }

    public String getRejectionMessage() {
        return rejectionMessage;
    }

    public int getSample() {
        return sample;
    }

    public int getRepetitions() {
        return repetition;
    }

    public SpeedSample getSpeedSample() {
        return speedSample;
    }

    public SpeedStats getLastStats() {
        return lastStats;
    }

    public int getTotalSamples() {
        return totalSamples;
    }

    public int[] getIterations() {
        return iterations;
    }

    public int getTimeSpentCoolingCpuMs() {
        return timeSpentCoolingCpuMs;
    }

    @Override
    public String toString() {
        final TableFormatter table = new TableFormatter()
                        .headerLeft("Progression Status", '-')
                        .param("message", rejectionMessage)
                        .param("samples executed", sample)
                        .param("samples required", totalSamples)
                        .param("repetitions", repetition)
                        .param("iterations", Arrays.toString(iterations));

        if (timeSpentCoolingCpuMs == -1) {
            table.param("CPU cooling ms", "not executed");
        } else {
            table.param("CPU cooling ms", timeSpentCoolingCpuMs);
        }

        if (lastStats != null) {
            return table.toString() + System.lineSeparator() +
                TableFormatter.title("Last Statistics:", '-') +
                lastStats.toString();
        }
        return table.toString();
    }
}
