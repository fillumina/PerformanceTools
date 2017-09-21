package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReferenceMeasure<T extends SingleStats>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Measure refMeasure;
    private final int refIndex;
    private final TName refName;

    public ReferenceMeasure(List<T> list) {
        TName name = null;
        int index = -1;
        Measure measure = null;

        int i = 0;
        for (SingleStats s : list) {
            Measure m = s.getMeasure();
            if (measure == null || measure.getMean() < m.getMean()) {
                name = s.getName();
                index = i;
                measure = m;
            }
            i++;
        }

        this.refName = name;
        this.refIndex = index;
        this.refMeasure = measure;
    }

    public Measure getReferenceTestMeasure() {
        return refMeasure;
    }

    public int getReferenceTestIndex() {
        return refIndex;
    }

    public TName getReferenceTestName() {
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
