package com.fillumina.performance.speed;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.executor.MultiThreadPerformanceExecutor;
import com.fillumina.performance.util.Sleeper;
import com.fillumina.performance.util.rnd.HighQualityRandom;
import java.util.Random;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CpuBurner {
    private final DefaultPerformanceTimer pt;
    private final int[] iterations;

    private static final Testable BURNER = new Testable() {
        private final Random rnd = new HighQualityRandom();
        @Override
        public void test() {
            drain(rnd.nextInt());
        }

    };

    public static final CpuBurner INSTANCE = new CpuBurner();

    public CpuBurner() {
        pt = new DefaultPerformanceTimer(
                MultiThreadPerformanceExecutor.builder()
                    .setUnlimitedThreads()
                    .buildMultiThreadPerformanceExecutor());
        pt.addTest("burner", BURNER);
        Sleeper.sleepSeconds(5);
        pt.warmup(500_000); // about 25 ms
        iterations = pt.iterationTimeEstimator(250);
        iterations[0] *= 5; // normalize to about 1 second
    }

    /**
     * Heat up all the CPU's cores.
     *
     * @param seconds how long it should burn (approximated).
     */
    public void burnSeconds(int seconds) {
        for (int i=0; i<seconds; i++) {
            pt.execute(iterations);
        }
    }
}
