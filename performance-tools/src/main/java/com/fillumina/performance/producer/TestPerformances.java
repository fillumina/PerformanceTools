package com.fillumina.performance.producer;

import java.io.Serializable;

/**
 * Contains the performances relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class TestPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final long elapsedNanoseconds;
    private final float percentage;
    private final double elapsedNanosecondsPerCycle;

    public TestPerformances(final String name,
            final long elapsedNanoseconds,
            final float percentage,
            final double elapsedNanosecondsPerCycle) {
        this.name = name;
        this.elapsedNanoseconds = elapsedNanoseconds;
        this.percentage = percentage;
        this.elapsedNanosecondsPerCycle = elapsedNanosecondsPerCycle;
    }

    public double getElapsedNanosecondsPerCycle() {
        return elapsedNanosecondsPerCycle;
    }

    public float getPercentage() {
        return percentage;
    }

    public long getElapsedNanoseconds() {
        return elapsedNanoseconds;
    }

    public String getName() {
        return name;
    }
}
