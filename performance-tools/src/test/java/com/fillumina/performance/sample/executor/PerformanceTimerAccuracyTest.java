package com.fillumina.performance.sample.executor;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.sample.AbstractTestable;
import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.viewer.StringCsvSampleViewer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerAccuracyTest {
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
                .setTimeout(60, TimeUnit.SECONDS)
                .build());
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final int concurrency = getConcurrencyLevel();

        assertPerformances("MULTI (" + concurrency + " threads)",
                PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(concurrency)
                .setWorkers(concurrency)
                .setTimeout(60, TimeUnit.SECONDS)
                .build());
    }

    private void assertPerformances(final String testName,
            final DefaultPerformanceTimer pt) {
        addTestsTo(pt);

        printOutIterationsPercentages(pt);

        final PerformanceStats stats = pt.instrumentedBy(
                    AutoProgressionPerformanceInstrumenter.builder()
                        .setName(testName)
                        .setTimeout(120, TimeUnit.SECONDS)
                        .setPerformanceStatsConsumerIf(printOut,
                            StringTableStatsViewer.INSTANCE)
                        .build())
                .execute()
                .getPerformanceStats();

        printOutResultPercentages(testName, stats);

        assertPerformances(stats);
    }

    private void addTestsTo(final DefaultPerformanceTimer pt) {
        pt.addTest("zero", new AbstractTestable() {

            @Override
            public Object test() {
                // so to not be eviced as dead code
                sleepMicroseconds(1);
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
            pt.addPerformanceSampleConsumer(StringCsvSampleViewer.INSTANCE);
        }
    }

    private void printOutResultPercentages(final String message,
            final PerformanceStats stats) {
        if (printOut) {
            StringTableStatsViewer.INSTANCE.consume(message, stats);
        }
    }

    private void assertPerformances(final PerformanceStats stats) {
        AssertPerformance
                .withTolerance(AssertPerformance.SUPER_SAFE_TOLERANCE)

                .assertPercentage("zero").sameAs(0)
                .assertPercentage("single").sameAs(33)
                .assertPercentage("double").sameAs(66)
                .assertPercentage("triple").sameAs(100)

                .check(stats);
    }

    private static int getConcurrencyLevel() {
        int concurrency = Runtime.getRuntime().availableProcessors();
        if (concurrency > 3) {
            concurrency -= 2;
        }
        return concurrency;
    }
}
