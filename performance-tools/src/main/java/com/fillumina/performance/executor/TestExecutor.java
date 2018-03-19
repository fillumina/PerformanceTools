package com.fillumina.performance.executor;

import com.fillumina.performance.util.ConsumerNotifier;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.tname.TNominable;
import java.util.function.Supplier;

/**
 *
 * @param I self (to allow sub-classes to call super methods with a fluid interface)
 * @param C notifiable  type of the notification messages
 * @param T test        type of the tests
 * @param P product
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestExecutor<I extends TestExecutor<I,C,T,P>, C, T, P>
        extends
            Supplier<P>,
            TestContainer<I,T>,
            ConsumerNotifier<I,C>,
            TNominable<I>,
            TNamed {

    /** Better name than {@link get()} for which it is just an alias. */
    default public P execute() {
        return get();
    }
}
