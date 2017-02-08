package com.fillumina.performance.util.filter;

import java.util.List;

/**
 * Filters element of a {@link List}.
 *
 * @param T the type of the {@link List}
 * @param V the type which would be used for filtering
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface ListFilter<T,V> {

    /**
     *
     * @param list      the input list (should not be modified)
     * @param extractor extracts a value used by the filter from T.
     * @return
     */
    List<T> filter(List<T> list, ValueExtractor<T, V> extractor);
}
