package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;

/**
 * Holds the iteration performance sample value for a single test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleValue extends SampleValue implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String type;
    private final long iterations;
    private final long timeNs;

    public TimeSampleValue(TName name,
            Quantity<?> quantity,
            String type,
            long iterations,
            long timeNs) {
        super(name, quantity);
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
    public String toCsv() {
        return super.toCsv() + ", " + timeNs + ", " + iterations;
    }

    @Override
    public String toStringValue() {
        return Long.toString(iterations);
    }

    @Override
    public String toString() {
        return super.toString() + " " + type.toString() +
                " (ns=" + timeNs + ", it=" + iterations + ")";
    }
}

