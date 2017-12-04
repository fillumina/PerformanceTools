package com.fillumina.performance.executor.sample;

import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.sample.strgen.SampleCsvStringGenerator;
import com.fillumina.performance.executor.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sample
        implements StatsTyped, CsvProducer, Serializable {
    private static final long serialVersionUID = 1L;

    private final StatsType type;
    private final TNameMap<SampleValue> map;

    public Sample(StatsType type, TNameMap<SampleValue> map) {
        this.type = type;
        this.map = map;
    }

    // TODO check for String and TName
    public SampleValue getSampleValue(CharSequence name) {
        return map.get(name);
    }

    @Override
    public StatsType getStatsType() {
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
        return testSample.getQuantity().toBase();
    }

    public List<TName> getTestNames() {
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
