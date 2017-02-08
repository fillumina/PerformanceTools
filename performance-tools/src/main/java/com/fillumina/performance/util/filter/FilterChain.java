package com.fillumina.performance.util.filter;

import java.util.List;

/**
 * Chains several {@link ListFilter}s.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FilterChain<T,V> implements ListFilter<T,V> {
    private final int minSize;
    private final ListFilter<T,V>[] filters;

    public FilterChain(int minSize, ListFilter<T,V>... filters) {
        this.minSize = minSize;
        this.filters = filters;
    }

    @Override
    public List<T> filter(List<T> list, ValueExtractor<T, V> extractor) {
        List<T> result = list;
        for (ListFilter<T,V> filter : filters) {
            if (result.size() > minSize) {
                result = filter.filter(result, extractor);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("FilterChain{");
        for (ListFilter<T,V> f : filters) {
            buf.append(f.toString());
        }
        buf.append('}');
        return buf.toString();
    }
}
