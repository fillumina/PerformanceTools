package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.SingleMeasure;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sample<I extends Sample<I,V>, V extends TestSample>
        extends AbstractAssertable<I>
        implements Serializable {
    private static final long serialVersionUID = 1L;

    private final TNameMap<V> map;

    public Sample(TNameMap<V> map) {
        this.map = map;
    }

    public V getTestSample(CharSequence name) {
        return map.get(name);
    }

    @Override
    public Collection<? extends CharSequence> getTestNames() {
        return map.keyList();
    }

    public double getValue(CharSequence testName) {
        V testSample = getTestSample(testName);
        if (testSample == null) {
            throw new TestNotFoundException(testName, getTestNames());
        }
        return testSample.getValue();
    }

    /**
     * Doesn't really make much sense with samples: don't use.
     * @see #getValue(java.lang.CharSequence) 
     */
    @Override
    public Measure getMeasure(CharSequence testName) {
        return new SingleMeasure(getValue(testName));
    }
}
