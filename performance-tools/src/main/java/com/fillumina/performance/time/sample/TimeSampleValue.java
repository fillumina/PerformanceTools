package com.fillumina.performance.time.sample;

import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Unit;
import com.fillumina.performance.util.unit.Units;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleValue extends SampleValue {

    private final String type;
    private final long iterations;
    private final long timeNs;

    public TimeSampleValue(TName name, double value, Unit unit,
            String type, long iterations, long timeNs) {
        super(name, value, unit);
        this.type = type;
        this.iterations = iterations;
        this.timeNs = timeNs;
    }

    public long getIterations() {
        return iterations;
    }

    public long getTimeNs() {
        return timeNs;
    }

    @Override
    public Map<String, String> toTable() {
        return LinkedMap.create(
                "name", getName(),
                "iterations", iterations,
                "timeNs", timeNs,
                type, Units.toString(getValue(), 3, getUnit()));
    }
}
