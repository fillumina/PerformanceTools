package com.fillumina.performance.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import com.fillumina.performance.assertion.AssertableExperiment;

/**
 * Records the test names of performances.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 * @param <A>
 */
public class ConsumerMock<A extends AssertableExperiment> implements Consumer<A> {

    private final List<A> list = new ArrayList<>();

    @Override
    public void accept(A assertable) {
        list.add(assertable);
    }

    public List<A> getConsumedAssertableList() {
        return Collections.unmodifiableList(list);
    }
}
