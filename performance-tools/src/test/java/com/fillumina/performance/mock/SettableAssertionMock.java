package com.fillumina.performance.mock;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.ExperimentAssertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SettableAssertionMock implements ExperimentAssertion {

    private final Consumer<AssertableExperiment> consumer;
    private final Function<AssertableExperiment,String> viewer;

    public SettableAssertionMock(Consumer<AssertableExperiment> consumer) {
        this(consumer, a -> a.toString());
    }

    public SettableAssertionMock(Consumer<AssertableExperiment> consumer,
            Function<AssertableExperiment, String> viewer) {
        this.consumer = consumer;
        this.viewer = viewer;
    }

    @Override
    public void check(AssertableExperiment t) {
        consumer.accept(t);
    }

    @Override
    public void appendTo(Appendable appendable, AssertableExperiment assertable)
            throws IOException {
        appendable.append(viewer.apply(assertable));
    }

}
