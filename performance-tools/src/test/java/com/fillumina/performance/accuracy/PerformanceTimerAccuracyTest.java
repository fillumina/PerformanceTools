package com.fillumina.performance.accuracy;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.assertion.AssertPerformance;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedTableStringGenerator;
import com.fillumina.performance.util.ComposedName;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Executes tests that last for a fixed time to assess the accuracy of the
 * framework.
 * Note that on most systems the {@link System#nanoTime() } call has a
 * granularity of about 30 ns and that the test time includes some little
 * time accountable to the framework itself and a jitter due to the
 * {@link System#nanoTime() } call (so the inevitable inaccuracy of results).
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
        printOutIterationsPercentages(pt);

        AutoProgressionPerformanceInstrumenter autoProgression =
                pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setName(testName)
                        .setTimeout(500, TimeUnit.SECONDS)
                        .setConfidence(0.999)
                        .setMaxPercentageMargin(15)
                        .setApproximateSampleMillis(250)
                        .setPerformanceStatsConsumerIf(printOut,
                            SpeedTableStringGenerator.VIEWER)
                        .build());

        addTestsTo(autoProgression);

        final SpeedStats stats = autoProgression
                .execute()
                .getPerformance();

        printOutResultPercentages(testName, stats);

        assertPerformances(stats);
    }

    private void addTestsTo(final TestContainer<Testable> pt) {
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
            pt.addPerformanceConsumer(SampleLineStringGenerator.VIEWER);
        }
    }

    private void printOutResultPercentages(final String message,
            final SpeedStats stats) {
        if (printOut) {
            SpeedTableStringGenerator.VIEWER.consume(
                    ComposedName.create(message), stats);
        }
    }

    private void assertPerformances(final SpeedStats stats) {
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
