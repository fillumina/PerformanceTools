package com.fillumina.performance.util;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Allows to select an option based on auto-calculated rank (the highest win).
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Selectable<T> {

    int selectableRank(T t);

    static <T, S extends Selectable<T>> S select(T t, S... selectables) {
        TreeMap<Integer, S> map = new TreeMap<>();
        for (S s : selectables) {
            map.put(s.selectableRank(t), s);
        }
        return map.lastEntry().getValue();
    }

    static <T, S extends Selectable<T>> S select(T t, Collection<S> selectables) {
        TreeMap<Integer, S> map = new TreeMap<>();
        selectables.forEach((s) -> {
            map.put(s.selectableRank(t), s);
        });
        return map.lastEntry().getValue();
    }

}
