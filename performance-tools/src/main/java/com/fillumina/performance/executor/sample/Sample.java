package com.fillumina.performance.executor.sample;

import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.pathname.PathNamedMap;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.Unit;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Contains the measure of one or more tests relative to the same experiment.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sample implements StatsTyped, Serializable {
    private static final long serialVersionUID = 1L;

    private final StatsType type;
    private final PathNamedMap<SampleValue> map;

    public Sample(StatsType type, PathNamedMap<SampleValue> map) {
        this.type = type;
        this.map = map;
    }

    public SampleValue getSampleValue(CharSequence name) {
        return map.get(name);
    }

    @Override
    public StatsType getStatsType() {
        return type;
    }

    public Map<PathName, SampleValue> getValuesMap() {
        return map.unmodifiableView();
    }

    public List<PathName> getTestNames() {
        return map.keyList();
    }

    @SuppressWarnings("unchecked")
    public <U extends Unit<U>> Quantity<U> getQuantity(CharSequence testName)
            throws MeasureNotFoundException {
        SampleValue sampleValue = getSampleValue(testName);
        if (sampleValue == null) {
            throw new MeasureNotFoundException(testName, getTestNames());
        }
        return (Quantity<U>) sampleValue.getQuantity();
    }

    public String toCsv() {
        StringBuilder buf = new StringBuilder();
        buf.append(type.toString());
        map.values().forEach( value -> buf.append(", ").append(value.toCsv()) );
        return buf.toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(type.toString()).append("{");
        boolean first = true;
        for (SampleValue v : map.values()) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(v.toString());
        }
        buf.append("}");
        return buf.toString();
    }
}
