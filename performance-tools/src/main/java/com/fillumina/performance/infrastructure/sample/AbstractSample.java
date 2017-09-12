package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.infrastructure.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.infrastructure.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.AbsoluteUnit;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractSample<
                    I extends AbstractSample<I,V,S>,
                    V extends SampleValue,
                    S extends Stats<?>>
        implements Assertable, CsvProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private final TNameMap<V> map;

    public AbstractSample(TNameMap<V> map) {
        this.map = map;
    }

    public abstract StatsBuilder<S,I> getStatsBuilder();

    public Unit getUnit() {
        return AbsoluteUnit.UNIT;
    }

    public V getTestSample(CharSequence name) {
        return map.get(name);
    }

    public TNameMap<V> getValuesMap() {
        return map.unmodifiable();
    }

    public double getValue(CharSequence testName) {
        V testSample = getTestSample(testName);
        if (testSample == null) {
            throw new TestNotFoundException(testName, getTestNames());
        }
        return testSample.getValue();
    }

    @Override
    public Collection<? extends CharSequence> getTestNames() {
        return map.keyList();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Measure getMeasure(CharSequence testName) {
        double value = getValue(testName);
        return new DimensionalOnlineMeasure(getUnit(), value);
    }

    @Override
    public CharSequence getReferenceTestName() {
        V maxValue = null;
        for (V v : map.values()) {
            if (maxValue == null || v.compareTo(maxValue) < 1) {
                maxValue = v;
            }
        }
        return maxValue.getName();
    }

    @Override
    public String toCsv() {
        return SampleCsvStringGenerator.INSTANCE.toString(this);
    }

    @Override
    public String toString() {
        return SampleLineStringGenerator.INSTANCE.toString(this);
    }
}
