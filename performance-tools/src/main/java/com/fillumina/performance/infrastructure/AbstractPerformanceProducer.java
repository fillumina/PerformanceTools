package com.fillumina.performance.infrastructure;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Encapsulates the consumers management (add, remove and notify).
 *
 * @param I fluent interface self
 * @param S the tree
 * @param A the leaf
 * @param T test
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractPerformanceProducer
            <I extends AbstractPerformanceProducer<I,S,A,T>,S,A,T>
        extends AbstractPerformanceConsumerNotifier<I,A>
        implements PerformanceProducer<S,A,T> {

    private final Map<String, T> tests = new LinkedHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public I clearTests() {
        tests.clear();
        return (I) this;
    }

    /**
     * If you need to perform some initialization use
     * {@link InitializingRunnable}, if you need a thread local object
     * use {@link ThreadLocalRunnable}, if you need to avoid dead code
     * elimination try {@link RunnableSink}.
     *
     * @see InitializingRunnable
     * @see ThreadLocalRunnable
     * @see RunnableSink
     */
    @Override
    @SuppressWarnings("unchecked")
    public I addTest(String name, T test) {
        tests.put(name, test);
        return (I) this;
    }

    /**
     * Ignore a test without having to comment out multiple
     * lines of code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I ignoreTest(final String name, final T test) {
        return (I) this;
    }

    @Override
    public I performGarbageCollection(int millis) {
        if (millis > 0) {
            System.gc();
            try {
                // sometimes gc are postponed by the JVM, this is a little
                // 'suggestion' that there could be time for it.
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (I) this;
    }

    protected Map<String, T> getTests() {
        return tests;
    }

    protected void assertTestsPresent() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test to execute");
        }
    }

    protected int[] createIterationArray(final int iterations) {
        int[] iterationArray = new int[getTests().size()];
        Arrays.fill(iterationArray, iterations == 0 ? 1 : iterations);
        return iterationArray;
    }

}
