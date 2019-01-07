package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.FixedSampleMean;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Quantity;

/**
 * Estimates how much time is needed to complete the task given the error
 * (the final error must be 0). The error sequence must be approximately
 * linear decreasing monotone.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinearEtaEstimator {
    public static final Quantity<IntervalUnit> ZERO =
            Quantity.from(0, IntervalUnit.SECONDS);

    private final StopWatch stopWatch = new StopWatch();
    private final StopWatch beginning = new StopWatch();
    private FixedSampleMean target;
    private double prevError;

    public void start() {
        prevError = 0.0;
        stopWatch.start();

        beginning.start();
        target = new FixedSampleMean(5);
    }

    public Quantity<IntervalUnit> getEta(double error) {
        if (error == 0) {
            return ZERO;
        }
        double elapsedNs = stopWatch.getNanosecondsSinceStart();
        double slope = error / (prevError - error);
        double estimatedNs = elapsedNs * slope;

        final long totalTimeNs = beginning.getNanosecondsSinceStart();
        target.addSample(estimatedNs + totalTimeNs);

        stopWatch.start();
        prevError = error;
        final double etaNs = target.getMean() - totalTimeNs;
        return Quantity.from(etaNs, IntervalUnit.NANOSECONDS);
    }
}
