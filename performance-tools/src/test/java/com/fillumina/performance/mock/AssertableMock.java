package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.DefaultDimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMock extends AbstractAssertable<AssertableMock>
        implements AssertableExperiment {

    private final String name;
    private final Map<CharSequence, Measure> map = new IndexedHashMap<>();
    private final Unit<?> unit;

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
        return createWithNameAdUnit(name, Absolute.UNIT, o);
    }

    public static AssertableMock createWithNameAdUnit(String name, Unit<?> unit,
            Object... o) {
        int start = (o.length & 1);
        IndexedHashMap<CharSequence,Measure> map = new IndexedHashMap<>();
        for (int i=start; i<o.length; i+=2) {
            double value = Double.valueOf(o[i+1].toString());
            map.put((CharSequence)o[i], new OnlineMeasure(value));
        }
        return new AssertableMock(name, map, unit);
    }

    public AssertableMock() {
        this("unnamed");
    }

    public AssertableMock(String name) {
        this(name, Collections.<CharSequence,Measure>emptyMap(), Absolute.UNIT);
    }

    public AssertableMock(String name, Map<CharSequence, Measure> map) {
        this(name, map, Absolute.UNIT);
    }

    public AssertableMock(String name, Map<CharSequence, Measure> map,
            Unit<?> unit) {
        this.name = name;
        this.map.putAll(map);
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<CharSequence> getNames() {
        return map.keySet();
    }

    @Override
    public DimensionalMeasure getMeasure(CharSequence testName) {
        String nstr = testName.toString();
        Optional<CharSequence> result = map.keySet().stream()
                    .filter(t -> t.equals(testName) || nstr.equals(t.toString()))
                    .findFirst();
        if (!result.isPresent()) {
            throw new MeasureNotFoundException(testName, map.keySet());
        }
        return new DefaultDimensionalMeasure(map.get(result.get()), unit);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.map);
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
        final AssertableMock other = (AssertableMock) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.map, other.map)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + "= " + map.toString();
    }
}
