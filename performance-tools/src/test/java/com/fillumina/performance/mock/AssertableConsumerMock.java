package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.AbstractAssertableConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableConsumerMock<A extends Assertable>
        extends AbstractAssertableConsumer<A> {

    private boolean notified = false;
    private A performance;

    public AssertableConsumerMock(Class<A> acceptedClazz) {
        super(acceptedClazz);
    }

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
