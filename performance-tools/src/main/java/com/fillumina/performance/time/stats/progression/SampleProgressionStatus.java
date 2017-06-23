package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
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
    private final TimeSample speedSample;
    private final TimeStats lastStats;
    private final int timeSpentCoolingCpuMs;
    private final TimeSampleCollector collector;

    public SampleProgressionStatus(String rejectionMessage,
            int sample,
            int totalSamples,
            int repetition,
            int[] iterations,
            TimeSample speedSample,
            TimeStats lastStats,
            int timeSpentCoolingCpuMs,
            TimeSampleCollector collector) {
        this.rejectionMessage = rejectionMessage;
        this.sample = sample;
        this.totalSamples = totalSamples;
        this.repetition = repetition;
        this.iterations = Arrays.copyOf(iterations, iterations.length);
        this.speedSample = speedSample;
        this.lastStats = lastStats;
        this.timeSpentCoolingCpuMs = timeSpentCoolingCpuMs;
        this.collector = collector;
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

    public TimeSample getSpeedSample() {
        return speedSample;
    }

    public TimeStats getLastStats() {
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

    public TimeSampleCollector getSpeedSampleCollector() {
        return collector;
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
