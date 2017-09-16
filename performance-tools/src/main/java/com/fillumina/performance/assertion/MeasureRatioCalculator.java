package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureRatioCalculator
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Assertable assertable;
    private final Measure refMeasure;
    private final int refIndex;
    private final CharSequence refName;

    public MeasureRatioCalculator(Assertable assertable) {
        this.assertable = assertable;
        CharSequence name = null;
        int index = -1;
        Measure measure = null;

        int i = 0;
        for (CharSequence n : assertable.getNames()) {
            Measure m = assertable.getMeasure(n);
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

    public MeasureRatio getRatio(CharSequence name, Ratio confidence) {
        Measure measure = assertable.getMeasure(name);
        return new MeasureRatio(measure, refMeasure, confidence);
    }

    public Measure getReferenceTestMeasure() {
        return refMeasure;
    }

    public int getReferenceTestIndex() {
        return refIndex;
    }

    public CharSequence getReferenceTestName() {
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
