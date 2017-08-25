package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.Nominable;
import com.fillumina.performance.util.instrument.Instrumentable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceProducer
            <I extends PerformanceProducer<I,S,T,P>, S, T, P>
        extends
            TestContainer<T>,
            ConsumerNotifier<S>,
            Instrumentable<I>,
            Nominable<I> {

    P execute();
}
