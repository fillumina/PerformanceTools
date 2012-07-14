package com.fillumina.performance.producer.timer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public class LoopPerformancesHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LoopPerformances loopPerformances;

    public LoopPerformancesHolder(final LoopPerformances loopPerformances) {
        this.loopPerformances = loopPerformances;
    }

    public LoopPerformances getLoopPerformances() {
        return loopPerformances;
    }

    public void use(final PerformanceConsumer consumer) {
        consumer.consume(null, loopPerformances);
    }
}
