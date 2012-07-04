package com.fillumina.performance;

import com.fillumina.performance.timer.LoopPerformances;

/**
 *
 * @author fra
 */
public interface PerformanceConsumer {

    PerformanceConsumer setMessage(final String message);

    PerformanceConsumer setPerformances(LoopPerformances performances);

    void process();

}
