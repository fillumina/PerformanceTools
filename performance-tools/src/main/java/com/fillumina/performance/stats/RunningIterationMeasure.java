package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.RunningMeasure;

/**
 *
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunningIterationMeasure extends RunningMeasure {
    private static final long serialVersionUID = 1L;
    private long iteration;

    public RunningIterationMeasure() {
        super();
    }

    /** Adds the measurement and the number of iterations. */
    protected Measure add(double value, long iteration) {
        this.iteration += iteration;
        return super.add(value);
    }

    public long getIteration() {
        return iteration;
    }
}
