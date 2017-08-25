package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.stats.SingleStats;
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

    private Measure refMeasure;
    private int refIndex;
    private TName refName;

    public ReferenceMeasure(List<T> list) {
        TName name = null;
        int index = -1;
        Measure measure = null;

        int i = 0;
        for (SingleStats s : list) {
            Measure m = s.getMeasure();
            if (measure == null || measure.getMean() < m.getMean()) {
                name = s.getTestName();
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
        return "{name=" + refName +
                ", index=" + refIndex +
                ", measure=" + refMeasure +
                "}";
    }

}
