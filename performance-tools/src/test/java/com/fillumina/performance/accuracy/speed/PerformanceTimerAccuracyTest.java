package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.assertion.AssertStats;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import static com.fillumina.performance.util.formatter.PerformanceTimeHelper.*;
import com.fillumina.performance.util.stats.Ratio;
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
                .build());
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final int concurrency = getConcurrencyLevel();

        assertPerformances("MULTI (" + concurrency + " threads)",
                PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(concurrency)
                .setWorkers(concurrency)
                .build());
    }

    private void assertPerformances(final String testName,
            final DefaultPerformanceTimer pt) {
        printOutIterationsPercentages(pt);

        AutoProgressionPerformanceInstrumenter autoProgression =
                pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                        .setName(testName)
                        .setConfidence(Ratio.P_999)
                        .setMaxPercentageMargin(15)
                        .setApproximateSampleMillis(250)
                        .setPerformanceStatsConsumer(
                                SpeedStatsTableStringGenerator.appendTo(printOut))
                        .build());

        addTestsTo(autoProgression);

        final PHolder<SpeedStats> stats = autoProgression.execute();

        printOutResultPercentages(testName, stats);

        assertPerformances(stats);
    }

    private void addTestsTo(final TestContainer<Testable> pt) {
        pt.addTest("zero", new Testable() {
            @Override
            public void test() {
                // so to not be eviced as dead code
                sleepMicroseconds(1);
            }
        });

        pt.addTest("single", new Testable() {

            @Override
            public void test() {
                sleepMicroseconds(100);
            }
        });

        pt.addTest("double", new Testable() {

            @Override
            public void test() {
                sleepMicroseconds(200);
            }
        });

        pt.addTest("triple", new Testable() {

            @Override
            public void test() {
                sleepMicroseconds(300);
            }
        });
    }

    public void printOutIterationsPercentages(final DefaultPerformanceTimer pt) {
        pt.addPerformanceConsumer(SampleLineStringGenerator.appendTo(printOut));
    }

    private void printOutResultPercentages(final String message,
            final PHolder<SpeedStats> stats) {
        SpeedStatsTableStringGenerator.appendTo(printOut).consume(stats);
    }

    private void assertPerformances(
            final PHolder<SpeedStats> stats) {
        AssertStats
                .<SpeedStats>withTolerance(AssertStats.SUPER_SAFE_TOLERANCE)

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
