package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.time.sample.iterator.PerformanceExecutor;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.tname.TName;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Computers are not very accurate in measuring short intervals of time
 * and so to improve its accuracy a measure is averaged over many samples.
 * <br>
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
        extends AbstractSampleProducer<PerformanceTimer>
        implements PerformanceTimer {

    private final PerformanceExecutor executor;
    private long sampleTimeMs = 250;

    /**
     * Produces statistics executing tests using the specified executor.
     */
    public DefaultPerformanceTimer(final PerformanceExecutor executor) {
        this.executor = executor;
    }

    /**
     * Sets a rough estimation of how long a sample will takes for each test.
     *
     * @param value duration in milliseconds
     * @return itself
     */
    public DefaultPerformanceTimer setSampleTimeMs(final long value) {
        this.sampleTimeMs = value;
        return this;
    }

    /**
     * Runs each test and returns a sample.
     */
    @Override
    public Map<StatsType, Sample> get() {
        int[] estimatedIterations = estimateIterations(sampleTimeMs);
        return executeWithIterations(estimatedIterations);
    }

    @Override
    public Map<StatsType, Sample> executeWithIterations(int... iterations) {
        TimeSampleBuilder builder = iterate(iterations);
        Sample avgSample = builder.buildAverageTimeSample();
        dispatchToConsumers(avgSample);
        Sample thrSample = builder.buildThroughputSample();
        dispatchToConsumers(thrSample);
        return new IndexedHashMap<StatsType,Sample>()
                .add(TimeStatsType.AVERAGE, avgSample)
                .add(TimeStatsType.THROUGHPUT, thrSample)
                .unmodifiableView();
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
        int[] actualIterations = createIterationsArrayIfNeeded(iterations);
        TimeSampleBuilder builder = performTests(actualIterations);
        return builder;
    }

    private int[] createIterationsArrayIfNeeded(int[] iterations) {
        int testsSize = getTests().size();
        final int length = iterations.length;
        if (length == 0 || !(length == 1  || length == testsSize) ) {
            throw new IllegalArgumentException("there must " + testsSize +
                    " iterations, was: " + length);
        }
        for (int i : iterations) {
            if (i < 1) {
                throw new IllegalArgumentException(
                        "Iterations must be positive, was = " + iterations);
            }
        }
        if (length == testsSize) {
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
     * <br>
     * The test execution order is scrambled to help detecting JVM bias
     * toward first executed test.
     * <br>
     * Each tests is executed at least twice, first time as initialization
     * (required to let JVM load the class and initialize its internals)
     * and the second time to actually measure its duration.
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
        // https://www.youtube.com/watch?v=hjpzLXoUu1Y&t=1596
        // executes all tests once so to init polimorphic classes
        warmup(1);
        return doEstimation(milliseconds);
    }

    private int[] doEstimation(long milliseconds)
            throws InvalidTestException {
        IndexedHashMap<TName,Runnable> map = getTests();
        int size = map.size();
        int[] estimations = new int[size];
        // this way the test execution order will be scrambled which is
        // useful to detect JVM bias toward first executed test.
        for (int idx : getShuffledIndexes(size)) {
            Map.Entry<TName,Runnable> entry = map.getEntryAtIndex(idx);
            TName name = entry.getKey();
            Runnable test = entry.getValue();
            estimations[idx] = estimateSingleTest(name, test, milliseconds);
        }
        return estimations;
    }

    static int[] getShuffledIndexes(int size) {
        int[] array = new int[size];
        for (int i=0; i<size; i++) {
            array[i] = i;
        }
        // swap indexes randomly
        Random rnd = ThreadLocalRandom.current();
        for (int i=size-1; i>0; i--) {
            int idx = rnd.nextInt(i);
            int t = array[i];
            array[i] = array[idx];
            array[idx] = t;
        }
        return array;
    }

    private int estimateSingleTest(TName name, Runnable testable, long millis)
        throws InvalidTestException {
        final double desiredTimeNs = millis * 1E6;
        int previousIterations = -1;
        int iterations = 1;
        final int max = 30;
        IterationLogger iteLogger = new IterationLogger(name, max);
        for (int i=0; i<max; i++) {
            TimeSampleBuilder ita = executeSingleTest(testable, iterations);
            long timeNs = ita.getTotalTimeNs();
            if (!almostEqualsTo(iterations, previousIterations, 0.1) &&
                    !almostEqualsTo(timeNs, desiredTimeNs, 0.1)) {
                previousIterations = iterations;
                double ratio = desiredTimeNs / timeNs;
                iterations = (int) Math.ceil(1.1 * iterations * ratio);
                iterations = (iterations == 0) ? 1 : iterations;
                iteLogger.log(i, iterations, desiredTimeNs, timeNs, ratio);
                if (iterations == Integer.MAX_VALUE) {
                    break;
                }
            } else {
                return iterations;
            }
        }
        throw new InvalidTestException(iteLogger.getMessage());
    }

    private TimeSampleBuilder executeSingleTest(Runnable runnable,
            int iterations) {
        final IndexedHashMap<TName,Runnable> singletonTest =
                IndexedHashMap.create(TN.tname("singleton"), runnable);
        final int[] singletonArray = new int[]{iterations};
        return executor.executeIterations(singletonTest, singletonArray);
    }

    /** Equality within given margin. */
    static boolean almostEqualsTo(double a, double b, double margin) {
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
        IndexedHashMap<TName,Runnable> tests = getTests();
        int[] actualIterations = createIterationsArrayIfNeeded(iterations);
        final TimeSampleBuilder builder =
                executor.executeIterations(tests, actualIterations);
        if (builder == null || builder.isEmpty()) {
            throw new RuntimeException("no performance test executed");
        }
        return builder;
    }
}
