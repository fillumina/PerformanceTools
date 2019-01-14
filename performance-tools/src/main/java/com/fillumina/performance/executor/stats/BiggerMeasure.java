package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class BiggerMeasure implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Predicate<String> ACCEPTS_ALL = name -> false;

    private final Measure refMeasure;
    private final int refIndex;
    private final CharSequence refName;

    public BiggerMeasure(Map<? extends CharSequence, ? extends Measure> map) {
        this(map, ACCEPTS_ALL);
    }

    public BiggerMeasure(Map<? extends CharSequence, ? extends Measure> map,
            Predicate<String> filter) {
        Optional<? extends Map.Entry<? extends CharSequence, ? extends Measure>> result =
                map.entrySet().stream()
                        .filter(e -> !filter.test(e.getKey().toString()))
                        .max((e1, e2) -> {
            return Double.compare(e1.getValue().getMean(), e2.getValue().getMean());
        });

        if (!result.isPresent()) {
            this.refName = null;
            this.refMeasure = null;
            this.refIndex = -1;
        } else {
            Map.Entry<? extends CharSequence, ? extends Measure> entry = result.get();
            this.refName = entry.getKey();
            this.refMeasure = entry.getValue();
            this.refIndex = findIndex(map, refName);
        }
    }

    public Measure getMeasure() {
        return refMeasure;
    }

    public int getIndex() {
        return refIndex;
    }

    public CharSequence getName() {
        return refName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{name=" + refName +
                ", index=" + refIndex +
                ", measure=" + refMeasure +
                "}";
    }

    private int findIndex(
            Map<? extends CharSequence, ? extends Measure> map,
            CharSequence refName) {
        int index = 0;
        for (CharSequence k : map.keySet()) {
            if (refName.equals(k)) {
                return index;
            }
            index++;
        }
        throw new AssertionError("should really not happen");
    }
}
