package com.fillumina.performance;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public interface PerformanceConsumer extends Serializable {

    PerformanceConsumer setMessage(final String message);

    PerformanceConsumer setPerformances(LoopPerformances performances);

    void consume();

}
