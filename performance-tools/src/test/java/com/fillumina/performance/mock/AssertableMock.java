package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMock extends AbstractAssertable implements Assertable {

    private final String name;
    private final Map<TName, Measure> map = new ConcurrentHashMap<>();

    /**
     * Use as:
     * {@code
     * AssertableMock am =
     *      AssertableMock.create("title", "test1", 1.0, "test2", 2.0);
     * }
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
        LinkedMap<TName,Measure> map = new LinkedMap<>();
        for (int i=start; i<o.length; i+=2) {
            map.put(TN.tname((String)o[i]), new OnlineMeasure((double) o[i+1]));
        }
        return new AssertableMock(name, map);
    }

    public static AssertableMock createWithTName(TName name, Object... o) {
        int start = (o.length & 1);
        LinkedMap<TName,Measure> map = new LinkedMap<>();
        for (int i=start; i<o.length; i+=2) {
            map.put(name.append((String)o[i]), new OnlineMeasure((double) o[i+1]));
        }
        return new AssertableMock(name.getLastName(), map);
    }

    public AssertableMock() {
        this("unnamed");
    }

    public AssertableMock(String name) {
        this.name = name;
    }

    public AssertableMock(String name, Map<TName, Measure> map) {
        this.name = name;
        this.map.putAll(map);
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<TName> getTestNames() {
        return map.keySet();
    }

    @Override
    public Measure getMeasure(TName testName) {
        return map.get(testName);
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
