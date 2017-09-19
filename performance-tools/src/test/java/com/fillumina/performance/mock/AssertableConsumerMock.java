package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerMock<A extends Assertable>
        implements Consumer<A> {

    private boolean notified = false;
    private A performance;

    @Override
    public void accept(A assertable) {
        this.performance = assertable;
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public A getReceivedAssertable() {
        return performance;
    }
}
