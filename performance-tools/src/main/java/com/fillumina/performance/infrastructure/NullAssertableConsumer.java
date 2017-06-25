package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.io.Serializable;

/**
 * A {@link AssertableConsumer} that does nothing. Useful to be passed
 * to methods that requires a consumer and doesn't accept {@code null}.
 *
 * @author Francesco Illuminati
 */
public final class NullAssertableConsumer<A extends Assertable>
        implements AssertableConsumer<A>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullAssertableConsumer<?> INSTANCE =
            new NullAssertableConsumer<>();

    /**
     * Use like this:
     * {@code NullPerformanceConsumer.<Map<ComposedName, PerformanceStats>>instance()}.
     * @param <A> the type of the accepted performance.
     * @return the created {@link AssertableConsumer}
     */
    @SuppressWarnings("unchecked")
    public static <A extends Assertable> AssertableConsumer<A>
            instance() {
        return (NullAssertableConsumer<A>) INSTANCE;
    }

    private NullAssertableConsumer() {}

    @Override
    public void consume(A assertable) {
        // do nothing
    }

}
