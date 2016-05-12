package com.fillumina.performance.infrastructure;

import java.io.Serializable;

/**
 * A {@link PerformanceConsumer} that does nothing. Useful to be passed
 * to methods that requires a consumer and doesn't accept {@code null}.
 *
 * @author Francesco Illuminati
 */
public final class NullPerformanceConsumer<A>
        implements PerformanceConsumer<A>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullPerformanceConsumer<?> INSTANCE =
            new NullPerformanceConsumer<>();

    @SuppressWarnings("unchecked")
    public static <A> NullPerformanceConsumer<A> instance() {
        return (NullPerformanceConsumer<A>) INSTANCE;
    }

    private NullPerformanceConsumer() {}

    @Override
    public void consume(final String message, final A stats) {
        // do nothing
    }

}
