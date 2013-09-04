package com.fillumina.performance.producer;

import com.fillumina.performance.consumer.PerformanceConsumer;
import com.fillumina.performance.consumer.viewer.StringTableViewer;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LoopPerformancesHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LoopPerformances loopPerformances;
    private final String name;
    private boolean active = true;

    public LoopPerformancesHolder(final LoopPerformances loopPerformances) {
        this(null, loopPerformances);
    }

    public LoopPerformancesHolder(final String name,
            final LoopPerformances loopPerformances) {
        this.name = name;
        this.loopPerformances = loopPerformances;
    }

    public LoopPerformances getLoopPerformances() {
        return loopPerformances;
    }

    public LoopPerformancesHolder use(final PerformanceConsumer consumer) {
        if (active) {
            consumer.consume(name, loopPerformances);
        }
        return this;
    }

    public LoopPerformancesHolder whenever(final boolean value) {
        this.active = value;
        return this;
    }

    @Override
    public String toString() {
        return new StringTableViewer(name, loopPerformances).toString();
    }
}
