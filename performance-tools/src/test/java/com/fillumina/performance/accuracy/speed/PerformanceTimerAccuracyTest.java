package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.infrastructure.AssertableHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.sample.strgen.SpeedSampleLineStringGenerator;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.time.stats.progression.ConfigurableStatsProducer;
import com.fillumina.performance.time.stats.progression.RepeatingStatsProducerBuilder;
import com.fillumina.performance.time.stats.strgen.AverageTimeStatsTableStringGenerator;
import static com.fillumina.performance.util.formatter.PerformanceTimeHelper.*;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Executes tests that last for a fixed time to assess the accuracy of the
 * framework.
 * Note that on most systems the {@link System#nanoTime() } call has a
 granularity of about 30 ns and that the run time includes some little
 time accountable to the framework itself and a jitter due to the
 {@link System#nanoTime() } call (so the inevitable inaccuracy of results).
 *
 * @author Francesco Illuminati
 */
@Ignore // TODO adjust using builder
public class PerformanceTimerAccuracyTest {
    private Appendable printOut;

    public static void main(final String[] args) {
        PerformanceTimerAccuracyTest test = new PerformanceTimerAccuracyTest();
        test.printOut = System.out;

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
                .buildMultiThreadPerformanceTimer());
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final int concurrency = getConcurrencyLevel();

        assertPerformances("MULTI (" + concurrency + " threads)",
                PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(concurrency)
                .setWorkers(concurrency)
                .buildMultiThreadPerformanceTimer());
    }

    private void assertPerformances(final String testName,
            final DefaultPerformanceTimer pt) {
        printOutIterationsPercentages(pt);

        ConfigurableStatsProducer autoProgression =
                pt.instrumentedBy(RepeatingStatsProducerBuilder.instance()
                        .setMaxPercentageMargin(Ratio.percentage(15))
                        .setApproximateSampleMillis(250)
                        .build());

        addTestsTo(autoProgression);

        final AssertableHolder<AverageTimeStats> stats = autoProgression.execute()
                .getStats(AverageTimeStats.class);

        printOutResultPercentages(testName, stats);

        assertPerformances(stats);
    }

    private void addTestsTo(final TestContainer<Runnable> pt) {
        pt.addTest("zero", new Runnable() {
            @Override
            public void run() {
                // so to not be eviced as dead code
                sleepMicroseconds(1);
            }
        });

        pt.addTest("single", new Runnable() {

            @Override
            public void run() {
                sleepMicroseconds(100);
            }
        });

        pt.addTest("double", new Runnable() {

            @Override
            public void run() {
                sleepMicroseconds(200);
            }
        });

        pt.addTest("triple", new Runnable() {

            @Override
            public void run() {
                sleepMicroseconds(300);
            }
        });
    }

    public void printOutIterationsPercentages(final DefaultPerformanceTimer pt) {
        pt.addConsumer(SpeedSampleLineStringGenerator.appendTo(printOut));
    }

    private void printOutResultPercentages(final String message,
            final AssertableHolder<AverageTimeStats> stats) {
        stats.use(AverageTimeStatsTableStringGenerator
                .appendTo(printOut, Ratio.P_99));
    }

    private void assertPerformances(
            final AssertableHolder<AverageTimeStats> stats) {
        stats.check(AssertStats.<TimeStats>withTolerance(
                        AssertStats.SUPER_SAFE_TOLERANCE)
                .assertPercentage("zero").sameAs(0)
                .assertPercentage("single").sameAs(33)
                .assertPercentage("double").sameAs(66)
                .assertPercentage("triple").sameAs(100));
    }

    private static int getConcurrencyLevel() {
        int concurrency = Runtime.getRuntime().availableProcessors();
        if (concurrency > 3) {
            concurrency -= 2;
        }
        return concurrency;
    }
}
