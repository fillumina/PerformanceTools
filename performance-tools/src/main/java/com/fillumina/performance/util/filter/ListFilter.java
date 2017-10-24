package com.fillumina.performance.util.filter;

import java.util.List;
import java.util.function.Function;

/**
 * Filters element of a {@link List}.
 *
 * @param T the type of the {@link List}
 * @param V the type which would be used for filtering
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ListFilter<V> {

    default List<V> filter(List<V> list) {
        return filter(list, Function.identity());
    }

    /**
     *
     * @param list      the input list (should not be modified)
     * @param extractor extracts a value used by the filter from T.
     * @return
     */
    <T> List<T> filter(List<T> list, Function<T, V> extractor);

    ListFilter<?> IDENTITY = new ListFilter<Object>() {
        @Override
        public <T> List<T> filter(List<T> list,
                Function<T, Object> extractor) {
            return list;
        }
    };

    @SuppressWarnings("unchecked")
    static <V> ListFilter<V> identity() {
        return (ListFilter<V>) IDENTITY;
    }
}
