package com.fillumina.performance.executor;

import com.fillumina.performance.util.ConsumerContainer;
import com.fillumina.performance.util.Nameable;
import com.fillumina.performance.util.Named;
import com.fillumina.performance.util.pathname.PathNameSettable;
import com.fillumina.performance.util.pathname.PathNamed;
import java.util.function.Supplier;

/**
 * Executes an experiment and return its result.
 *
 * @param I self (to allow sub-classes to call super methods with fluent interface)
 * @param N notifications
 * @param T test
 * @param R result
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestExecutor<I extends TestExecutor<I,N,T,R>, N, T, R>
        extends
            Supplier<R>, // TODO is this really useful?
            TestContainer<I,T>,
            ConsumerContainer<I,N>,
            PathNameSettable<I>,
            PathNamed,
            Named, Nameable<I> {

    /** Better name than {@link get()} for which it is just an alias. */
    default public R execute() {
        return get();
    }
}
