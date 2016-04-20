package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.RunningMeasure;

/**
 * A {@link RunningMeasure} that keeps track of the total number of iterations
 * executed.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationRunningMeasure extends RunningMeasure {
    private static final long serialVersionUID = 1L;

    private String name;
    private long iterations;

    /** Clone constructor. */
    public IterationRunningMeasure(IterationRunningMeasure other) {
        super(other);
        this.name = other.name;
        this.iterations = other.iterations;
    }

    public IterationRunningMeasure(String name) {
        this.name = name;
    }

    public RunningMeasure add(TimeIteration ti) {
        iterations += ti.getIterations();
        return super.add(ti.getTimePerIteration());
    }

    public long getIterations() {
        return iterations;
    }

    public String getName() {
        return name;
    }
}
