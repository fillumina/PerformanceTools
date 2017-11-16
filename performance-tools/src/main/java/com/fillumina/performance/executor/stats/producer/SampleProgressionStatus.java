package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleProgressionStatus {

    private final int executedSamples;
    private final int totalSamples;
    private final UnmodifiableIntList iterations;
    private final int repetition;

    private final Map<Class<?>,? extends AbstractSample<?,?,?>> samples;
    private final MixedAssertableHolder lastStats;
    private final int timeSpentCoolingCpuMs;
    private final String statusMessage;

    public SampleProgressionStatus(
            int executedSamples,
            int totalSamples,
            int repetition,
            UnmodifiableIntList iterations,
            Map<Class<?>,? extends AbstractSample<?,?,?>> sample,
            MixedAssertableHolder mixedHolder,
            int timeSpentCoolingCpuMs,
            String statusMessage) {
        this.statusMessage = statusMessage;
        this.executedSamples = executedSamples;
        this.totalSamples = totalSamples;
        this.repetition = repetition;
        this.iterations = iterations;
        this.samples = sample;
        this.lastStats = mixedHolder;
        this.timeSpentCoolingCpuMs = timeSpentCoolingCpuMs;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getExecutedSamples() {
        return executedSamples;
    }

    public int getRepetitions() {
        return repetition;
    }

    public Map<Class<?>,? extends AbstractSample<?,?,?>> getSamples() {
        return samples;
    }


    public MixedAssertableHolder getLastStats() {
        return lastStats;
    }

    public int getTotalSamples() {
        return totalSamples;
    }

    public UnmodifiableIntList getIterations() {
        return iterations;
    }

    public int getTimeSpentCoolingCpuMs() {
        return timeSpentCoolingCpuMs;
    }

    @Override
    public String toString() {
        final TableFormatter table = new TableFormatter()
                        .headerLeft("Progression Status", '-')
                        .param("message", statusMessage)
                        .param("samples executed", executedSamples)
                        .param("samples required", totalSamples)
                        .param("repetitions", repetition)
                        .param("iterations", iterations.toString());

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
