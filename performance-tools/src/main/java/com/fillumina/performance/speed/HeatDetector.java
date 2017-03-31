package com.fillumina.performance.speed;

import com.fillumina.performance.infrastructure.LfsrTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HeatDetector {
    private final DefaultPerformanceTimer pt;
    private final int[] iterations;
    private final double expectedSpeed;

    public static final HeatDetector INSTANCE = new HeatDetector();

    public HeatDetector() {
        pt = new DefaultPerformanceTimer(new SingleThreadPerformanceExecutor());
        pt.addTest("lfsr", new LfsrTestable());
        sleepSeconds(10);
        pt.warmup(500_000); // about 25 ms
        iterations = pt.iterationTimeEstimator(20);
        expectedSpeed = checkSpeed();
    }

    public void init() {
        // just make sure it is initialized
    }

    public void coolDownCpu() {
        if (isHeated()) {
            sleepSeconds(15);
        }
    }

    public boolean isHeated() {
        double speed = checkSpeed();
        return speed > expectedSpeed * 1.5;
    }

    private void sleepSeconds(final int seconds) {
        try {
            Thread.sleep(seconds * 1_000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private double checkSpeed() {
        return pt.execute(iterations).getValue("lfsr").getMean();
    }
}
