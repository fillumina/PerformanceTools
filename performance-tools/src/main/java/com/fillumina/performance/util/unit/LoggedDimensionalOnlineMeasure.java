package com.fillumina.performance.util.unit;

import com.fillumina.performance.util.stats.Measure;
import java.util.Collection;

/**
 * A dimensional measure able to provide some logging about how
 * it is calculated.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LoggedDimensionalOnlineMeasure extends DimensionalOnlineMeasure {
    private static final long serialVersionUID = 1L;

    private final StringBuilder buf = new StringBuilder();

    public LoggedDimensionalOnlineMeasure() {
    }

    public LoggedDimensionalOnlineMeasure(double... values) {
        super(values);
    }

    public LoggedDimensionalOnlineMeasure(
            Collection<? extends Number> collection) {
        super(collection);
    }

    public LoggedDimensionalOnlineMeasure(Measure other) {
        super(other);
    }

    public LoggedDimensionalOnlineMeasure(Unit unit) {
        super(unit);
    }

    public LoggedDimensionalOnlineMeasure(Unit unit, double... values) {
        super(unit, values);
    }

    public LoggedDimensionalOnlineMeasure(Unit unit,
            Collection<? extends Number> collection) {
        super(unit, collection);
    }

    public LoggedDimensionalOnlineMeasure(Unit unit, Measure other) {
        super(unit, other);
    }

    public LoggedDimensionalOnlineMeasure(DimensionalMeasure other) {
        super(other);
    }

    public void log(String msg) {
        buf.append(msg).append(System.lineSeparator());
    }

    public String getLogMessages() {
        return buf.toString();
    }
}
