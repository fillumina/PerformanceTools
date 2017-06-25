package com.fillumina.performance.time.stats;

import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class SingleTimeStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TName name;
    private final DimensionalMeasure measure;
    private final long totalIterations;
    private final long samples;
    private final long originalSamples;
    private final long totalTime;

    public SingleTimeStats(TName name,
            DimensionalMeasure measure,
            long totalIterations,
            long samples,
            long originalSamples,
            long totalTime) {
        this.name = name;
        this.measure = measure;
        this.totalIterations = totalIterations;
        this.samples = samples;
        this.originalSamples = originalSamples;
        this.totalTime = totalTime;
    }

    /** It depends on the type of measurement taken. */
    public DimensionalMeasure getMeasure() {
        return measure;
    }

    /** Test name. */
    public TName getName() {
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
        return name + ":\t" + measure.toString() +
                "\t (" + totalIterations + ")";
    }
}
