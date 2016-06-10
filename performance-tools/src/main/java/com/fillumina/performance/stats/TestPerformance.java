package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class TestPerformance implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Measure time;
    private final long originalTotalSamples;
    private final long iterations;
    private final long totalTime;

    private MeasureRatio ratio;
    private double tukey;

    public TestPerformance(String name,
            Measure time,
            long iterations,
            long originalTotalSamples,
            long totalTime) {
        this.name = name;
        this.time = time;
        this.iterations = iterations;
        this.originalTotalSamples = originalTotalSamples;
        this.totalTime = totalTime;
    }

    void setRatio(MeasureRatio ratio, double tukey) {
        this.ratio = ratio;
        this.tukey = tukey;
    }

    /** Statistics about the elapsed time per cycle. */
    public Measure getElapsedNanosecondsPerCycle() {
        return time;
    }

    /** Comparison between the present test and the slowest one. */
    public MeasureRatio getPercentage() {
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

    /**
     * Total number of iterations used to create the statistics
     * (after outliers elimination).
     */
    public long getIterations() {
        return iterations;
    }

    /** Total number of iterations performed. */
    public long getOriginalTotalSamples() {
        return originalTotalSamples;
    }

    /** Total time used to perform the test. */
    public long getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return name + ":\t" + time.toString() +
                "\t " + getPercentage().toString() +
                "\t (" + iterations + ")";
    }
}
