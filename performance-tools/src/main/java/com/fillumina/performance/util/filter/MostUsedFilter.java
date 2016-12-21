package com.fillumina.performance.util.filter;

import com.fillumina.performance.util.MostUsedValueBag;
import java.util.AbstractList;
import java.util.List;

/**
 * Filter the given list leaving only the most used elements.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MostUsedFilter<T> implements ListFilter<T, Double> {

    @Override
    public List<T> filter(List<T> list, ValueExtractor<T, Double> extractor) {
        MostUsedValueBag<T> bag = new MostUsedValueBag<>();
        for (T t : list) {
            bag.add(t);
        }
        return new SameList<>(bag.getMostUsedValue(),
                bag.getMostUsedValueFrequency());
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
}
