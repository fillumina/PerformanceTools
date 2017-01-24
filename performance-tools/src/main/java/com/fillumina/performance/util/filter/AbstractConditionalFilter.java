package com.fillumina.performance.util.filter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractConditionalFilter<T, V>
        implements ListFilter<T, V> {

    /** @return true if the value is accepted. */
    protected abstract boolean acceptCondition(V value);

    @Override
    public List<T> filter(List<T> coll,
            ValueExtractor<T, V> extractor) {
        int size = coll.size();
        List<T> result = new ArrayList<>(size);
        for (int i=size-1; i>=0; i--) {
            T t = coll.get(i);
            V value = extractor.getValue(t);
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