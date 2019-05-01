package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.collection.MostUsedValueBag;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

/**
 * Filter the given list leaving only the most used elements.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MostUsedFilter implements ListFilter<Void> {

    public static final MostUsedFilter INSTANCE = new MostUsedFilter();

    @SuppressWarnings("unchecked")
    public static <T> ListFilter<T> instance() {
        return (ListFilter<T>) INSTANCE;
    }

    @Override
    public <T> List<T> filter(List<T> list, Function<T, Void> notUsed) {
        MostUsedValueBag<T> bag = new MostUsedValueBag<>();
        for (T t : list) {
            bag.add(t);
        }
        final int frequency = bag.getMostUsedValueFrequency();
        if (frequency == 1) {
            return list;
        }
        return new SameList<>(bag.getMostUsedValue(), frequency);
    }

    private static class SameList<T> extends AbstractList<T> {

        private final T value;
        private final int size;

        private SameList(T value, int size) {
            this.value = value;
            this.size = size;
        }

        @Override
        public T get(int index) {
            return value;
        }

        @Override
        public int size() {
            return size;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
