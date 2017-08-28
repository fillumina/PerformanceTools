package com.fillumina.performance.time.sample;

import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleValue extends SampleValue {

    private final long iterations;
    private final long timeNs;

    public TimeSampleValue(TName name, double value, Unit unit,
            long iterations, long timeNs) {
        super(name, value, unit);
        this.iterations = iterations;
        this.timeNs = timeNs;
    }

    public long getIterations() {
        return iterations;
    }

    public long getTimeNs() {
        return timeNs;
    }
}
