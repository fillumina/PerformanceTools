package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Unit;
import com.fillumina.performance.util.unit.Units;

/**
 * Holds the iteration performance sample.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestSample implements TNamed {

    private final TName name;
    private final double value;
    private final Unit unit;

    public TestSample(TName name, double value, Unit unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    @Override
    public TName getTestName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return name + "= " + Units.toString(value, 3, unit);
    }
}
