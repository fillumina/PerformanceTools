package com.fillumina.performance.producer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceExecutorInstrumenter {

    PerformanceExecutorInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor);
}
