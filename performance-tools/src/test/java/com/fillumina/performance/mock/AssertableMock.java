package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.DefaultAssertableExperiment;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMock extends DefaultAssertableExperiment {

    private final String name;

    /**
     * Use as:
     * {@code
        AssertableMock am =
             AssertableMock.create("test1", 1.0, "test2", 2.0);
        }
     *
     * @param name test name (optional)
     * @param o is an array of pairs where:
     * <ol>
     * <li>test name (String)
     * <li>measure (double)
     * </ol>
     */
    public static AssertableMock create(Object... o) {
        return createWithName(((o.length & 1) == 1) ? (String) o[0] : "test", o);
    }

    public static AssertableMock createWithName(String name, Object... o) {
        return createWithNameAndUnit(name, Absolute.UNIT, o);
    }

    public static AssertableMock createWithUnit(Unit<?> unit,
            Object... o) {
        return createWithNameAndUnit("test", unit, o);
    }

    public static AssertableMock createWithNameAndUnit(String name, Unit<?> unit,
            Object... o) {
        int start = (o.length & 1); // return 1 if unpair
        IndexedHashMap<CharSequence,DimensionalMeasure> map = new IndexedHashMap<>();
        for (int i=start; i<o.length; i+=2) {
            double value = Double.valueOf(o[i+1].toString());
            map.put((CharSequence)o[i], new OnlineDimensionalMeasure(unit, value));
        }
        return new AssertableMock(name, map, unit);
    }

    public AssertableMock() {
        this("unnamed");
    }

    public AssertableMock(String name) {
        this(name, Collections.<CharSequence,DimensionalMeasure>emptyMap(), Absolute.UNIT);
    }

    public AssertableMock(String name, Map<CharSequence, DimensionalMeasure> map) {
        this(name, map, Absolute.UNIT);
    }

    public AssertableMock(String name, Map<CharSequence, DimensionalMeasure> map,
            Unit<?> unit) {
        super(map);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
