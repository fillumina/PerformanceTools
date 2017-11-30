package com.fillumina.performance.accuracy.speed;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.executor.stats.StatsProducer;
import com.fillumina.performance.executor.stats.producer.RequiredMarginStrategy;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.time.sample.DefaultPerformanceTimer;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.time.stats.strgen.AverageTimeStatsTableStringGenerator;
import static com.fillumina.performance.util.formatter.PerformanceTimeHelper.*;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.IntervalUnit;
import org.junit.Test;

/**
 * Executes tests that last for a fixed time to assess the accuracy of the
 * framework.
 * Note that on most systems the {@link System#nanoTime() } call has a
 * granularity of about 30 ns and that the run time includes some little
 * time accountable to the framework itself and a jitter due to the
 * {@link System#nanoTime() } call (so the inevitable inaccuracy of results).
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerAccuracyTest {
    private boolean printOut;

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

        if (printOut) {
            System.out.println("");
            System.out.println("==== TEST: " + testName);
            System.out.println("");
        }

        pt.addConsumerIf(printOut, SampleLineStringGenerator.VIEWER);

        StatsProducer<?> producer =
                pt.instrumentedBy(RequiredMarginStrategy.builder()
                        .samples(10)
                        .setCoolDownCpuActive(false)
                        .maxAllowedMargin(Ratio.percentage(15))
                        .setStatsTimeout(IntervalUnit.MINUTES.quantity(2))
                        .buildStatsProducer());

        // avoid dead code eviction
        producer.addTest("zero", () -> sleepMicroseconds(1));
        producer.addTest("single", () -> sleepMicroseconds(100));
        producer.addTest("double", () -> sleepMicroseconds(200));
        producer.addTest("triple", () -> sleepMicroseconds(300));

        final StatsHolder holder =
                producer.execute().getStatsHolder(TimeStatsType.AVERAGE);

        if (printOut) {
            holder.use(AverageTimeStatsTableStringGenerator
                    .appendTo(System.out, Ratio.P_99));
        }

        holder.check(Assertions.
                <Stats>withTolerance(Assertions.SUPER_SAFE_TOLERANCE)
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
