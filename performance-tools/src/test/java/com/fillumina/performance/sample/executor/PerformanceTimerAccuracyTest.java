package com.fillumina.performance.sample.executor;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.progression.StandardDeviationConsumer;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.viewer.StringTableSampleViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableViewer;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerAccuracyTest {
    private static final int ITERATIONS = 1_000;
    private static final int SAMPLES = 10;

    private boolean printOut = false;

    public static void main(final String[] args) {
        PerformanceTimerAccuracyTest test = new PerformanceTimerAccuracyTest();
        test.printOut = true;

        test.shouldSingleThreadBeAccurate();
        test.shouldMultiThreadingBeAccurateUsingOnlyOneThread();
        test.shouldMultiThreadingBeAccurate();
    }

    @Test
    public void shouldSingleThreadBeAccurate() {
        assertPerformances("SINGLE",
                PerformanceTimerFactory.createSingleThreaded());
    }

    @Test
    public void shouldMultiThreadingBeAccurateUsingOnlyOneThread() {
        assertPerformances("MULTI (single thread)",
                PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(1)
                .setWorkers(1)
                .setTimeout(30, TimeUnit.SECONDS)
                .build());
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final int concurrency = getConcurrencyLevel();

        assertPerformances("MULTI (" + concurrency + " threads)",
                PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(concurrency)
                .setWorkers(concurrency)
                .setTimeout(30, TimeUnit.SECONDS)
                .build());
    }

    private void assertPerformances(final String testName,
            final DefaultPerformanceTimer pt) {
        addTestsTo(pt);

        printOutIterationsPercentages(pt);

        final PerformanceStats stats = pt.instrumentedBy(
                    AutoProgressionPerformanceInstrumenter.builder()
                        .setBaseIterations(ITERATIONS / SAMPLES)
                        .setBaseSamples(SAMPLES)
                        .setMaxStandardDeviation(7)
                        .setTimeout(2, TimeUnit.MINUTES)
                        .build())
                .addStandardErrorConsumer(new StandardDeviationConsumerPrinter())
                .execute()
                .getPerformanceStats();

        printOutResultPercentages(testName, stats);

        assertPerformances(stats);
    }

    private void addTestsTo(final DefaultPerformanceTimer pt) {
        pt.addTest("null", new AbstractTestable() {

            @Override
            public Object test() {
                return null;
            }
        });

        pt.addTest("single", new AbstractTestable() {

            @Override
            public Object test() {
                sleepMicroseconds(100);
                return null;
            }
        });

        pt.addTest("double", new AbstractTestable() {

            @Override
            public Object test() {
                sleepMicroseconds(200);
                return null;
            }
        });

        pt.addTest("triple", new AbstractTestable() {

            @Override
            public Object test() {
                sleepMicroseconds(300);
                return null;
            }
        });
    }

    public void printOutIterationsPercentages(final DefaultPerformanceTimer pt) {
        if (printOut) {
            pt.addPerformanceSampleConsumer(StringTableSampleViewer.INSTANCE);
        }
    }

    private void printOutResultPercentages(final String message,
            final PerformanceStats stats) {
        if (printOut) {
            StringTableViewer.INSTANCE.getTable(message, stats)
                .print();
        }
    }

    private void assertPerformances(final PerformanceStats stats) {
        AssertPerformance
                .withTolerance(AssertPerformance.SUPER_SAFE_TOLERANCE)

                .assertPercentageFor("null").sameAs(0)
                .assertPercentageFor("single").sameAs(33)
                .assertPercentageFor("double").sameAs(66)
                .assertPercentageFor("triple").sameAs(100)

                .check(stats);
    }

    private class StandardDeviationConsumerPrinter
            implements StandardDeviationConsumer {

        @Override
        public void consume(final long iterations,
                final long samples, final double stdDev) {
            if (printOut) {
                System.out.println(new StringBuilder()
                        .append("Iterations: ").append(iterations)
                        .append("\tSamples: ").append(samples)
                        .append("\tStandard Deviation: ").append(stdDev)
                        .toString());
            }
        }
    }

    private static int getConcurrencyLevel() {
        int concurrency = Runtime.getRuntime().availableProcessors();
        if (concurrency > 3) {
            concurrency -= 2;
        }
        return concurrency;
    }
}
