package com.fillumina.performance.executor;

import com.fillumina.performance.util.ConsumerNotifier;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathName;
import java.util.Map;

/**
 *
 * @param I self used to allow fluent interface to extending classes
 * @param N notification
 * @param T test
 * @param R result
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractTestExecutor
                <I extends AbstractTestExecutor<I,N,T,R>, N, T, R>
        extends ConsumerNotifier<I, N>
        implements TestExecutor<I, N, T, R>, TestContainer<I,T> {
    public static final String UNNAMED_TEST_PREFIX = "test_";
    public static final String SINGLE_TEST_NAME = UNNAMED_TEST_PREFIX + "0";

    private final IndexedHashMap<PathName, T> tests = new IndexedHashMap<>();
    private PathName name = PN.EMPTY;

    @Override
    @SuppressWarnings("unchecked")
    public I setPathName(PathName name) {
        this.name = name;
        return (I) this;
    }

    /** Sets a name for the test. */
    @Override
    @SuppressWarnings("unchecked")
    public I setName(String name) {
        this.name = PN.pname(name);
        return (I) this;
    }

    @Override
    public PathName getPathName() {
        return name;
    }

    @Override
    public String getName() {
        return name.toString();
    }

    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I clearTests() {
        tests.clear();
        return (I) this;
    }

    /** @inheritDoc */
    @Override
    public I addTest(T test) {
        return addTest(UNNAMED_TEST_PREFIX + tests.size() , test);
    }

    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I addTests(Map<PathName,T> tests) {
        this.tests.putAll(tests);
        return (I) this;
    }

    /** @inheritDoc */
    @Override
    public I addTest(String name, T test) {
        return addTest(PN.pname(name), test);
    }

    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I addTest(PathName name, T test) {
        if (tests.containsKey(name)) {
            throw new RuntimeException("test '" + name + "' already inserted");
        }
        tests.put(name, test);
        return (I) this;
    }

    /** @inheritDoc */
    @Override
    public I ignoreTest(String name, T test) {
        return ignoreTest(PN.pname(name), test);
    }

    /** @inheritDoc */
    @Override
    @SuppressWarnings("unchecked")
    public I ignoreTest(final PathName name, final T test) {
        return (I) this;
    }

    /** @inheritDoc */
    @Override
    public IndexedHashMap<PathName, T> getTests() {
        return tests.unmodifiableView();
    }

    protected static PathName createTestName(PathName producerName, PathName testName) {
        if (producerName.isSharingPrefixWith(testName)) {
            return testName;
        }
        return producerName.append(testName);
    }

    protected void assertTestsPresent() {
        if (getTests().isEmpty()) {
            throw new IllegalStateException("no test to execute");
        }
    }
}
