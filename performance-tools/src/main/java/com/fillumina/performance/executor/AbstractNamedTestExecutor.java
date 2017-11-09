package com.fillumina.performance.executor;

import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.UnmodifiableLinkedMap;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @param I self
 * @param M message for consumers
 * @param T test type
 * @param P produced aggregate by type
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractNamedTestExecutor
                <I extends NamedTestExecutor<I,M,T,P>, M, T, P>
        implements NamedTestExecutor<I, M, T, P> {
    public static final String UNNAMED_TEST_PREFIX = "test_";
    public static final String SINGLE_TEST_NAME = "test_0";

    private final List<Consumer<? super M>> consumers = new ArrayList<>();
    private final LinkedMap<TName, T> tests = new LinkedMap<>();
    private TName name = TN.EMPTY;

    @Override
    @SuppressWarnings("unchecked")
    public I addConsumerIf(boolean condition, Consumer<? super M> consumer) {
        if (condition) {
            addConsumer(consumer);
        }
        return (I) this;
    }

    /**
     * {@link AssertableConsumer}s added here will be notified any time a
     * statistics is elaborated even if it is not the final one
     * (which will be finally reported).
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I addConsumer(Consumer<? super M> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
        return (I) this;
    }

    /**
     * A {@code null} argument and {@code null} array's elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I removeConsumer(final Consumer<? super M> consumer) {
        if (consumer != null) {
            consumers.remove(consumer);
        }
        return (I) this;
    }

    /**
     * Passes the {@link PerformanceSample} to all
     * {@link PerformanceSampleConsumer}s
     * in the same order they were added.
     */
    @SuppressWarnings("unchecked")
    protected void dispatchToConsumers(M message) {
        if (message != null) {
            for (Consumer<? super M> c: consumers) {
                c.accept(message);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I clearConsumers() {
        consumers.clear();
        return (I) this;
    }

    // TODO convert all to CharSequence instead of String/TName...

    @Override
    @SuppressWarnings("unchecked")
    public I setName(TName name) {
        this.name = name;
        return (I) this;
    }

    /** Sets a name for the test. */
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = TN.tname(name);
        return (I) this;
    }

    @Override
    public TName getName() {
        return name;
    }

    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I clearTests() {
        tests.clear();
        return (I) this;
    }

    @Override
    public I addTest(T test) {
        return addTest(UNNAMED_TEST_PREFIX + tests.size() , test);
    }

    @Override
    @SuppressWarnings("unchecked")
    public I addTests(Map<TName,T> tests) {
        this.tests.putAll(tests);
        return (I) this;
    }

    @Override
    public I addTest(String name, T test) {
        return addTest(TN.tname(name), test);
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
        return ignoreTest(TN.tname(name), test);
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

    @Override
    public LinkedMap<TName, T> getTests() {
        return UnmodifiableLinkedMap.copy(tests);
    }

    protected void assertTestsPresent() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test to execute");
        }
    }
}
