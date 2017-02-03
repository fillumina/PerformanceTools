package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReturningToCallerImpl<C> implements ReturningToCaller<C> {
    private final C caller;

    public ReturningToCallerImpl(C caller) {
        this.caller = caller;
    }

    @Override
    public C end() {
        return caller;
    }
}
