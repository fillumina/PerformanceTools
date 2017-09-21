package com.fillumina.performance.mock;

import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NotifiableConsumerMock<T> implements Consumer<T> {

    private boolean notified = false;
    private T performance;

    @Override
    public void accept(T assertable) {
        this.performance = assertable;
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public T getReceivedAssertable() {
        return performance;
    }
}
