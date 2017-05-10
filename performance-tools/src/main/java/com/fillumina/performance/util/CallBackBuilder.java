package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class CallBackBuilder<C,B> implements Builder<B> {

    public interface Setter<C,B> {
        C setBuiltObjectAndReturn(B builtObject);
    }

    private final Setter<C,B> setter;

    @SuppressWarnings("unchecked")
    public CallBackBuilder() {
        this.setter = (builtObject) -> { return (C) builtObject; };
    }

    public CallBackBuilder(C caller) {
        this.setter = (builtObject) -> { return caller; };
    }

    public CallBackBuilder(Setter<C, B> setter) {
        this.setter = setter;
    }

    public C end() {
        return setter.setBuiltObjectAndReturn(build());
    }
}
