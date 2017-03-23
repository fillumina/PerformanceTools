package com.fillumina.performance.speed.sample;

import com.fillumina.performance.infrastructure.AbstractPerformanceProducer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.TestableController;
import com.fillumina.performance.speed.sample.executor.PerformanceExecutor;
import com.fillumina.performance.util.instrument.Instrumenter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A {@link PerformanceProducer} that executes tests and returns their
 * execution time as a {@link SpeedSample}.
 * The sample returned refers to one round of iterations
 * only and is often a very rough estimation of the speed of the actual code.
 * Systems are not very accurate in measuring short intervals of time
 * and so a measure is averaged over a certain number of iterations. To be more
 * accurate some statistics should be performed over several rounds of
 * iterations each of these is represented as a {@link SpeedSample}.
 * This code is used by more advanced estimator that collects several samples
 * and using statistics can give a much more precise indication of the
 * code speed.
 * <p>
 * Performance tests are subject to many factors that might
 * hinder their accuracy:
 * <ul>
 * <li>Hardware type and available resources (FPU, memory quantity, SDD);
 * <li>CPU speed throttling (heat level or energy management);
 * <li>Operative System type and load (concurrency and resource contention);
 * <li>JDK brand, version and configuration (code optimizations, memory management);
 * <li>JVM Garbage Collector (memory allocation, availability and contention).
 * </ul>
 * All these factors can produce relevant performance fluctuations.
 * The only way to marginalize these factors is to run the test long enough
 * so that those disturbances fade away statistically.
 * Anyway performance tests might fail randomly: there is really no way to
 * avoid that in a real system so try to increase the iteration number or
 * relax the tolerance of your assertions and close demanding background
 * processes.
 * <p>
 * This class is not thread safe.
 *
 * @author Francesco Illuminati
 */
public class DefaultPerformanceTimer
        extends AbstractPerformanceProducer
            <DefaultPerformanceTimer, SpeedSample, Testable>
        implements PerformanceTimer {
    private final PerformanceExecutor executor;
    private boolean testsInitialized;

    /**
     * Produces statistics executing tests using the specified executor.
     */
    public DefaultPerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
    }

    /**
     * Runs each test for approximately 250 milliseconds and returns a sample.
     * If a test takes more than that it will be executed only once.
     */
    @Override
    public PHolder<SpeedSample> execute() {
        assertTestsPresent();
        int[] estimatedIterations = iterationTimeEstimator(250);
        return new PHolder<>(getName(), execute(estimatedIterations));
    }

    /**
     * Executes the tests with the given number of iterations (all tests the
     * same).
     *
     * @param iterations number of times to repeat each test.
     * @return a sample
     */
    @Override
    public SpeedSample execute(int iterations) {
        assertTestsPresent();
        if (iterations < 1) {
            throw new IllegalArgumentException(
                    "Iterations must be positive, was = " + iterations);
        }
        return execute(createIterationsArray(iterations));
    }

    /**
     * Executes the performance test.
     *
     * @param iterations repeat the code under test for iterations time
     *        before measuring its time.
     * @see DefaultPerformanceTimer#warmup(int)
     */
    @Override
    public SpeedSample execute(int[] iterations) {
        assertTestsPresent();
        SpeedSample performanceSample = performTests(iterations);
        final PHolder<SpeedSample> performanceHolder =
                new PHolder<>(getName(), performanceSample);
        dispatchToConsumers(performanceHolder);
        return performanceSample;
    }

    /**
     * Estimation of how many iterations are completed in the given time.
     * This measure is very approximated (it has also tolerances) and should
     * not be relied upon. It is used for test tuning.
     *
     * @param milliseconds the time in milliseconds to wait for each test
     * @return number of iteration executed in the given time (approx)
     *
     * @see <a href='http://shipilev.net/blog/2014/nanotrusting-nanotime/'>
     *  Aleksey Shipilёv: Nanotrusting the Nanotime</a>
     */
    @Override
    public int[] iterationTimeEstimator(long milliseconds) {
        assertTestsPresent();
        initTests();
        warmup(1);
        final Map<String, Testable> tests = getTests();
        int[] estimations = new int[tests.size()];
        int index = 0;
        for (Map.Entry<String, Testable> entry : tests.entrySet()) {
            String name = entry.getKey();
            Testable test = entry.getValue();
            estimations[index] = estimateSingleTest(milliseconds, name, test);
            index++;
        }
        tearDownTests();
        return estimations;
    }

    private int estimateSingleTest(long millis, String name, Testable testable) {
        LinkedHashMap<String,Testable> singletonTest =
                createSingleton("singleton", testable);
        final double desiredTimeNs = millis * 1E6;
        int iterations = 1;
        final int max = 20;
        IterationLogger ite = new IterationLogger(name, max);
        int[] counter = new int[]{iterations};
        for (int i=0; i<max; i++) {
            SpeedSample sample = executor.executeTests(singletonTest, counter);
            long timeNs = sample.getTotalTimeNs();
            if (timeNs < desiredTimeNs * 0.9 ||
                    (timeNs > 1.5 * desiredTimeNs && iterations > 1)) {
                double ratio = desiredTimeNs / timeNs;
                iterations = (int) Math.ceil(1.1 * iterations * ratio);
                iterations = (iterations == 0) ? 1 : iterations;
                ite.log(iterations, desiredTimeNs, timeNs, ratio);
                if (iterations == Integer.MAX_VALUE) {
                    break;
                }
                counter[0] = iterations;
            } else {
                return iterations;
            }
        }
        throw new InvalidTestException(ite.getMessage());
    }

    private LinkedHashMap<String,Testable> createSingleton(String name,
            Testable testable) {
        LinkedHashMap<String,Testable> map = new LinkedHashMap<>(1, 1);
        map.put(name, testable);
        return map;
    }

    @Override
    public DefaultPerformanceTimer warmup(int iterations) {
        return warmup(createIterationsArray(iterations));
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

    private SpeedSample performTests(int[] iterations)
            throws IllegalStateException {
        Map<String,Testable> tests = getTests();
        assertValidIterations(iterations, tests);
        initTests();
        final SpeedSample performanceSample =
                executor.executeTests(getTests(), iterations);
        tearDownTests();
        if (performanceSample == null ||
                performanceSample.getTimeMap().isEmpty()) {
            throw new RuntimeException("no performance test executed");
        }
        return performanceSample;
    }

    private void assertValidIterations(int[] iterations,
            Map<String, Testable> tests) throws IllegalStateException {
        if (iterations.length != tests.size()) {
            throw new IllegalArgumentException(
                    "invalid iteration number = " + Arrays.toString(iterations));
        }
        for (int iteration : iterations) {
            if (iteration < 0) {
                throw new IllegalArgumentException(
                        "invalid iteration value = " + iteration);
            }
        }
    }

    @Override
    public DefaultPerformanceTimer clearTests() {
        tearDownTests();
        return super.clearTests();
    }

    /** Used to initialize only once even if warmup is required. */
    private void initTests() {
        if (!testsInitialized) {
            for (Testable testable: getTests().values()) {
                TestableController.INSTANCE.setUp(testable);
            }
            testsInitialized = true;
        }
    }

    /** Used to teardown only once even if warmup is required. */
    private void tearDownTests() {
        if (testsInitialized) {
            for (Testable testable: getTests().values()) {
                TestableController.INSTANCE.tearDown(testable);
            }
            testsInitialized = false;
        }
    }

    /**
     * Set a supervisor able to pilot this {@link PerformanceTimer}.
     */
    @Override
    public <T extends Instrumenter<PerformanceTimer>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
