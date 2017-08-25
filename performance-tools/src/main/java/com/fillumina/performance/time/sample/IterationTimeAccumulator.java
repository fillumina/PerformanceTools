package com.fillumina.performance.time.sample;

/**
 * A single test iteration can be eventually split into different fractions
 * that can be added separately. This class adds different takes to the
 * same iteration sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IterationTimeAccumulator {
    private long time;
    private long iterations;

    public IterationTimeAccumulator() {
        this(0, 0);
    }

    public IterationTimeAccumulator(long time, long iterations) {
        this.time = time;
        this.iterations = iterations;
    }

    public IterationTimeAccumulator add(long time, long iterations) {
        this.time += time;
        this.iterations += iterations;
        return this;
    }

    long getTimeNs() {
        return time;
    }

    long getIterations() {
        return iterations;
    }

    @Override
    public String toString() {
        return "{" + "time=" + time + ", iterations=" + iterations + '}';
    }
}
