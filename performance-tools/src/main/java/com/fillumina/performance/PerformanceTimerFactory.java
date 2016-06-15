package com.fillumina.performance;

import com.fillumina.performance.sample.DefaultPerformanceTimer;
import com.fillumina.performance.sample.PerformanceTimer;
import com.fillumina.performance.sample.executor.MultiThreadPerformanceExecutorBuilder;
import com.fillumina.performance.sample.executor.SingleThreadPerformanceExecutor;

/**
 * Static factory to create a {@link PerformanceTimer}.
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

    /**
     * Creates a single threaded performance test.
     */
    public static DefaultPerformanceTimer createSingleThreaded() {
        return new DefaultPerformanceTimer(new SingleThreadPerformanceExecutor(1));
    }

    /**
     * Creates a single threaded performance test specifying the number
     * of times each test will be switched during the execution of a single
     * iteration.
     *
     * @param fractions indicates the times tests switch execution during a
     *        single sample.
     *        Not very important for micro-benchmark should be used when each
     *        test last for a time longer than some milliseconds.
     */
    public static DefaultPerformanceTimer createSingleThreaded(int fractions) {
        return new DefaultPerformanceTimer(
                new SingleThreadPerformanceExecutor(fractions));
    }

    /**
     * Creates a multi threade {@link PerformanceTimer} builder.
     * Each test will be executed in a multi threaded
     * environment (so take extra care about thread safety, especially with
     * the test's fields).
     */
    public static MultiThreadPerformanceExecutorBuilder getMultiThreadedBuilder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }
}
