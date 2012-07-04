package com.fillumina.performance;

import com.fillumina.performance.timer.LoopPerformances;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author fra
 */
public class MultiPerformanceConsumer
        implements PerformanceConsumer, Serializable {
    private static final long serialVersionUID = 1L;

    private final List<PerformanceConsumer> consumers;

    public MultiPerformanceConsumer(final PerformanceConsumer... consumers) {
        this.consumers = Arrays.asList(consumers);
    }

    @Override
    public PerformanceConsumer setMessage(final String message) {
        for (PerformanceConsumer pc: consumers) {
            pc.setMessage(message);
        }
        return this;
    }

    @Override
    public PerformanceConsumer setPerformances(final LoopPerformances performances) {
        for (PerformanceConsumer pc: consumers) {
            pc.setPerformances(performances);
        }
        return this;
    }

    @Override
    public void process() {
        for (PerformanceConsumer pc: consumers) {
            pc.process();
        }
    }

}
