package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.Nominable;
import com.fillumina.performance.util.instrument.Instrumentable;
import java.util.function.Supplier;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface PerformanceProducer
            <I extends PerformanceProducer<I,S,T,P>, S, T, P>
        extends
            Supplier<P>,
            TestContainer<T>,
            ConsumerNotifier<S>,
            Instrumentable<I>,
            Nominable<I> {

}
