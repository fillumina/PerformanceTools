package com.fillumina.performance.producer;

import java.io.Serializable;

/**
 *
 * @author fra
 */
public class DefaultPerformanceExecutorInstrumenter
            <T extends PerformanceExecutorInstrumenter>
        implements PerformanceExecutorInstrumenter, Serializable {
    private static final long serialVersionUID = 1L;

    private InstrumentablePerformanceExecutor<?> performanceExecutor;

    /**
     * Mandatory.
     * Most of the time it is not called directly by the user code.
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public T instrument(final InstrumentablePerformanceExecutor<?> performanceExecutor) {
        this.performanceExecutor = performanceExecutor;
        return (T) this;
    }

    public InstrumentablePerformanceExecutor<?> getPerformanceExecutor() {
        return performanceExecutor;
    }
}
