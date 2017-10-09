package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.annotation.AnnotatedRunnableSetter;
import com.fillumina.performance.util.tname.TName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati
 */
public class WarmupTimedIterator {
    private static final long MILLIS = 1_000_000L;

    public static final WarmupTimedIterator INSTANCE =
            new WarmupTimedIterator();

    /**
     * Warmup the tests for a specific amount of time trying to equalize
     * their iterations. The problem is there are two similar test the first
     * to be executed will be optimized and will have an advantage over the
     * second. Here we try to execute the same number of iterations on all
     * tests (they should be similar in speed) to avoid that. This method
     * should be called several times to be effective (5 is the minimum
     * recommended).
     *
     * @param tests
     * @param millis how long (approximatively) each test must iterate
     * @return total number of iterations performed by each test
     */
    public long executeTests(Map<TName, Runnable> tests, long millis) {
        sleep(2000);
        long counter = 0;
        counter += warmUp(tests, millis);
        return counter;
    }

    private static void sleep(long millis) throws RuntimeException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private long warmUp(Map<TName, Runnable> tests, long millis) {
        for (Runnable r : tests.values()) {
            AnnotatedRunnableSetter.INSTANCE.setUp(r);
        }

        long end = System.nanoTime() + (millis * MILLIS);
        long counter = 0;
        int iterations = 1;
        long less = 0;
        do {
            long max = Integer.MIN_VALUE;
            for (Map.Entry<TName,Runnable> e : tests.entrySet()) {
                long ns = iterate(e.getValue(), iterations);
                if (ns > max) {
                    max = ns;
                }
            }
            counter += iterations;
            less = end - System.nanoTime();
            iterations = (int) Math.round((iterations * 1.0 / max) * less);
        } while (less > 0);

        for (Runnable r : tests.values()) {
            AnnotatedRunnableSetter.INSTANCE.tearDown(r);
        }

        return counter;
    }

    private static long iterate(Runnable testable, int iterations) {
        final AnnotatedRunnableSetter setter = AnnotatedRunnableSetter.INSTANCE;

        setter.onBeforeSample(testable, iterations);

        long ns = RunnableIterator.DISPATCHER.getIterator(testable)
                .measureIterationTimeNs(iterations);
        if (ns == -1) {
            throw new AssertionError("cannot happen");
        }

        setter.onAfterSample(testable, iterations);

        return ns;
    }
}
