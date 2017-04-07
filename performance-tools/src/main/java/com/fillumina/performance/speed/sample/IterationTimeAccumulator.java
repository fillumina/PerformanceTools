package com.fillumina.performance.speed.sample;

import java.io.Serializable;

/**
 * A single test iteration can be eventually split into different fractions
 * that can be added separately. This class adds different takes to the
 * same iteration sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeAccumulator implements IterationTime, Serializable {
    private static final long serialVersionUID = 1L;

    private long time;
    private long iterations;

    public IterationTimeAccumulator() {
        this(0, 0);
    }

    public IterationTimeAccumulator(long elapsed, long iterations) {
        this.time = elapsed;
        this.iterations = iterations;
    }

    public IterationTimeAccumulator add(long time, long iterations) {
        this.time += time;
        this.iterations += iterations;
        return this;
    }

    @Override
    public long getTimeNs() {
        return time;
    }

    @Override
    public long getIterations() {
        return iterations;
    }

    @Override
    public double getTimePerIterationNs() {
        return time * 1.0 / iterations;
    }

    @Override
    public String toString() {
        return "{" + "time=" + time + ", iterations=" + iterations + '}';
    }
}
