package com.fillumina.performance.util.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractConditionalFilter<V>
        implements ListFilter<V> {

    /** @return true if the value is accepted. */
    protected abstract boolean acceptCondition(V value);

    @Override
    public <T> List<T> filter(List<T> coll, Function<T, V> extractor) {
        int size = coll.size();
        List<T> result = new ArrayList<>(size);
        for (int i=size-1; i>=0; i--) {
            T t = coll.get(i);
            V value = extractor.apply(t);
            if (acceptCondition(value)) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}