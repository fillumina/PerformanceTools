package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleProgressionStatus {

    private final int executedSamples;
    private final int totalSamples;
    private final int[] iterations;
    private final int repetition;

    private final Map<Class<?>,? extends AbstractSample<?,?,?>> sample;
    private final MixedAssertableHolder lastStats;
    private final int timeSpentCoolingCpuMs;
    private final String errorMessage;

    public SampleProgressionStatus(
            int executedSamples,
            int[] iterations,
            int totalSamples,
            int repetition,
            Map<Class<?>,? extends AbstractSample<?,?,?>> sample,
            MixedAssertableHolder mixedHolder,
            int timeSpentCoolingCpuMs,
            String errorMessage) {
        this.errorMessage = errorMessage;
        this.executedSamples = executedSamples;
        this.totalSamples = totalSamples;
        this.repetition = repetition;
        this.iterations = Arrays.copyOf(iterations, iterations.length);
        this.sample = sample;
        this.lastStats = mixedHolder;
        this.timeSpentCoolingCpuMs = timeSpentCoolingCpuMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getExecutedSamples() {
        return executedSamples;
    }

    public int getRepetitions() {
        return repetition;
    }

    public Map<Class<?>,? extends AbstractSample<?,?,?>> getSample() {
        return sample;
    }


    public MixedAssertableHolder getLastStats() {
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
                        .param("message", errorMessage)
                        .param("samples executed", executedSamples)
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
