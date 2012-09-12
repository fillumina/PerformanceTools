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

    private PerformanceExecutor<?> performanceExecutor;

    /**
     * Mandatory.
     * Most of the time it is not called directly by the user code.
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public T instrument(final PerformanceExecutor<?> performanceExecutor) {
        this.performanceExecutor = performanceExecutor;
        return (T) this;
    }

    public PerformanceExecutor<?> getPerformanceExecutor() {
        return performanceExecutor;
    }
}
