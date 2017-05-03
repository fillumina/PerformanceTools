package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Encapsulates the consumers management (add, remove and notify).
 *
 * @param I fluent interface self
 * @param A the assertable test
 * @param T test
 *
 * @author Francesco Illuminati
 */
// TODO add Instrumenter implementation too
public abstract class AbstractPerformanceProducer
            <I extends AbstractPerformanceProducer<I,A,T>,
             A extends Assertable,
             T>
        extends AbstractPerformanceConsumerNotifier<I,A>
        implements PerformanceProducer<A,T> {

    private final LinkedHashMap<TName, T> tests = new LinkedHashMap<>();

    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I clearTests() {
        tests.clear();
        return (I) this;
    }

    @Override
    public I addTest(String name, T test) {
        return addTest(TN.n(name), test);
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
    public I addTest(TName name, T test) {
        if (tests.containsKey(name)) {
            throw new RuntimeException("test '" + name + "' already inserted");
        }
        tests.put(name, test);
        return (I) this;
    }

    @Override
    public I ignoreTest(String name, T test) {
        return ignoreTest(TN.n(name), test);
    }

    /**
     * Ignore a test without having to comment out multiple
     * lines of code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I ignoreTest(final TName name, final T test) {
        return (I) this;
    }

    //TODO should this be really here?
    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I performGarbageCollection(int millis) {
        if (millis > 0) {
            System.gc();
            try {
                // sometimes GC are postponed by the JVM, this is a little
                // 'suggestion' that there could be time for it.
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return (I) this;
    }

    protected LinkedHashMap<TName, T> getTests() {
        return tests;
    }

    protected void assertTestsPresent() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test to execute");
        }
    }

    protected int[] createIterationsArray(final int iterations) {
        int[] iterationArray = new int[getTests().size()];
        Arrays.fill(iterationArray, iterations == 0 ? 1 : iterations);
        return iterationArray;
    }

}
