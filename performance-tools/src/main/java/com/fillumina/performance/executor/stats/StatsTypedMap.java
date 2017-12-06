package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.collection.AbstractMapListWrapper;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsTypedMap<T extends StatsTyped>
        extends AbstractMapListWrapper<StatsTypedMap<T>, StatsType, T> {
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
        super(list);
    }

    @Override
    protected StatsType getKeyFromValue(T value) {
        return value.getStatsType();
    }

    @Override
    protected StatsTypedMap<T> createNew(List<T> list) {
        return new StatsTypedMap<>(list);
    }
}
