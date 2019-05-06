package com.fillumina.performance.util;

import com.fillumina.performance.util.collection.CircularBuffer;
import com.fillumina.performance.util.stats.Point;
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
            Quantity.of(0, IntervalUnit.SECONDS);

    private final StopWatch stopWatch = new StopWatch();
    private long  totalTimeNs;
    private CircularBuffer<PastValue> history;
    private PastValue first;

    public void start() {
        stopWatch.start();
        totalTimeNs = 0;
        history = new CircularBuffer<>(10);
        first = null;
    }

    public Quantity<IntervalUnit> getEta(double error) {
        if (error == 0) {
            return ZERO;
        }

        double elapsedNs = stopWatch.getNanosecondsSinceStart();
        totalTimeNs += elapsedNs;
        stopWatch.start();

        PastValue past = history.putAndGetOlder(new PastValue(totalTimeNs, error));

        double approxEtaNs = getApproxLinearEtaNs(error, totalTimeNs, past);
        Quantity<IntervalUnit> approxEta =
                Quantity.of(approxEtaNs, IntervalUnit.NANOSECONDS);

        return approxEta;
    }

    private double getApproxLinearEtaNs(double error, long totalTimeNs,
            PastValue past) {
        if (past == null) {
            if (first == null) {
                first = new PastValue(totalTimeNs, error);
                return 0;
            }
            past = first;
        }

        double slope = error / (past.error - error);
        double linearEtaNs = (totalTimeNs - past.time) * slope;

        return linearEtaNs;
    }

    static class PastValue implements Point {
        private final double error;
        private final long time;

        public PastValue(long time, double error) {
            this.error = error;
            this.time = time;
        }

        @Override
        public double getX() {
            return time;
        }

        @Override
        public double getY() {
            return error;
        }

        @Override
        public String toString() {
            return "{error=" + error + ", time=" + time + '}';
        }
    }
}
