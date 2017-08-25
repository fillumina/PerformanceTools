package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.Named;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.UnmodifiableLinkedMap;
import java.util.Map;

/**
 * Encapsulates the consumers management (add, remove and notify).
 *
 * @param I fluent interface self
 * @param A the assertable test
 * @param T test
 *
 * @author Francesco Illuminati
 */
public abstract class AbstractAssertableProducer
            <I extends AbstractAssertableProducer<I,T>, T>
        extends AbstractAssertableConsumerNotifier<I>
        implements AssertableProducer<T>, Named {

    private final LinkedMap<TName, T> tests = new LinkedMap<>();

    private TName name = TN.EMPTY;

    @Override
    @SuppressWarnings("unchecked")
    public I setName(TName name) {
        this.name = name;
        return (I) this;
    }

    /** Sets a name for the test. */
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = TN.EMPTY.append(name);
        return (I) this;
    }

    protected TName getName() {
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
    public TestContainer<T> addTest(T test) {
        return addTest("test_" + tests.size() , test);
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
