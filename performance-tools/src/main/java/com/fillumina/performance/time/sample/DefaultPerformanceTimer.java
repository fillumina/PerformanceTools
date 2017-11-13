package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.time.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.tname.TName;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Computers are not very accurate in measuring short intervals of time
 * and so to improve its accuracy a measure is averaged over several
 * samples.
 * <p>
 * Timing tests are subject to many factors that might hinder their accuracy:
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
        extends AbstractSampleProducer<PerformanceTimer,
                                       AbstractTimeSample>
        implements PerformanceTimer {

    private final PerformanceExecutor executor;
    private long sampleTimeMs = 250;

    /**
     * Produces statistics executing tests using the specified executor.
     */
    public DefaultPerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
    }

    public DefaultPerformanceTimer setSampleTimeMs(final long value) {
        this.sampleTimeMs = value;
        return this;
    }

    /**
     * Runs each test for approximately 250 milliseconds and returns a sample.
     * If a test takes more than that it will be executed only once.
     */
    @Override
    public Map<Class<?>, AbstractTimeSample> get() {
        int[] estimatedIterations = estimateIterations(sampleTimeMs);
        return executeWithIterations(estimatedIterations);
    }

    @Override
    public Map<Class<?>, AbstractTimeSample> executeWithIterations(
            int... iterations) {
        TimeSampleBuilder builder = iterate(iterations);
        AverageTimeSample avgSample = builder.buildAverageTimeSample();
        dispatchToConsumers(avgSample);
        ThroughputSample thrSample = builder.buildThroughputSample();
        dispatchToConsumers(thrSample);
        return LinkedMap
                .<Class<?>,AbstractTimeSample>builder()
                .put(AverageTimeSample.class, avgSample)
                .put(ThroughputSample.class, thrSample)
                .build();
    }

    /**
     * Executes the tests with the given number of iterations (all tests the
     * same).
     *
     * @param iterations number of times to repeat each test.
     * @return a sample
     */
    @Override
    public TimeSampleBuilder iterate(int iterations) {
        assertTestsPresent();
        if (iterations < 1) {
            throw new IllegalArgumentException(
                    "Iterations must be positive, was = " + iterations);
        }
        return iterate(new int[]{iterations});
    }

    /**
     * Executes the performance test.
     *
     * @param iterations repeat the code under test for iterations time
     *        before measuring its time.
     * @see DefaultPerformanceTimer#warmup(int)
     */
    @Override
    public TimeSampleBuilder iterate(int[] iterations) {
        assertTestsPresent();
        TimeSampleBuilder builder =
                performTests(createIterationsArrayIfNeeded(iterations));
        return builder;
    }

    private int[] createIterationsArrayIfNeeded(int[] iterations) {
        int testsSize = getTests().size();
        if (iterations.length == testsSize) {
            return iterations;
        }
        int[] iterationArray = new int[testsSize];
        int value = (iterations[0] == 0) ? 1 : iterations[0];
        Arrays.fill(iterationArray, value);
        return iterationArray;
    }

    /**
     * Estimation of how many iterations are completed in the given time.
     * This measure is very approximated (it has also tolerances) and should
     * not be relied upon. It is used for test tuning.
     * <p>
     * The test execution order is scrambled to help detecting JVM bias
     * toward first executed test.
     *
     * @param milliseconds the time in milliseconds to wait for each test
     * @return number of iteration executed in the given time (approx)
     *
     * @see <a href='http://shipilev.net/blog/2014/nanotrusting-nanotime/'>
     *  Aleksey Shipilёv: Nanotrusting the Nanotime</a>
     */
    @Override
    public int[] estimateIterations(long milliseconds)
            throws InvalidTestException {
        assertTestsPresent();
        warmup(1);
        return doEstimation(milliseconds);
    }

    private int[] doEstimation(long milliseconds)
            throws InvalidTestException {
        int[] estimations = new int[getTests().size()];
        int index = 0;
        // this way the test execution order will be scrambled which is
        // useful to detect JVM bias toward first executed test.
        List<Map.Entry<TName, Runnable>> entries =
                new ArrayList<>(getTests().entrySet());
        Collections.shuffle(entries, new SecureRandom());
        for (Map.Entry<TName, Runnable> entry : entries) {
            TName name = entry.getKey();
            Runnable test = entry.getValue();
            estimations[index] = estimateSingleTest(name, test, milliseconds);
            index++;
        }
        return estimations;
    }

    private int estimateSingleTest(TName name, Runnable testable, long millis)
        throws InvalidTestException {
        final double desiredTimeNs = millis * 1E6;
        int previousIterations = -1;
        int iterations = 1;
        final int max = 30;
        IterationLogger ite = new IterationLogger(name, max);
        for (int i=0; i<max; i++) {
            TimeSampleBuilder ita =
                    executeSingleTest(testable, iterations);
            long timeNs = ita.getTotalTimeNs();
            if (!close(iterations, previousIterations, 0.1) &&
                    !close(timeNs, desiredTimeNs, 0.1)) {
                previousIterations = iterations;
                double ratio = desiredTimeNs / timeNs;
                iterations = (int) Math.ceil(1.1 * iterations * ratio);
                iterations = (iterations == 0) ? 1 : iterations;
                ite.log(i, iterations, desiredTimeNs, timeNs, ratio);
                if (iterations == Integer.MAX_VALUE) {
                    break;
                }
            } else {
                return iterations;
            }
        }
        throw new InvalidTestException(ite.getMessage());
    }

    private TimeSampleBuilder executeSingleTest(Runnable runnable,
            int iterations) {
        final LinkedMap<TName,Runnable> singletonTest =
                LinkedMap.create(TN.tname("singleton"), runnable);
        final int[] singletonArray = new int[]{iterations};
        return executor.executeIterations(singletonTest, singletonArray);
    }

    static boolean close(double a, double b, double margin) {
        return a >= b * (1.0 - margin) && a <= b * (1.0 + margin);
    }

    @Override
    public DefaultPerformanceTimer warmup(int iterations) {
        return warmup(new int[]{iterations});
    }

    /**
     * Run exactly the same tests as {@link #get()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code
     * before taking the actual sample.
     */
    @Override
    public DefaultPerformanceTimer warmup(int[] iterations) {
        performTests(iterations);
        return this;
    }

    private TimeSampleBuilder performTests(int[] iterations)
            throws IllegalStateException {
        LinkedMap<TName,Runnable> tests = getTests();
        int[] actualIterations = span(iterations, tests.size());
        final TimeSampleBuilder builder =
                executor.executeIterations(tests, actualIterations);
        if (builder == null || builder.isEmpty()) {
            throw new RuntimeException("no performance test executed");
        }
        return builder;
    }

    private int[] span(int[] iterations, int size)
            throws IllegalStateException {
        if (iterations.length != size) {
            int value = iterations[0];
            if (value <= 0) {
                throw new IllegalStateException("illegal iterations: " + value);
            }
            int[] result = new int[size];
            Arrays.fill(result, value);
            return result;
        }
        for (int iteration : iterations) {
            if (iteration < 0) {
                throw new IllegalArgumentException(
                        "invalid iteration value = " + iteration);
            }
        }
        return iterations;
    }
}
