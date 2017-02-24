package com.fillumina.performance.util;

/**
 * Implementation of the
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReentrantFluidInterfaceImpl<C>
        implements ReentrantFluidInterface<C> {
    private final C caller;

    public ReentrantFluidInterfaceImpl(C caller) {
        this.caller = caller;
    }

    @Override
    public C end() {
        return caller;
    }
}
