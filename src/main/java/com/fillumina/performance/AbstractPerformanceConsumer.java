package com.fillumina.performance;

import com.fillumina.performance.timer.LoopPerformances;

/**
 *
 * @author fra
 */
public abstract class AbstractPerformanceConsumer<T extends PerformanceConsumer>
        implements PerformanceConsumer {
    private static final long serialVersionUID = 1L;

    private LoopPerformances loopPerformances;
    private String message;

    protected LoopPerformances getLoopPerformances() {
        return loopPerformances;
    }

    protected String getMessage() {
        return message;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setMessage(final String message) {
        this.message = message;
        return (T) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setPerformances(LoopPerformances performances) {
        this.loopPerformances = performances;
        return (T) this;
    }

    @Override
    public abstract void process();

}
