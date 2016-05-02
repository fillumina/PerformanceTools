package com.fillumina.performance.progression;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunToRunEstimationTest {

    public static void main(final String[] args) {
        new RunToRunEstimationTest().run();
    }

    public void run() {
            PerformanceTimerFactory.createSingleThreaded()

            .addTest("2", new AbstractTestable() {

                @Override
                public Object test() {
                    randomSleep(2);
                    return null;
                }
            })
            .addTest("5", new AbstractTestable() {

                @Override
                public Object test() {
                    randomSleep(5);
                    return null;
                }
            })
            .addTest("10", new AbstractTestable() {

                @Override
                public Object test() {
                    randomSleep(10);
                    return null;
                }
            })

//            .addPerformanceSampleConsumer(StringTableStatsViewer.INSTANCE)
            .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setPerformanceStatsConsumer(StringTableStatsViewer.INSTANCE)
                    .build())
                .addPerformanceConsumer(StringTableStatsViewer.INSTANCE)
                .execute()
                .print();
    }

    private void randomSleep(int decs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10) * decs);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
