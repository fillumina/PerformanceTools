package com.fillumina.performance;

import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.PerformanceTimer;
import com.fillumina.performance.speed.sample.executor.MultiThreadPerformanceExecutorBuilder;
import com.fillumina.performance.speed.sample.executor.SingleThreadPerformanceExecutor;

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
// TODO improve display of single test (used to know speed)
// TODO "all threads available" is not correct choose "number of cpus"
// TODO multithreading: throughput as total op/sec and op/sec per thread
// TODO write also op/sec instead of only ms
// TODO write also how many iterations performed (useful for multithreading)

// TODO test with several versions of JDK (7,8,oracle?)
// TODO test coverage (cobertura)
// TODO test with different memory manager

// TODO add warning about memory stability (cannot evaluate variations)
// TODO add warning if memory parameters aren't standard (possible error)
// TODO set mem analysis as experimental, add warnings

// TODO remove mem test when not useful in templates (improve execution speed)

// TODO improve fluent operations, create an easy builder from templates (?)
// TODO templates: on config write params, sequence, tests
public class PerformanceTimerFactory {

    /**
     * Creates a single threaded performance test.
     */
    public static DefaultPerformanceTimer createSingleThreaded() {
        return new DefaultPerformanceTimer(new SingleThreadPerformanceExecutor(1));
    }

    /**
     * Creates a single threaded performance test specifying in how many
     * fractions each test sample must be divided. Tests are interleaved
     * at each fraction to minimize external factors (i.e. OS scheduling and
     * CPU throttling).
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
     * Creates a multi thread {@link PerformanceTimer} builder.
     * Each test will be executed in a multi threaded
     * environment (so take extra care about thread safety, especially with
     * the test's fields).
     */
    public static MultiThreadPerformanceExecutorBuilder getMultiThreadedBuilder() {
        return new MultiThreadPerformanceExecutorBuilder();
    }
}
