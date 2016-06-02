package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 * Contains the performances relative to a specific test.
 *
 * @author Francesco Illuminati
 */
class TestPerformanceImpl implements TestPerformance, Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Measure time;
    private final long originalTotalSamples;
    private final long iterations;
    private final long totalTime;

    private MeasureRatio ratio;
    private double tukey;

    public TestPerformanceImpl(String name,
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

    @Override
    public Measure getElapsedNanosecondsPerCycle() {
        return time;
    }

    @Override
    public MeasureRatio getPercentage() {
        return ratio;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getTukeyHsd() {
        return tukey;
    }

    @Override
    public long getIterations() {
        return iterations;
    }

    @Override
    public long getOriginalTotalSamples() {
        return originalTotalSamples;
    }

    @Override
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
