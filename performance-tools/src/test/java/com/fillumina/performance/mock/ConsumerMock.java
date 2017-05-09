package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <A>
 */
public class ConsumerMock<A extends Assertable>
        implements PerformanceConsumer<A> {

    private final List<A> list = new ArrayList<>();


    @Override
    public void consume(A assertable) {
        list.add(assertable);
    }

    public List<A> getConsumedAssertableList() {
        return Collections.unmodifiableList(list);
    }
}
