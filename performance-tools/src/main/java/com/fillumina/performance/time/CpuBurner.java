package com.fillumina.performance.time;

import com.fillumina.performance.infrastructure.SafeSink;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.iterator.MultiThreadPerformanceExecutor;
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

    private static final Runnable BURNER = new Runnable() {
        private final Random rnd = new HighQualityRandom();
        @Override
        public void run() {
            SafeSink.drain(rnd.nextInt());
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
        iterations = pt.estimateIterations(250);
    }

    /**
     * Heat up all the CPU's cores.
     *
     * @param seconds how long it should burn (approximated).
     */
    public void burnSeconds(int seconds) {
        long ms = System.currentTimeMillis();
        do {
            pt.iterate(iterations);
        } while (System.currentTimeMillis() - ms < seconds * 1000);
    }
}
