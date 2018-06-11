package com.fillumina.performance.executor;

import com.fillumina.performance.util.ConsumerNotifier;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.tname.TNominable;
import java.util.function.Supplier;

/**
 * Executes an experiment and return its result.
 *
 * @param I self (to allow sub-classes to call super methods with a fluid interface)
 * @param C notifications
 * @param T test
 * @param R result
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestExecutor<I extends TestExecutor<I,C,T,R>, C, T, R>
        extends
            Supplier<R>,
            TestContainer<I,T>,
            ConsumerNotifier<I,C>,
            TNominable<I>,
            TNamed {

    /** Better name than {@link get()} for which it is just an alias. */
    default public R execute() {
        return get();
    }
}
