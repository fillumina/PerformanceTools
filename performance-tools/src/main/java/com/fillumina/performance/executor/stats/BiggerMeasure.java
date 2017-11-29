package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.stats.Measure;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class BiggerMeasure implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Measure refMeasure;
    private final int refIndex;
    private final CharSequence refName;

    public BiggerMeasure(Map<? extends CharSequence, ? extends Measure> map) {
        CharSequence name = null;
        int index = -1;
        Measure measure = null;

        // save max measure in list
        int i = 0;
        for (Map.Entry<? extends CharSequence,? extends Measure> e : map.entrySet()) {
            CharSequence n = e.getKey();
            Measure m = e.getValue();
            if (measure == null || measure.getMean() < m.getMean()) {
                name = n;
                index = i;
                measure = m;
            }
            i++;
        }

        this.refName = name;
        this.refIndex = index;
        this.refMeasure = measure;
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

}
