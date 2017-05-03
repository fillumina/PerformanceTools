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

    private Measure slowestMeasure;
    private int slowestIndex;
    private TName slowestName;

    protected Measure getSlowestTestMeasure() {
        if (slowestMeasure == null) {
            calculateSlowestMeasure();
        }
        return slowestMeasure;
    }

    protected int getSlowestTestIndex() {
        if (slowestIndex == -1) {
            calculateSlowestMeasure();
        }
        return slowestIndex;
    }

    @Override
    public TName getSlowestTestName() {
        if (slowestName == null) {
            calculateSlowestMeasure();
        }
        return slowestName;
    }

    @Override
    public boolean isEmpty() {
        return getTestNames().isEmpty();
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(TName testName,
            Ratio confidence) {
        Measure m = getMeasure(testName);
        if (m == null) {
            throw new IllegalStateException("cannot find test '" + testName +
                    "'");
        }
        return new MeasureRatio(m, getSlowestTestMeasure(), confidence);
    }

    private void calculateSlowestMeasure() {
        TName sName = null;
        int sIndex = -1;
        Measure sMeasure = null;
        int index = 0;
        for (TName name : getTestNames()) {
            Measure m = getMeasure(name);
            if (sMeasure == null || sMeasure.getMean() < m.getMean()) {
                sName = name;
                sIndex = index;
                sMeasure = m;
            }
            index++;
        }
        this.slowestName = sName;
        this.slowestIndex = sIndex;
        this.slowestMeasure = sMeasure;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName()).append('{');
        Collection<TName> names = getTestNames();
        if (!names.isEmpty()) {
            final TName slowest = getSlowestTestName();
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
