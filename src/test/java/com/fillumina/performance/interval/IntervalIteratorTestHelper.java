package com.fillumina.performance.interval;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fra
 */
public class IntervalIteratorTestHelper {

    protected <T> List<T> iterate(final IntervalBuilder<T> builder,
            final T first, final T last, final T step) {
        builder.start(first);
        builder.last(last);
        builder.step(step);

        final List<T> list = new ArrayList<>();
        for (T t: builder.iterable()) {
            list.add(t);
        }
        return list;
    }
}
