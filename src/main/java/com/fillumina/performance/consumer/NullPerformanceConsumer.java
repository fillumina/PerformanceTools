package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.timer.LoopPerformances;
import java.io.Serializable;

/**
 *
 * @author fra
 */
public final class NullPerformanceConsumer
        implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullPerformanceConsumer INSTANCE =
            new NullPerformanceConsumer();

    private NullPerformanceConsumer() {}

    @Override
    public void consume(final String message,
            final LoopPerformances loopPerformances) {
        // do nothing
    }

}
