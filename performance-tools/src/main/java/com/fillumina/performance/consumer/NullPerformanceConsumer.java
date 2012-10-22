package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.LoopPerformances;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
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
