package com.fillumina.performance;

import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * @author fra
 */
public class PerformanceTimerAccuracyTest {
    private static final int LOOPS = 10_000;

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
        final PerformanceTimer pt = PerformanceTimerBuilder.createSigleThread();

        setTests(pt);

        pt.execute(LOOPS);

        printOutPercentages("SINGLE", pt);

        assertPerformance(pt);
    }

    @Test
    public void shouldMultiThreadingBeAccurateUsingOnlyOneThread() {
        final PerformanceTimer pt = PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(1)
                .setTaskNumber(1)
                .setTimeout(10, TimeUnit.SECONDS)
                .build();

        setTests(pt);

        pt.execute(LOOPS);

        printOutPercentages("MULTI (single thread)", pt);

        assertPerformance(pt);
    }

    @Test
    public void shouldMultiThreadingBeAccurate() {
        final  int cpus = Runtime.getRuntime().availableProcessors();

        final PerformanceTimer pt = PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(cpus)
                .setTaskNumber(cpus)
                .setTimeout(10, TimeUnit.SECONDS)
                .build();

        setTests(pt);

        pt.execute(LOOPS);

        printOutPercentages("MULTI (" + cpus + " threads)", pt);

        assertPerformance(pt);

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
                sleepMicros(100);
            }
        });

        pt.addTest("double", new Runnable() {

            @Override
            public void run() {
                sleepMicros(200);
            }
        });

        pt.addTest("triple", new Runnable() {

            @Override
            public void run() {
                sleepMicros(300);
            }
        });
    }

    private void printOutPercentages(final String message,
            final PerformanceTimer pt) {
        if (printOut) {
            new Presenter(pt)
                .addMessage(message)
                .getComparisonString(TimeUnit.MICROSECONDS)
                .println();
        }
    }

    private void assertPerformance(final PerformanceTimer pt) {
        new AssertPerformance(pt)
            .setTolerancePercentage(7) // super safe

            .assertPercentage("single", 33)
            .assertPercentage("double", 66)
            .assertPercentage("triple", 100);

        // these tests cannot be safely executed on all systems
//            .assertEquals("triple", 3, TimeUnit.SECONDS)
//            .assertLessThan("null", 20, TimeUnit.MILLISECONDS);
    }

    /**
     * It should be more accurate than {@code Thread.sleep()}
     * because it doesn't involve SO thread management.
     */
    private static void sleepMicros(final int nano) {
        final long start = System.nanoTime();
        while(System.nanoTime() - start < nano * 1E3);
    }

}
