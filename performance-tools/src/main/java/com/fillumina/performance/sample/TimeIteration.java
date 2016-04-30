package com.fillumina.performance.sample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeIteration {

    private long time;
    private long iterations;

    public TimeIteration() {
        this(0, 0);
    }

    public TimeIteration(long elapsed, long iterations) {
        this.time = elapsed;
        this.iterations = iterations;
    }

    void add(long time, long iterations) {
        this.time += time;
        this.iterations += iterations;
    }

    public long getTime() {
        return time;
    }

    public long getIterations() {
        return iterations;
    }

    public double getTimePerIteration() {
        return time * 1.0 / iterations;
    }

    @Override
    public String toString() {
        return "{" + "time=" + time + ", iterations=" + iterations +
                '}';
    }
}
