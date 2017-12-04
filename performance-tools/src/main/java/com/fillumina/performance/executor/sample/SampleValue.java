package com.fillumina.performance.executor.sample;

import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.TableProducer;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.Quantity;
import com.fillumina.performance.util.unit.Unit;
import java.util.Map;
import java.util.Objects;

/**
 * Holds the iteration performance sample value for a single test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleValue
        implements TNamed, CsvProducer, TableProducer {

    private final TName name;
    private final Quantity<?> quantity;

    public SampleValue(TName name, double value, Unit<?> unit) {
        this(name, Quantity.from(value, unit));
    }

    public SampleValue(TName name, Quantity<?> quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public TName getName() {
        return name;
    }

    public Quantity<?> getQuantity() {
        return quantity;
    }

    @Override
    public Map<String, String> toTable() {
        return createTable(
                "name", getName(),
                "value", quantity.toString());
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

    public String toStringValue() {
        return String.format("%.2f", quantity.toBase());
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.quantity, other.quantity)) {
            return false;
        }
        return true;
    }
}
