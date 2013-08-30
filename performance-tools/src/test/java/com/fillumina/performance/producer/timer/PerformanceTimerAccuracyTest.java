package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
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
                PerformanceTimerBuilder.createSingleThreaded());
    }

    @Test
    public void shouldMultiThreadingBeAccurateUsingOnlyOneThread() {
        assertPerformances("MULTI (single thread)",
                PerformanceTimerBuilder.getMultiThreadedBuilder()
                .setThreads(1)
                .setWorkers(1)
                .setTimeout(30, TimeUnit.SECONDS)
                .buildPerformanceTimer());
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final int concurrency = getConcurrencyLevel();

        assertPerformances("MULTI (" + concurrency + " threads)",
                PerformanceTimerBuilder.getMultiThreadedBuilder()
                .setThreads(concurrency)
                .setWorkers(concurrency)
                .setTimeout(30, TimeUnit.SECONDS)
                .buildPerformanceTimer());
    }

    private void assertPerformances(final String testName,
            final PerformanceTimer pt) {
        addTestsTo(pt);

        printOutIterationsPercentages(pt);

        final LoopPerformances performances =
//            pt.iterate(iterations).getLoopPerformances();

            pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                    .setBaseIterations(ITERATIONS / SAMPLES)
                    .setSamplesPerMagnitude(SAMPLES)
                    .setMaxStandardDeviation(7)
                    .setTimeout(2, TimeUnit.MINUTES)
                    .build())
                .addStandardDeviationConsumer(new StandardDeviationConsumerPrinter())
                .execute().getLoopPerformances();

        printOutResultPercentages(testName, performances);

        assertPerformances(performances);
    }

    private void addTestsTo(final PerformanceTimer pt) {
        pt.addTest("null", new Runnable() {

            @Override
            public void run() {
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

    public void printOutIterationsPercentages(final PerformanceTimer pt) {
        if (printOut) {
            pt.addPerformanceConsumer(StringCsvViewer.CONSUMER);
        }
    }

    private void printOutResultPercentages(final String message,
            final LoopPerformances loopPerformances) {
        if (printOut) {
            new StringTableViewer(message, loopPerformances)
                .getTable()
                .println();
        }
    }

    private void assertPerformances(final LoopPerformances loopPerformances) {
        AssertPerformance
                .withTolerance(AssertPerformance.SUPER_SAFE_TOLERANCE)

                .assertPercentageFor("null").sameAs(0)
                .assertPercentageFor("single").sameAs(33)
                .assertPercentageFor("double").sameAs(66)
                .assertPercentageFor("triple").sameAs(100)

                .check(loopPerformances);
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
