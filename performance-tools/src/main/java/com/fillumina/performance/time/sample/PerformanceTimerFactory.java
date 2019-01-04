package com.fillumina.performance.time.sample;

import com.fillumina.performance.time.sample.iterator.MultiThreadPerformanceExecutorBuilder;
import com.fillumina.performance.time.sample.iterator.SelectorMultiThreadPerformanceExecutor;
import com.fillumina.performance.time.sample.iterator.SingleThreadPerformanceExecutor;

/**
 * Static factory to create a {@link PerformanceTimer}.
 * <p>
 * This class is not thread safe. Don't run more than one
 * {@link PerformanceTimer} test at the same time because speed tests are very
 * sensitive to CPU resource fluctuations.
 *
 * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp02225/index.html'>
 *      Java theory and practice: Anatomy of a flawed microbenchmark
 *      (Brian Goetz)
 *      </a>
 *
 * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0'>
 *      Java theory and practice: Dynamic compilation and performance measurement
 *      (Brian Goetz)
 *      </a>
 *
 * @author Francesco Illuminati
 */
public class PerformanceTimerFactory {

    public static DefaultPerformanceTimer createPerformanceTimer(
            SelectorMultiThreadPerformanceExecutor.Configuration conf) {
        return new DefaultPerformanceTimer(
                new SelectorMultiThreadPerformanceExecutor(conf));
    }

    /**
     * Creates a single threaded performance test.
     */
    public static DefaultPerformanceTimer createSingleThreaded() {
        return new DefaultPerformanceTimer(
                SingleThreadPerformanceExecutor.INSTANCE);
    }

    /**
     * Creates a single threaded performance test specifying how many
     * fractions each test sample must be divided into. Tests are interleaved
     * at each fraction to minimize external factors (i.e. OS scheduling and
     * CPU throttling).
     *
     * @param fractions indicates how many times tests switch execution during a
     *        single sample.
     *        Not very important for micro-benchmark should be used when each
     *        test last for a time longer than some milliseconds.
     */
    public static DefaultPerformanceTimer createSingleThreadedWithFractions(
            int fractions) {
        return new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor(fractions));
    }

    /**
     * Creates a multi thread {@link PerformanceTimer} builder.
     * Each test will be executed in a multi threaded
     * environment (so take extra care about thread safety, especially with
     * the test's fields).
     */
    public static MultiThreadPerformanceExecutorBuilder getMultiThreadedBuilder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }
}
