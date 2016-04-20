package com.fillumina.performance.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 * Contains the performances relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class TestPerformances implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final Measure time;
    private final Measure slower;
    private final double confidence;
    private final long iterations;

    public TestPerformances(String name, Measure time, Measure slower,
            double confidence, long iterations) {
        this.name = name;
        this.time = time;
        this.slower = slower;
        this.confidence = confidence;
        this.iterations = iterations;
    }

    public Measure getElapsedNanosecondsPerCycle() {
        return time;
    }

    public MeasureRatio getPercentage() {
        return new MeasureRatio(time, slower, confidence);
    }

    public String getName() {
        return name;
    }

    public double getConfidence() {
        return confidence;
    }

    public long getIterations() {
        return iterations;
    }

    @Override
    public String toString() {
        return name + ":\t" + time.toString() +
                "\t " + getPercentage().toString() +
                "\t (" + iterations + ")";
    }
}
