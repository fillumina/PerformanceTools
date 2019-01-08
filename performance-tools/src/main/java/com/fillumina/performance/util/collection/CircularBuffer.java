package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CircularBuffer<T> extends AbstractList<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final T[] data;
    private int index;
    private int size;

    @SuppressWarnings(value = "unchecked")
    public CircularBuffer(int size) {
        data = (T[]) new Object[size];
    }

    public T putAndGetOlder(T value) {
        T result = getOlderInserted();
        add(value);
        return result;
    }

    public T getLastInserted() {
        final int length = data.length;
        return data[(index + length - 1) % length];
    }

    public T getOlderInserted() {
        final int length = data.length;
        if (size < length) {
            return data[0];
        }
        return data[index];
    }

    @Override
    public boolean add(T value) {
        data[index] = value;
        final int length = data.length;
        index = (index + 1) % length;
        if (size < length) {
            size++;
        }
        return true;
    }

    public boolean isFull() {
        return size == data.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int i) {
        T r;
        if (isFull()) {
            r = data[(index + i) % data.length];
        } else {
            r = data[i];
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("History{size=").append(size).append(", ");
        forEach((p) -> buf.append(p.toString()).append(", "));
        buf.append('}');
        return buf.toString();
    }

}
