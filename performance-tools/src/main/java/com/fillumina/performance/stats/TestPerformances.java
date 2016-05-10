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
    private final MeasureRatio ratio;
    private final double confidence;
    private final double tukey;
    private final long iterations;
    private final long totalTime;

    public TestPerformances(String name,
            Measure time,
            Measure slower,
            double confidence,
            double tukey,
            long iterations,
            long totalTime) {
        this.name = name;
        this.time = time;
        this.ratio = (slower == null) ?
                new MeasureRatio(time, confidence) :
                new MeasureRatio(time, slower, confidence);
        this.confidence = confidence;
        this.tukey = tukey;
        this.iterations = iterations;
        this.totalTime = totalTime;
    }

    public Measure getElapsedNanosecondsPerCycle() {
        return time;
    }

    public MeasureRatio getPercentage() {
        return ratio;
    }

    public String getName() {
        return name;
    }

    public double getTukeyHsd() {
        return tukey;
    }

    public double getConfidence() {
        return confidence;
    }

    public long getIterations() {
        return iterations;
    }

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
