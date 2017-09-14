package com.fillumina.performance.infrastructure;

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
public interface PerformanceProducer
            <I extends PerformanceProducer<I,C,T,P>, C, T, P>
        extends
            Supplier<P>,
            TestContainer<I,T>,
            ConsumerNotifier<C>,
            Nominable<I> {
}
