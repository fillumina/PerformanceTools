package com.fillumina.performance.stats;

import com.fillumina.performance.sample.TimeIteration;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;

/**
 * A {@link RunningOnlineMeasure} that keeps track of the total number of iterations
 * executed.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationRunningMeasure extends OnlineMeasure {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int sampleBeforeCleaning;

    private long totalTime;
    private long iterations;
    private Measure nullTest;

    /** Clone constructor. */
    public IterationRunningMeasure(IterationRunningMeasure other) {
        super(other);
        this.name = other.name;
        this.sampleBeforeCleaning = other.sampleBeforeCleaning;

        this.iterations = other.iterations;
        this.totalTime = other.totalTime;
    }

    public IterationRunningMeasure(String name, int samplesBeforeCleaning) {
        this.name = name;
        this.sampleBeforeCleaning = samplesBeforeCleaning;
    }

    IterationRunningMeasure add(TimeIteration ti) {
        iterations += ti.getIterations();
        totalTime += ti.getTime();
        super.add(ti.getTimePerIteration());
        return this;
    }

    IterationRunningMeasure addBaseline(Measure nullTest) {
        this.nullTest = nullTest;
        return this;
    }

    public int getOriginalTotalSamples() {
        return sampleBeforeCleaning;
    }

    public long getIterations() {
        return iterations;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toStringForConfidence(double confidence) {
        return String.format("%.4f ± %.4f (%d samples %d iterations)",
            getMean(), getMarginOfError(confidence),
            getCount(), iterations);
    }

    @Override
    public String toString() {
        return toStringForConfidence(0.95);
    }
}
