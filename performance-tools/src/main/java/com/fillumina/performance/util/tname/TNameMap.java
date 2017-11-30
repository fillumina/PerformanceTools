package com.fillumina.performance.util.tname;

import com.fillumina.performance.util.collection.ArrayListMap;
import com.fillumina.performance.util.collection.UnmodifiableList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMap<T extends TNamed> extends ArrayListMap<TName, T> {

    private static final Function<TNamed, TName> TNAME_EXTRACTOR =
            t -> t.getName();

    @SuppressWarnings("unchecked")
    private static <T extends TNamed> Function<T,TName> getDefaultExtractor() {
        return (Function<T, TName>) TNAME_EXTRACTOR;
    }

    public TNameMap() {
        super(getDefaultExtractor());
    }

    public TNameMap(int size) {
        super(getDefaultExtractor(), size);
    }

    public TNameMap(List<T> list) {
        super(getDefaultExtractor(), list);
    }

    public TNameMap(Map<TName,T> copy) {
        super(getDefaultExtractor(), copy.size());
        copy.forEach((k,v) -> put(k,v));
    }

    protected TNameMap(List<T> list, Void direct) {
        super(getDefaultExtractor(), list, null);
    }

    /** Equality is defined in terms of equals string representations. */
    public T get(CharSequence testName) {
        String nameStr = testName.toString();
        return findByKey(t -> t.equals(testName) || nameStr.equals(t.toString()));
    }

    @Override
    public TNameMap<T> unmodifiable() {
        return new TNameMap<>(new UnmodifiableList<>(values()), null);
    }

    @Override
    public TNameMap<T> add(T... values) {
        super.add(values);
        return this;
    }
}
