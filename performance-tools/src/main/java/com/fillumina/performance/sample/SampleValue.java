package com.fillumina.performance.sample;

import com.fillumina.performance.util.CsvProducer;
import com.fillumina.performance.util.TableProducer;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.formatter.CsvFormatter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.Unit;
import java.util.Map;

/**
 * Holds the iteration performance sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleValue
        implements TNamed, CsvProducer, TableProducer, Comparable<SampleValue> {

    private final TName name;
    private final double value;
    private final Unit unit;

    public SampleValue(TName name, double value, Unit unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public TName getName() {
        return name;
    }

    public double getValue() {
        return unit.convertToBase(value);
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public int compareTo(SampleValue o) {
        return Double.compare(getValue(), o.getValue());
    }

    @Override
    public Map<String, String> toTable() {
        return LinkedMap.create(
                "name", getName(),
                "value", getValue());
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
