package com.fillumina.performance.util;

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
    private double prevError;

    public void start() {
        prevError = 0.0;
        stopWatch.start();
    }

    public Quantity<IntervalUnit> getEta(double error) {
        if (error == 0) {
            return ZERO;
        }
        double elapsed = stopWatch.getNanosecondsSinceStart();
        double slope = error / (prevError - error);
        double estimated = elapsed * slope;
        stopWatch.start();
        prevError = error;
        return Quantity.from(estimated, IntervalUnit.NANOSECONDS);
    }
}
