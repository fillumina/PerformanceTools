package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class RatioAgainstBiggerMeasureCalculator
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final AssertableExperiment assertable;
    private final DimensionalMeasure refMeasure;
    private final int refIndex;
    private final CharSequence refName;

    public RatioAgainstBiggerMeasureCalculator(AssertableExperiment assertable) {
        this.assertable = assertable;
        CharSequence name = null;
        int index = -1;
        DimensionalMeasure measure = null;
        double mean = Double.NEGATIVE_INFINITY;

        int i = 0;
        for (CharSequence n : assertable.getNames()) {
            DimensionalMeasure dm = assertable.getMeasure(n);
            double m = dm.getUnit().convertToBase(dm.getMean());
            if (measure == null || mean < m) {
                name = n;
                index = i;
                measure = dm;
                mean = m;
            }
            i++;
        }
        this.refName = name;
        this.refIndex = index;
        this.refMeasure = measure;
    }

    public MeasureRatio getRatio(CharSequence name, Ratio confidence) {
        DimensionalMeasure measure = assertable.getMeasure(name);
        Unit<?> unit = measure.getUnit();
        Unit<?> bestUnit = unit.bestUnit(measure.getMean(), refMeasure.getMean());
        return new MeasureRatio(measure.in(bestUnit), refMeasure.in(bestUnit), confidence);
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
