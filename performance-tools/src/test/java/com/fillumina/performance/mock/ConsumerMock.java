package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <A>
 */
public class ConsumerMock<A extends Assertable>
        implements AssertableConsumer<A> {

    private final List<A> list = new ArrayList<>();


    @Override
    public void consume(A assertable) {
        list.add(assertable);
    }

    public List<A> getConsumedAssertableList() {
        return Collections.unmodifiableList(list);
    }
}
