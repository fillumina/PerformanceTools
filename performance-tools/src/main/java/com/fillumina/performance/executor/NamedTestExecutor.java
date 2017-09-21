package com.fillumina.performance.executor;

import com.fillumina.performance.util.ConsumerNotifier;
import com.fillumina.performance.util.Nominable;
import java.util.function.Supplier;

/**
 * @param I self
 * @param C notifiable
 * @param T test
 * @param P product
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface NamedTestExecutor
            <I extends NamedTestExecutor<I,C,T,P>, C, T, P>
        extends
            Supplier<P>,
            TestContainer<I,T>,
            ConsumerNotifier<C>,
            Nominable<I> {

    /** Better name than {@link get()} for which it is just an alias. */
    default public P execute() {
        return get();
    }
}
