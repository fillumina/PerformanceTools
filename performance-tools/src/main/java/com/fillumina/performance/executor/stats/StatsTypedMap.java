package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.collection.UnmodifiableList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated
public class StatsTypedMap<T extends StatsTyped>
        extends ArrayMap<Stats.Type, T> {

    private static final Function<StatsTyped, Stats.Type> TNAME_EXTRACTOR =
            t -> t.getStatsType();

    @SuppressWarnings("unchecked")
    private static <T extends StatsTyped> Function<T,Stats.Type>
            getDefaultExtractor() {
        return (Function<T, Stats.Type>) TNAME_EXTRACTOR;
    }

    public StatsTypedMap() {
        super(getDefaultExtractor());
    }

    public StatsTypedMap(int size) {
        super(getDefaultExtractor(), size);
    }

    public StatsTypedMap(List<T> list) {
        super(getDefaultExtractor(), list);
    }

    public StatsTypedMap(Map<Stats.Type,T> copy) {
        super(getDefaultExtractor(), copy.size());
        copy.forEach((k,v) -> put(k,v));
    }

    protected StatsTypedMap(List<T> list, Void direct) {
        super(getDefaultExtractor(), list, null);
    }

    /** Equality is defined in terms of equals string representations. */
    public T get(CharSequence testName) {
        String nameStr = testName.toString();
        return findByKey(t -> t.equals(testName) || nameStr.equals(t.toString()));
    }

    @Override
    public StatsTypedMap<T> unmodifiable() {
        return new StatsTypedMap<>(new UnmodifiableList<>(values()), null);
    }

    @Override
    public StatsTypedMap<T> add(T... values) {
        super.add(values);
        return this;
    }

}
