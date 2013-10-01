package com.fillumina.performance.producer;

/**
 * Defines an executor able to take some tests as a {@link TestsContainer}
 * and execute them producing some performances as {@link LoopPerformances}
 * that will be passed to some
 * {@link com.fillumina.performance.consumer.PerformanceConsumer}s.
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
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface InstrumentablePerformanceExecutor
        <T extends InstrumentablePerformanceExecutor<T>>
        extends PerformanceProducer, TestsContainer {

    /**
     * Allows the executor to be piloted by another given class.
     * <p>
     * IMPORTANT: the returned object is the one passed as an argument so
     * in a <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>
     * fluent interface</a></i> the next method refers to the argument:
     * <pre>
     *      first.instrumentedBy(second)
     *          .secondMethod();
     * </pre>
     * Is the same as:
     * <pre>
     *      first.instrumentedBy(second);
     *      second.secondMethod();
     * </pre>
     */
    <T extends PerformanceExecutorInstrumenter> T
            instrumentedBy(final T instrumenter);

    /** Executes the test. */
    LoopPerformancesHolder execute();

    /**
     * Executes the test without extracting statistics. It is useful
     * to force the JVM to perform early code optimizations before the actual
     * performance measurements are taken. You should run a particular code
     * at least some thousand times (depending on the JVM) for optimizations
     * to kick off.
     *
     * @see <a href='http://www.ibm.com/developerworks/java/library/j-jtp12214/#4.0'>
     *      Java theory and practice: Dynamic compilation and performance measurement
     *      (Brian Goetz)
     *      </a>
     */
    InstrumentablePerformanceExecutor<T> warmup();
}
