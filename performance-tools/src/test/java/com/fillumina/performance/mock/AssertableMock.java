package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.util.collection.ArrayMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMock extends AbstractAssertable<AssertableMock>
        implements Assertable {

    private final String name;
    private final Map<CharSequence, Measure> map = new ArrayMap<>();

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
        int start = (o.length & 1);
        String name = (start == 1) ? (String) o[0] : "test";
        ArrayMap<CharSequence,Measure> map = new ArrayMap<>();
        for (int i=start; i<o.length; i+=2) {
            double value = Double.valueOf(o[i+1].toString());
            map.put((CharSequence)o[i], new OnlineMeasure(value));
        }
        return new AssertableMock(name, map);
    }

    public static AssertableMock createWithName(String name, Object... o) {
        int start = (o.length & 1);
        ArrayMap<CharSequence,Measure> map = new ArrayMap<>();
        for (int i=start; i<o.length; i+=2) {
            double value = Double.valueOf(o[i+1].toString());
            map.put((CharSequence)o[i], new OnlineMeasure(value));
        }
        return new AssertableMock(name, map);
    }

    public AssertableMock() {
        this("unnamed");
    }

    public AssertableMock(String name) {
        this.name = name;
    }

    public AssertableMock(String name, Map<CharSequence, Measure> map) {
        this.name = name;
        this.map.putAll(map);
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<CharSequence> getNames() {
        return map.keySet();
    }

    @Override
    public Measure getMeasure(CharSequence testName) {
        String nstr = testName.toString();
        for (Entry<CharSequence,Measure> e : map.entrySet()) {
            CharSequence t = e.getKey();
            if (t.equals(testName) || nstr.equals(t.toString())) {
                return e.getValue();
            }
        }
        throw new MeasureNotFoundException(testName, map.keySet());
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
