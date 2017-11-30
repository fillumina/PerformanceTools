package com.fillumina.performance.executor.sample;

import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.TableProducer;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.Unit;
import com.fillumina.performance.util.unit.Units;
import java.util.Map;
import java.util.Objects;

/**
 * Holds the iteration performance sample value for a single test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleValue
        implements TNamed, CsvProducer, TableProducer, Comparable<SampleValue> {

    private final TName name;
    private final Quantity<?> quantity;
    private final String type;
    private final long iterations;
    private final long timeNs;

    public SampleValue(TName name, double value, Unit<?> unit) {
        this(name, Quantity.from(value, unit));
    }

    public SampleValue(TName name, Quantity<?> quantity) {
        this(name, quantity, "type", 1, 0);
    }

    public SampleValue(TName name,
            double value, Unit<?> unit,
            String type,
            long iterations,
            long timeNs) {
        this(name, Quantity.from(value, unit), type, iterations, timeNs);
    }

    public SampleValue(TName name,
            Quantity<?> quantity,
            String type,
            long iterations,
            long timeNs) {
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.iterations = iterations;
        this.timeNs = timeNs;
    }

    @Override
    public TName getName() {
        return name;
    }

    public double getValue() {
        return quantity.getValue();
    }

    public Unit<?> getUnit() {
        return quantity.getUnit();
    }

    public Quantity<?> getQuantity() {
        return quantity;
    }

    public long getIterations() {
        return iterations;
    }

    public long getTimeNs() {
        return timeNs;
    }

    @Override
    public int compareTo(SampleValue o) {
        return Double.compare(getValue(), o.getValue());
    }

    @Override
    public Map<String, String> toTable() {
        return createTable(
                "name", getName(),
                "iterations", iterations,
                "timeNs", timeNs,
                type, Units.toString(getValue(), 3, getUnit()));

        //return createTable("name", getName(), "value", getValue());
    }

    protected ArrayMap<String, String> createTable(Object... values) {
        ArrayMap<String,String> map = new ArrayMap<>();
        for (int i=0,l=values.length; i<l; i+=2) {
            String key = Objects.toString(values[i]);
            String value = Objects.toString(values[i+1]);
            map.put(key,value);
        }
        return map;
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
                    .append(e.getValue());
        }
        return buf.toString();
    }

    @Override
    public String toCsv() {
        CsvFormatter csv = new CsvFormatter();
        for (String s : toTable().values()) {
            csv.append(s);
        }
        return csv.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.quantity);
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + (int) (this.iterations ^ (this.iterations >>> 32));
        hash = 97 * hash + (int) (this.timeNs ^ (this.timeNs >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SampleValue other = (SampleValue) obj;
        if (this.iterations != other.iterations) {
            return false;
        }
        if (this.timeNs != other.timeNs) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.quantity, other.quantity)) {
            return false;
        }
        return true;
    }
}
