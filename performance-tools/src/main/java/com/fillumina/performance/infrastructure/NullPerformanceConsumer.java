package com.fillumina.performance.infrastructure;

import java.io.Serializable;
import com.fillumina.performance.assertion.Assertable;

/**
 * A {@link PerformanceConsumer} that does nothing. Useful to be passed
 * to methods that requires a consumer and doesn't accept {@code null}.
 *
 * @author Francesco Illuminati
 */
public final class NullPerformanceConsumer<A extends Assertable>
        implements PerformanceConsumer<A>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullPerformanceConsumer<?> INSTANCE =
            new NullPerformanceConsumer<>();

    /**
     * Use like this:
     * {@code NullPerformanceConsumer.<Map<ComposedName, PerformanceStats>>instance()}.
     * @param <A> the type of the accepted performance.
     * @return the created {@link PerformanceConsumer}
     */
    @SuppressWarnings("unchecked")
    public static <A extends Assertable> PerformanceConsumer<A>
            instance() {
        return (NullPerformanceConsumer<A>) INSTANCE;
    }

    private NullPerformanceConsumer() {}

    @Override
    public void consume(final PerformanceHolder<A> stats) {
        // do nothing
    }

}
