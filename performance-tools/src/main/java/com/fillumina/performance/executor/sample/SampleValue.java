package com.fillumina.performance.executor.sample;

import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.TableProducer;
import com.fillumina.performance.util.collection.LinkedMap;
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
        implements TNamed, CsvProducer, TableProducer, Comparable<SampleValue> {

    private final TName name;
    private final Quantity<?> quantity;

    public SampleValue(TName name, double value, Unit<?> unit) {
        this.name = name;
        this.quantity = Quantity.from(value, unit);
    }

    public SampleValue(TName name, Quantity<?> quantity) {
        this.name = name;
        this.quantity = quantity;
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

    @Override
    public int compareTo(SampleValue o) {
        return Double.compare(getValue(), o.getValue());
    }

    @Override
    public Map<String, String> toTable() {
        return createTable("name", getName(), "value", getValue());
    }

    protected LinkedMap<String, String> createTable(Object... values) {
        LinkedMap<String,String> map = new LinkedMap<>();
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
}
