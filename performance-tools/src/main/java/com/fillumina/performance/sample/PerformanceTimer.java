package com.fillumina.performance.sample;

import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.infrastructure.PerformanceProducer;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceTimer
        extends PerformanceProducer<PerformanceSample, Testable>,
            TestContainer<Testable>, Instrumentable<PerformanceTimer> {

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
    PerformanceSample execute(int iterations);

    /**
     * This execution is not very reliable and should be used only as
     * a rough estimation of how many iterations can be done in a given time.
     * It is used mainly to calculate how many iterations should be done for
     * each sample collected.
     *
     * @param milliseconds
     * @return number of iterations executed (not really accurate)
     */
    int iterationTimeEstimator(long milliseconds);

    /**
     * Run exactly the same tests as {@link #execute()} without taking
     * any statistics. It's used to warm up the JVM into optimizing the code.
     */
    DefaultPerformanceTimer warmup(int iterations);
}
