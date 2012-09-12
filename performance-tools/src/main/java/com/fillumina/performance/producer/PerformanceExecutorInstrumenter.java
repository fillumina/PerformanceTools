package com.fillumina.performance.producer;

/**
 *
 * @author fra
 */
public interface PerformanceExecutorInstrumenter {

    PerformanceExecutorInstrumenter instrument(
            final InstrumentablePerformanceExecutor<?> performanceExecutor);
}
