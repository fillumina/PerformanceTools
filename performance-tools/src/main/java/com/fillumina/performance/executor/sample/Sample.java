package com.fillumina.performance.executor.sample;

import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sample
        implements StatsTyped, CsvProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private final TNameMap<SampleValue> map;
    private final Stats.Type type;

    public Sample(Stats.Type type, TNameMap<SampleValue> map) {
        this.type = type;
        this.map = map;
    }

    public SampleValue getSampleValue(CharSequence name) {
        return map.get(name);
    }

    @Override
    public Stats.Type getStatsType() {
        return type;
    }

    public TNameMap<SampleValue> getValuesMap() {
        return map.unmodifiable();
    }

    public double getValue(CharSequence testName) {
        SampleValue testSample = getSampleValue(testName);
        if (testSample == null) {
            throw new MeasureNotFoundException(testName, getTestNames());
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
