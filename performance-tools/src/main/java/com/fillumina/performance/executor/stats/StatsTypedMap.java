package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.collection.IndexedHashMap;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsTypedMap<T extends StatsTyped>
        extends IndexedHashMap<StatsType,T> {
    private static final long serialVersionUID = 1L;

    public StatsTypedMap() {
    }

    public StatsTypedMap(int size) {
        super(size);
    }

    public StatsTypedMap(StatsTypedMap<T> copy) {
        super(copy);
    }

    private StatsTypedMap(List<T> list) {
        super();
    }

    public StatsTypedMap<T> add(T t) {
        put(t.getStatsType(), t);
        return this;
    }

    public T put(T t) {
        return put(t.getStatsType(), t);
    }
}
