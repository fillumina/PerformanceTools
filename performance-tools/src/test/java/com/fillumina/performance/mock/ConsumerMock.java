package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.UnmodificableTNameMapWrapper;
import com.fillumina.performance.util.collection.LinkedMap;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <A>
 */
public class ConsumerMock<A extends Assertable>
        implements PerformanceConsumer<A> {

    private final LinkedMap<TName,A> list = new LinkedMap<>();


    @Override
    public void consume(TName tname, A assertable) {
        list.put(tname, assertable);
    }

    public UnmodificableTNameMapWrapper<A> getConsumedAssertableMap() {
        return new UnmodificableTNameMapWrapper<>(list);
    }
}
