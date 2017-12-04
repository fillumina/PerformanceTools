package com.fillumina.performance.time.sample;

import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Quantity;
import java.io.Serializable;
import java.util.Map;

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
    public String toStringValue() {
        return Long.toString(iterations);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String,String> e : toTable().entrySet()) {
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append(e.getKey())
                    .append(": ")
                    .append(timeNs)
                    .append(" (")
                    .append(iterations)
                    .append(")");
        }
        return buf.toString();
    }

    @Override
    public Map<String, String> toTable() {
        return createTable(
                "name", getName(),
                "iterations", iterations,
                "timeNs", timeNs,
                type, getQuantity().toString());
    }
}

