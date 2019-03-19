package com.fillumina.performance.mock;

import java.util.function.Consumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NotifiableConsumerMock<T> implements Consumer<T> {

    private boolean notified = false;
    private T message;

    @Override
    public void accept(T message) {
        this.message = message;
        notified = true;
    }

    public boolean isNotified() {
        return notified;
    }

    public T getReceivedMessage() {
        return message;
    }
}
