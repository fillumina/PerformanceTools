package com.fillumina.performance.util.filter;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FilterListSizeSelector<V> implements ListFilter<V> {
    private final ListFilter<V> first;
    private final ListFilter<V> second;
    private final Predicate<Integer> condition;

    public FilterListSizeSelector(ListFilter<V> first, ListFilter<V> second,
            Predicate<Integer> conditionOverListSize) {
        this.condition = conditionOverListSize;
        this.first = first;
        this.second = second;
    }

    @Override
    public <T> List<T> filter(List<T> list, Function<T, V> extractor) {
        if (condition.test(list.size())) {
            return first.filter(list, extractor);
        } else {
            return second.filter(list, extractor);
        }
    }

    @Override
    public String toString() {
        return "FilterListSizeSelector{" + first.toString() + " vs " +
                second.toString() + '}';
    }
}
