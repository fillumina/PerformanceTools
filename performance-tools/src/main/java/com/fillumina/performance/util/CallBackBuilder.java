package com.fillumina.performance.util;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class CallBackBuilder<C,B> implements Builder<B>, Reentrant<C> {

    public interface Setter<C,B> {
        C setBuiltObjectAndReturn(B builtObject);
    }

    private final Setter<C,B> setter;

    @SuppressWarnings("unchecked")
    public CallBackBuilder() {
        this((Setter<C,B>)null);
    }

    @SuppressWarnings("unchecked")
    public CallBackBuilder(C caller) {
        if (caller == null) {
            this.setter = (builtObject) -> { return (C) builtObject; };
        } else {
            this.setter = (builtObject) -> { return caller; };
        }
    }

    @SuppressWarnings("unchecked")
    public CallBackBuilder(Setter<C, B> setter) {
        if (setter == null) {
            this.setter = (builtObject) -> { return (C) builtObject; };
        } else {
            this.setter = setter;
        }
    }

    @Override
    public C end() {
        return setter.setBuiltObjectAndReturn(build());
    }
}
