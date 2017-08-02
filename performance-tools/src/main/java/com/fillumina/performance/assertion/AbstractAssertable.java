package com.fillumina.performance.assertion;

import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertable implements Assertable {

    private Measure refMeasure;
    private int refIndex;
    private TName refName;

    protected Measure getReferenceTestMeasure() {
        if (refMeasure == null) {
            calculateBiggerMeasure();
        }
        return refMeasure;
    }

    protected int getReferenceTestIndex() {
        if (refIndex == -1) {
            calculateBiggerMeasure();
        }
        return refIndex;
    }

    @Override
    public TName getReferenceTestName() {
        if (refName == null) {
            calculateBiggerMeasure();
        }
        return refName;
    }

    @Override
    public boolean isEmpty() {
        return getTestNames().isEmpty();
    }

    @Override
    public MeasureRatio getRatioToReferenceTest(
            TName testName,
            Ratio confidence) {
        Measure m = getMeasure(testName);
        if (m == null) {
            throw new TestNotFoundException(testName);
        }
        return new MeasureRatio(m, getReferenceTestMeasure(), confidence);
    }

    private void calculateBiggerMeasure() {
        TName name = null;
        int index = -1;
        Measure measure = null;

        int i = 0;
        for (TName n : getTestNames()) {
            Measure m = getMeasure(n);
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

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName()).append('{');
        Collection<TName> names = getTestNames();
        if (!names.isEmpty()) {
            final TName slowest = getReferenceTestName();
            append(buf, slowest);
            if (names.size() > 1) {
                buf.append(", ");
            }
            for (TName n : names) {
                if (!n.equals(slowest)) {
                    append(buf, n);
                }
            }
        }
        buf.append('}');
        return buf.toString();
    }

    private void append(StringBuilder buf, TName name) {
        buf.append(name.toString())
                .append(": ")
                .append(getMeasure(name));
    }
}
