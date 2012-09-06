package com.fillumina.performance.producer.timer;

import com.fillumina.performance.producer.LoopPerformances;
import com.fillumina.performance.PerformanceTimerBuilder;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import com.fillumina.performance.consumer.assertion.AssertPerformance;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static com.fillumina.performance.util.PerformanceTimeHelper.*;

/**
 *
 * @author fra
 */
public class PerformanceTimerAccuracyTest {
    private int iterations = 2_500;
    private boolean printOut = false;

    public static void main(final String[] args) {
        PerformanceTimerAccuracyTest test = new PerformanceTimerAccuracyTest();
        test.printOut = true;
        test.iterations = 10_000;

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
        final  int cpuNumber = Runtime.getRuntime().availableProcessors();

        assertPerformances("MULTI (" + cpuNumber + " threads)",
                PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(cpuNumber)
                .setWorkerNumber(cpuNumber)
                .setTimeout(10, TimeUnit.SECONDS)
                .build());
    }

    private void assertPerformances(final String testName,
            final PerformanceTimer pt) {
        setTests(pt);

        final LoopPerformances performances =
                pt.iterate(iterations).getLoopPerformances();

        printOutPercentages(testName, performances);

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

    private void printOutPercentages(final String message,
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

}
