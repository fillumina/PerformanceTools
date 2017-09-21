package com.fillumina.performance.time;

import com.fillumina.performance.util.CpuBurner;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.executor.test.RndRunnable;
import com.fillumina.performance.time.sample.PerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.stats.progression.RepeatingStatsProducerBuilder;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HeatDetectorTest {

    private static final HeatDetector heatDetector = new HeatDetector();

    public static void main(final String[] args) {
//        checkProgression();
//        checkSpeed();

//        burningTest();
        heatProfile();
    }

    private static void heatProfile() {

        System.out.println("BEFORE");
        for (int i=0; i<10; i++) {
            System.out.println("speed= " + heatDetector.checkSpeed(0));
        }

        PerformanceTimer pt = PerformanceTimerFactory.getMultiThreadedBuilder()
                .setConcurrencyLevel(Runtime.getRuntime().availableProcessors() * 2)
                .build()
                .addTest("lfsr", new RndRunnable());

        for (int i=0; i<10; i++) {
            pt.estimateIterations(4_000);
        }
        int[] iterations = pt.estimateIterations(8_000);

        System.out.println("PROFILING...");
        long ms = System.currentTimeMillis();
        double elapsed = -1;
        do {
            System.out.println("elapsed=" + elapsed +
                    "\tspeed= " + heatDetector.checkSpeed(0));
            pt.iterate(iterations);
            elapsed = Math.ceil((System.currentTimeMillis() - ms) / 1000);
        } while (elapsed < 300);

        System.out.println("AFTER");
        for (int i=0; i<40; i++) {
            System.out.println("speed= " + heatDetector.checkSpeed(0));
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void burningTest() {
        System.out.println("burning...");
        CpuBurner.burnMillis(45_000);

        System.out.println("checking...");
        System.out.println("HEATED = " + heatDetector.isHeated());

        System.out.println("expected=" + heatDetector.getExpectedTimeNs());
        System.out.println("time    =" + heatDetector.getLastCheckTimeNs());

        System.out.println("cooling down...");
        heatDetector.coolDownCpu();

        System.out.println("cooled.");
        System.out.println("expected=" + heatDetector.getExpectedTimeNs());
        System.out.println("time    =" + heatDetector.getLastCheckTimeNs());
    }

    public static void checkProgression() {
        for (int i=0; i<10; i++) {
            System.out.println("speed= " + heatDetector.checkSpeed(0));
        }
        System.out.println("sleeping...");
        try {
            Thread.sleep(5 * 1_000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        for (int i=0; i<10; i++) {
            System.out.println("speed= " + heatDetector.checkSpeed(0));
        }
    }

    public static void checkSpeed() {
        PerformanceTimerFactory.createSingleThreaded()
                .instrumentedBy(RepeatingStatsProducerBuilder.instance()
                        .setMaxPercentageMargin(Ratio.percentage(10))
                        .build())
                .addTest("test", new LfsrRunnable())
                .execute()
                .print();
    }

}
