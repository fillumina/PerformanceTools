package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReentrantImpl<C> implements Reentrant<C> {
    private C caller;

    public ReentrantImpl(C caller) {
        this.caller = caller;
    }

    protected void setCaller(C caller) {
        this.caller = caller;
    }

    protected C getCaller() {
        return caller;
    }

    @Override
    public C end() {
        return caller;
    }
}
