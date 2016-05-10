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

    private String name;
    private long totalTime;
    private long iterations;
    private Measure nullTest;

    /** Clone constructor. */
    public IterationRunningMeasure(IterationRunningMeasure other) {
        super(other);
        this.name = other.name;
        this.iterations = other.iterations;
        this.totalTime = other.totalTime;
    }

    public IterationRunningMeasure(String name) {
        this.name = name;
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
        return mean() + " ± " + marginOfError(confidence) +
                " (" + count() + " samples, " + iterations + " iterations)";
    }

    @Override
    public String toString() {
        return toStringForConfidence(0.95);
    }
}
