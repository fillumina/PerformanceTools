package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that does nothing. Useful to be passed
 * to methods that requires a consumer and doesn't accept {@code null}.
 *
 * @author Francesco Illuminati
 */
public final class NullAssertableConsumer<A extends Assertable>
        implements Consumer<A>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final NullAssertableConsumer<?> INSTANCE =
            new NullAssertableConsumer<>();

    /**
     * @param <A> the type of the accepted assertable.
     * @return the created {@link Consumer}
     */
    @SuppressWarnings("unchecked")
    public static <A extends Assertable> Consumer<A> instance() {
        return (NullAssertableConsumer<A>) INSTANCE;
    }

    @Override
    public void accept(Assertable assertable) {
        // do nothing
    }

}
