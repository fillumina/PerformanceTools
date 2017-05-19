package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PHolder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface TestListener {

    <S extends Assertable, M extends Assertable> boolean notify(
                Configuration config,
                MixedAssertion<?, ?> assertion,
                PHolder<S> speedStats,
                PHolder<M> usedMemStats,
                PHolder<M> allocatedMemStats,
                Throwable exception);
}
