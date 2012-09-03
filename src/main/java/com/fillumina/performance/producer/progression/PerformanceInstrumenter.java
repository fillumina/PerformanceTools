package com.fillumina.performance.producer.progression;

import com.fillumina.performance.producer.PerformanceProducer;
import com.fillumina.performance.producer.timer.LoopPerformancesHolder;

/**
 *
 * @author fra
 */
public interface PerformanceInstrumenter<T extends PerformanceInstrumenter<?>>
        extends PerformanceProducer<T> {

    LoopPerformancesHolder executeSequence();
}
