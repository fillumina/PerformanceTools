package com.fillumina.performance.speed.stats;

import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class TestPerformance implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final DimensionalMeasure time;
    private final long totalIterations;
    private final long samples;
    private final long originalSamples;
    private final long totalTime;

    private MeasureRatio ratio;
    private double tukey;

    public TestPerformance(String name,
            DimensionalMeasure time,
            long totalIterations,
            long samples,
            long originalSamples,
            long totalTime) {
        this.name = name;
        this.time = time;
        this.totalIterations = totalIterations;
        this.samples = samples;
        this.originalSamples = originalSamples;
        this.totalTime = totalTime;
    }

    void setRatio(MeasureRatio ratio, double tukey) {
        this.ratio = ratio;
        this.tukey = tukey;
    }

    /** Statistics about the elapsed time per cycle. */
    public DimensionalMeasure getElapsedNanosecondsPerCycle() {
        return time;
    }

    /** Comparison between the present test and the slowest one. */
    public MeasureRatio getRatio() {
        return ratio;
    }

    /** Test name. */
    public String getName() {
        return name;
    }

    /** The Tukey HSD test value compared to the slowest test. */
    public double getTukeyHsd() {
        return tukey;
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
        return name + ":\t" + time.toString() +
                "\t " + getRatio().toString() +
                "\t (" + totalIterations + ")";
    }
}
