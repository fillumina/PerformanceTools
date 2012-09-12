package com.fillumina.performance.util.interval;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fra
 */
public class IntervalIteratorTestHelper {

    protected <T> List<T> getList(final IntervalBuilder<T> builder) {
        final List<T> list = new ArrayList<>();
        for (T t: builder.iterable()) {
            list.add(t);
        }
        return list;
    }
}
