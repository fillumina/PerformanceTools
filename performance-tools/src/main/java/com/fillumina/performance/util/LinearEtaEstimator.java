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
    private long  totalTimeNs;
    private History history;
    private PastValue first;

    public void start() {
        stopWatch.start();
        totalTimeNs = 0;
        history = new History(10);
        first = null;
    }

    public Quantity<IntervalUnit> getEta(double error) {
        double elapsedNs = stopWatch.getNanosecondsSinceStart();
        stopWatch.start();
        double estimatedNs = getEtaNs(error, elapsedNs);
        return Quantity.from(estimatedNs, IntervalUnit.NANOSECONDS);
    }

    private double getEtaNs(double error, double itElapsedNs) {
        if (error == 0) {
            return 0;
        }

        totalTimeNs += itElapsedNs;

        PastValue past = history.get();

        if (past == null) {
            if (first == null) {
                first = new PastValue(error, totalTimeNs);
                return 0;
            }
            past = first;
        }

        double slope = error / (past.error - error);
        double linearEtaNs = (totalTimeNs - past.time) * slope;

        history.add(new PastValue(error, totalTimeNs));

        return linearEtaNs;
    }

    private static class PastValue {
        private final double error;
        private final long time;

        public PastValue(double error, long time) {
            this.error = error;
            this.time = time;
        }
    }

    private static class History {
        private PastValue[] data;
        private int index;

        public History(int size) {
            data = new PastValue[size];
        }

        public void add(PastValue value) {
            data[index] = value;
            index = (index + 1) % data.length;
        }

        public PastValue get() {
            return data[index];
        }
    }

}
