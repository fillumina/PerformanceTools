package com.fillumina.performance.executor.sample;

import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.executor.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.tname.TNameMap;
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
        implements CsvProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private final TNameMap<V> map;

    public AbstractSample(TNameMap<V> map) {
        this.map = map;
    }

    public abstract StatsBuilder<S,I> getStatsBuilder();

    public V getSampleValue(CharSequence name) {
        return map.get(name);
    }

    public TNameMap<V> getValuesMap() {
        return map.unmodifiable();
    }

    public double getValue(CharSequence testName) {
        V testSample = getSampleValue(testName);
        if (testSample == null) {
            throw new TestNotFoundException(testName, getTestNames());
        }
        return testSample.getValue();
    }

    public Collection<? extends CharSequence> getTestNames() {
        return map.keyList();
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
