package com.fillumina.performance.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.sample.executor.PerformanceExecutor;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * A {@link PerformanceProducer} that executes tests and returns their
 * execution time as a {@link PerformanceSample}.
 * The sample returned refers to one bunch of iterations
 * only and is a very rough estimation of the speed of the actual code.
 * This code is used by more advanced estimator that collects several samples
 * and using statistics can give a much more precise indication of the
 * code speed.
 *
 * <b>NOTE</b>
 * Performance tests are subject to many factors that might
 * hinder their accuracy:
 * <ul>
 * <li>Hardware type and available resources (FPU, memory quantity, SDD);
 * <li>CPU speed throttling (heat level or energy management);
 * <li>Operative System type and load (concurrency and resource contention);
 * <li>JDK brand, version and configuration (code optimizations);
 * <li>JVM Garbage Collector (memory allocation, availability and contention).
 * </ul>
 * All these factors can produce relevant performance fluctuations.
 * The only way to marginalize these factors is to run the test long enough
 * so that those disturbances fade away statistically.
 * Anyway performance tests might fail randomly: there is really no way to
 * avoid that so try to increase the iteration number or
 * relax the tolerance of your assertions and close demanding background
 * processes.
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
     * Produces statistics executing tests using the specified executor.
     */
    public DefaultPerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
    }

    /**
     * Runs the tests for approximately 250 ms and returns a sample.
     */
    @Override
    public PerformanceHolder<PerformanceSample> execute() {
        assertTestsPresent();
        int[] estimatedIterations = iterationTimeEstimator(250);
        return new PerformanceHolder<>(execute(estimatedIterations));
    }

    @Override
    public PerformanceSample execute(int iterations) {
        assertTestsPresent();
        if (iterations < 1) {
            throw new IllegalArgumentException(
                    "Iterations must be positive, was = " + iterations);
        }
        return execute(createIterationArray(iterations));
    }

    /**
     * Executes the performance test.
     *
     * @param iterations repeat the code under test for iterations time
     *        before measuring its time.
     * @see DefaultPerformanceTimer#warmup(int)
     */
    @Override
    public PerformanceSample execute(int[] iterations) {
        assertTestsPresent();
        PerformanceSample performanceSample = performTests(iterations);
        dispatchToConsumers(null, performanceSample);
        return performanceSample;
    }

    /**
     * Estimation of how many iterations are completed in the given time.
     *
     * @param milliseconds The time to wait for the iteration estimation
     * @return number of iteration executed in the given time (approx)
     *
     * @see <a href='http://shipilev.net/blog/2014/nanotrusting-nanotime/'>
     *  Aleksey Shipilёv: Nanotrusting the Nanotime</a>
     */
    @Override
    public int[] iterationTimeEstimator(long milliseconds) {
        assertTestsPresent();
        initTests();
        final Map<String, Testable> tests = getTests();
        int[] estimations = new int[tests.size()];
        int index = 0;
        for (Testable t : tests.values()) {
            estimations[index] = estimateSingleTest(milliseconds, t);
            index++;
        }
        return estimations;
    }

    private int estimateSingleTest(long millis, Testable testable) {
        Map<String,Testable> singletonTest =
                Collections.<String, Testable>singletonMap(null, testable);
        long time;
        PerformanceSample sample;
        int[] counter = new int[]{1};
        while (true) {
            sample = executor.executeTests(singletonTest, counter);
            time = (sample.getTotalTime() / 1_000_000) + 1;
            if (Math.abs(time - millis) > millis * 0.1) {
                counter[0] = (int) (sample.getTimeMap().values().iterator().next()
                        .getIterations() * (millis * 1.0 / time));
            } else {
                break;
            }
        }
        return counter[0];
    }

    @Override
    public DefaultPerformanceTimer warmup(int iterations) {
        return warmup(createIterationArray(iterations));
    }

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code
     * before taking the actual sample.
     */
    @Override
    public DefaultPerformanceTimer warmup(int[] iterations) {
        performTests(iterations);
        return this;
    }

    private PerformanceSample performTests(int[] iterations)
            throws IllegalStateException {
        Map<String,Testable> tests = getTests();
        assertValidIterations(iterations, tests);
        initTests();
        final PerformanceSample performanceSample =
                executor.executeTests(getTests(), iterations);
        if (performanceSample == null ||
                performanceSample.getTimeMap().isEmpty()) {
            throw new AssertionError("no performance test executed");
        }
        return performanceSample;
    }

    private void assertValidIterations(int[] iterations,
            Map<String, Testable> tests) throws IllegalStateException {
        if (iterations.length != tests.size()) {
            throw new IllegalStateException(
                    "invalid iteration number = " + Arrays.toString(iterations));
        }
        for (int iteration : iterations) {
            if (iteration < 0) {
                throw new IllegalStateException(
                        "invalid iteration value = " + iteration);
            }
        }
    }

    @Override
    public DefaultPerformanceTimer clearTests() {
        testInitialized = false;
        return super.clearTests();
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
