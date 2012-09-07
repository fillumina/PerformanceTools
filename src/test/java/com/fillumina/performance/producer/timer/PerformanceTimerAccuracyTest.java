package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import com.fillumina.performance.consumer.viewer.StringCsvViewer;
import com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.producer.progression.StandardDeviationConsumer;
import com.fillumina.performance.util.ConcurrencyHelper;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;

/**
 *
 * @author fra
 */
public class PerformanceTimerAccuracyTest {
    private int iterations = 1_000;
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
                PerformanceTimerBuilder.createSingleThread());
    }

    @Test
    public void shouldMultiThreadingBeAccurateUsingOnlyOneThread() {
        assertPerformances("MULTI (single thread)",
                PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(1)
                .setWorkerNumber(1)
                .setTimeout(10, TimeUnit.SECONDS)
                .build());
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final int concurrency = ConcurrencyHelper.getConcurrencyLevel();

        assertPerformances("MULTI (" + concurrency + " threads)",
                PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(concurrency)
                .setWorkerNumber(concurrency)
                .setTimeout(30, TimeUnit.SECONDS)
                .build());
    }

    private void assertPerformances(final String testName,
            final PerformanceTimer pt) {
        setTests(pt);

        printOutIterationsPercentages(pt);

        final int samples = 10;
        final LoopPerformances performances =
//            pt.iterate(iterations).getLoopPerformances();

            pt.instrumentedBy(AutoProgressionPerformanceInstrumenter.builder())
                .setBaseIterations(iterations / samples)
                .setSamplesPerMagnitude(samples)
                .setMaxStandardDeviation(7)
                .setTimeout(5, TimeUnit.MINUTES)
                .build()
                .addStandardDeviationConsumer(new StandardDeviationConsumerImpl())
                .execute().getLoopPerformances();

        printOutResultPercentages(testName, performances);

        assertPerformances(performances);
    }

    private void setTests(final PerformanceTimer pt) {
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

                .assertPercentageFor("null").equals(0)
                .assertPercentageFor("single").equals(33)
                .assertPercentageFor("double").equals(66)
                .assertPercentageFor("triple").equals(100)

                .check(loopPerformances);
    }

    private class StandardDeviationConsumerImpl
            implements StandardDeviationConsumer {

        @Override
        public void consume(final double stdDev) {
            if (printOut) {
                System.out.println("Standard Deviation: " + stdDev);
            }
        }
    }
}
