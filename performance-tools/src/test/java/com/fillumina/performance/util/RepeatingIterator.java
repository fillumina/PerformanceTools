package com.fillumina.performance.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RepeatingIterator<T> implements Iterator<T>, Serializable {
    private static final long serialVersionUID = 1L;
    
    public static <T> Iterable<T> getIterable(final T t) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new RepeatingIterator<T>(t);
            }
        };
    }

    private final T t;

    public RepeatingIterator(final T t) {
        this.t = t;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        return t;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
