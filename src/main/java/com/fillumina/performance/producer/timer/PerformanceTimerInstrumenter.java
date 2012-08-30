package com.fillumina.performance.producer.timer;

/**
 *
 * @author fra
 */
public interface PerformanceTimerInstrumenter {

    PerformanceTimerInstrumenter instrument(
            final PerformanceTimer performanceTimer);
}
