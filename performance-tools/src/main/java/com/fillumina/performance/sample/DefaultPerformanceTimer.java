package com.fillumina.performance.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.sample.executor.PerformanceExecutor;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.Map;

/**
 * This is the base class for all performance tests. It delegates
 * the test execution to a given {@link PerformanceExecutor} and can be
 instrumented to iterationTimeEstimator tests in a specific way (i.e. repeat the test
 until a target result stability is reached).

 <p>
 * <b>WARNING:</b>
 * Performance tests are subject to many factors that may
 * hinder accuracy:
 * <ul>
 * <li>System load;
 * <li>CPUs heat level;
 * <li>JDK version and brand;
 * <li>JVM Garbage Collector
 * </ul>
 * The only way to marginalize these factors is to run the test long enough
 * so that those disturbances fade away.
 * A performance test might fail randomly: try to increase the iteration number,
 * relax the tolerance and close demanding background processes.
 *
 * @author Francesco Illuminati
 */
public class DefaultPerformanceTimer
        extends AbstractPerformanceProducer
            <DefaultPerformanceTimer,PerformanceSample,Testable>
        implements PerformanceTimer {
    private final PerformanceExecutor executor;
    private boolean testInitialized;

    /**
     * Executes the tests using the specified executor.
     */
    public DefaultPerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
    }

    @Override
    public PerformanceHolder<PerformanceSample> execute() {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Executes the performance test.
     * Instead of specifying the number of iterations
     * (with {@link #setIterations(long) }) and than {@link #execute()}
     * you may use the shorter (and recommended) {@link #iterate(int) }.
     * <p>
     * <b>Hint:</b>It may be convenient to run a small amount of iterations
     * before the actual test
     * to warm up the JVM and let it do the necessary optimizations
     * up front (see {@link DefaultPerformanceTimer#warmup(int) }). At any
     * rate if you use
     * {@link com.fillumina.performance.producer.progression.AutoProgressionPerformanceInstrumenter}
     * the value will be found automatically.
     *
     * @see DefaultPerformanceTimer#iterate(int)
     * @see DefaultPerformanceTimer#warmup(int)
     */
    @Override
    public PerformanceSample execute(int iterations) {
        PerformanceSample performanceSample = performTests(iterations);
        dispatchToConsumers(null, performanceSample);
        return performanceSample;
    }

    /**
     * This execution is not very reliable and should be used only as
     * a reference.
     * @param milliseconds
     * @return
     */
    @Override
    public int iterationTimeEstimator(long milliseconds) {
        final long start = System.nanoTime();
        final long end = start + milliseconds * 1_000_000;
        final Map<String, Testable> tests = getTests();
        int counter = 0;
        initTests();
        while(System.nanoTime() < end) {
            executor.executeTests(tests, 1);
            counter++;
        }
        return counter;
    }

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     */
    @Override
    public DefaultPerformanceTimer warmup(int iterations) {
        performTests(iterations);
        return this;
    }

    private PerformanceSample performTests(int iterations)
            throws IllegalStateException {
        if (iterations <= 0) {
            throw new IllegalStateException(
                    "invalid iteration number = " + iterations);
        }
        initTests();
        final PerformanceSample performanceSample =
                executor.executeTests(getTests(), iterations);
        if (performanceSample == null ||
                performanceSample.getTimeMap().isEmpty()) {
            throw new AssertionError("no test performed");
        }
        return performanceSample;
    }

    @Override
    public DefaultPerformanceTimer resetTests() {
        testInitialized = false;
        return super.resetTests();
    }

    protected void initTests() {
        if (!testInitialized) {
            for (Testable testable: getTests().values()) {
                testable.setUp();
            }
            testInitialized = true;
        }
    }

    @Override
    public <T extends Instrumenter<PerformanceTimer>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
