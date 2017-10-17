package com.fillumina.performance.util.filter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Chains several {@link ListFilter}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FilterChain<V> implements ListFilter<V> {
    private final int minSize;
    private final ListFilter<V>[] filters;

    @SafeVarargs
    public FilterChain(int minSize, ListFilter<V>... filters) {
        this.minSize = minSize;
        this.filters = filters;
    }

    @Override
    public <T> List<T> filter(List<T> list, Function<T, V> extractor) {
        List<T> result = list;
        for (ListFilter<V> f : filters) {
            if (result.size() > minSize) {
                result = f.filter(result, extractor);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(filters);
    }
}
