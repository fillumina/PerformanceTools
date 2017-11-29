package com.fillumina.performance.time;

import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.util.stats.OnlineMeasure;

/**
 * Heat could in some cases hinder the results of a performance test.
 * Extreme heat might provoke CPU/bus down-clocking and other collateral
 * that can affect the test. This utility tries to establish a
 * performance baseline so to detect when the general performances are
 * degraded. In that case it sleeps down the CPU for some time in an effort
 * to cool it.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HeatDetector {
    public static final HeatDetector INSTANCE = new HeatDetector();

    private static final int RETRIES = 30;
    private static final int BASE_MEASURE_COUNT = 6;
    private static final long SECONDS = 1_000_000_000;
    private static final int SECONDS_BEFORE_CHECK = 5;
    private static final int SECONDS_TO_WAIT = 3;

    private final OnlineMeasure expected = new OnlineMeasure();
    private final int iterations;
    private final int baseMeasureCount;
    private final int secondsBeforeCheck;
    private final int secondsToSleep;
    private final int maxRepetitions;
    private final Runnable testable = new LfsrRunnable();
    private double lastCheckValue;
    private long lastCheck;

    public HeatDetector() {
        this(BASE_MEASURE_COUNT, SECONDS_BEFORE_CHECK, SECONDS_TO_WAIT, RETRIES);
    }

    /**
     *
     * @param baseMeasureCount    how many heat measures should be taken before
     *                            considering the mean accurate
     * @param secondsBeforeCheck  the interval at which measures should be taken
     * @param secondsToSleep      how many seconds to sleep for cooling (base)
     * @param maxRepetitions      max number of sleep cycles routine before
     *                            considering the CPU cooled anyway
     */
    public HeatDetector(int baseMeasureCount,
            int secondsBeforeCheck,
            int secondsToSleep,
            int maxRepetitions) {
        this.baseMeasureCount = baseMeasureCount;
        this.secondsBeforeCheck = secondsBeforeCheck;
        this.secondsToSleep = secondsToSleep;
        this.maxRepetitions = maxRepetitions;
        this.iterations = initIterations();
        this.lastCheck = System.currentTimeMillis();
    }

    private int initIterations() {
        // warmup
        for (int k=0; k<100_000; k++) {
            testable.run();
        }
        // actual measure (50 ms + allowance)
        long end = System.nanoTime() + 60_000_000;
        int counter = 0;
        do {
            for (int k=0; k<1_000; k++) {
                testable.run();
                counter++;
            }
        } while (System.nanoTime() < end);
        // init parameters
        return counter;
    }

    /** Call this method to make it sure the object is initialized. */
    public void init() {
        // do nothing (will implicitly call the static creator)
    }

    /**
     * Check if the CPU is hot and eventually cool it down.
     *
     * @return -1 if no cooling down was needed, otherwise the time spent cooling
     *          in milliseconds.
     */
    public int checkCpuHeat() {
        long time = System.nanoTime();
        if (time - lastCheck > secondsBeforeCheck * SECONDS) {
            if (isHeated()) {
                coolDownCpu();
                long after = System.nanoTime();
                lastCheck = after;
                return (int)((after - time) / 1_000_000.0);
            } else {
                lastCheck = time;
                return 0;
            }
        }
        return -1;
    }

    public void coolDownCpu() {
        int counter = 0;
        do {
            sleepSeconds((int)(secondsToSleep * (Math.ceil(counter / 10))));
            if (counter > maxRepetitions) {
                // ok must be cooled. It's slow because it has clocked down.
                return;
            }
            counter++;
        } while (isHeated());
    }

    double getExpectedTimeNs() {
        return expected.getMean();
    }

    double getLastCheckTimeNs() {
        return lastCheckValue;
    }

    public boolean isHeated() {
        lastCheckValue = checkSpeed(0);
        if (expected.getCount() < baseMeasureCount) {
            expected.addSample(lastCheckValue);
            return false;
        } else {
            final boolean heated = lastCheckValue > (expected.getMean() * 1.2);
            if (!heated) {
                expected.addSample(lastCheckValue);
            }
            return heated;
        }
    }

    private void sleepSeconds(final int seconds) {
        try {
            Thread.sleep(seconds * 1_000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    final double checkSpeed(int warmup) {
        // to rise the CPU freq if it in is a low speed state
        // (i.e. when the system is at rest)
        for (int i=0; i<warmup * iterations; i++) {
            testable.run();
        }
        // actual measurement
        long start = System.nanoTime();
        for (int i=0; i<iterations; i++) {
            testable.run();
        }
        lastCheckValue = System.nanoTime() - start;
        return lastCheckValue;
    }
}
