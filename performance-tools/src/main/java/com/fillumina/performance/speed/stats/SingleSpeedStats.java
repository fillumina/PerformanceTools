package com.fillumina.performance.speed.stats;

import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class SingleSpeedStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final DimensionalMeasure timeNs;
    private final long totalIterations;
    private final long samples;
    private final long originalSamples;
    private final long totalTime;

    public SingleSpeedStats(String name,
            DimensionalMeasure timeNs,
            long totalIterations,
            long samples,
            long originalSamples,
            long totalTime) {
        this.name = name;
        this.timeNs = timeNs;
        this.totalIterations = totalIterations;
        this.samples = samples;
        this.originalSamples = originalSamples;
        this.totalTime = totalTime;
    }

    /** Statistics about the elapsed time per cycle. */
    public DimensionalMeasure getElapsedNanosecondsPerCycle() {
        return timeNs;
    }

    /** Test name. */
    public String getName() {
        return name;
    }

    /** Total number of iIterations performed. */
    public long getOriginalSamples() {
        return originalSamples;
    }

    /** Total time used to perform the test. */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Total number of iIterations used to create the statistics
     * (after outliers elimination).
     */
    public long getTotalIterations() {
        return totalIterations;
    }

    public long getIterationsPerSample() {
        return totalIterations / samples;
    }

    public long getSamples() {
        return samples;
    }

    @Override
    public String toString() {
        return name + ":\t" + timeNs.toString() +
                "\t (" + totalIterations + ")";
    }
}
