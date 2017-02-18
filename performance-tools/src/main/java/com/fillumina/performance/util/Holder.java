package com.fillumina.performance.util;

/**
 * Used to pass values out of an inner class.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Holder<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
