package com.fillumina.performance.executor;

import com.fillumina.performance.util.ConsumerNotifier;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.tname.TNominable;
import java.util.function.Supplier;

/**
 * @param I self
 * @param C notifiable
 * @param T test
 * @param P product
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestExecutor<I extends TestExecutor<I,C,T,P>, C, T, P>
        extends
            Supplier<P>,
            TestContainer<I,T>,
            ConsumerNotifier<C>,
            TNominable<I>,
            TNamed {

    /** Better name than {@link get()} for which it is just an alias. */
    default public P execute() {
        return get();
    }
}
