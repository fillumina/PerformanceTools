package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractAssertable implements Assertable {

    private Measure slowestMeasure;
    private int slowestIndex;
    private String slowestName;

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
    public String getSlowestTestName() {
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
    public MeasureRatio getRatioWithSlowestTest(String testName,
            Ratio confidence) {
        Measure m = getMeasure(testName);
        if (m == null) {
            throw new IllegalStateException("cannot find test '" + testName +
                    "'");
        }
        return new MeasureRatio(m, getSlowestTestMeasure(), confidence);
    }

    private void calculateSlowestMeasure() {
        String sName = null;
        int sIndex = -1;
        Measure sMeasure = null;
        int index = 0;
        for (String name : getTestNames()) {
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
}
