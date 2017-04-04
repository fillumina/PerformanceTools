package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.collection.LinkedMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableMock implements Assertable {

    private final String name;
    private final Map<String, Measure> map = new ConcurrentHashMap<>();

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
        LinkedMap<String,Measure> map = new LinkedMap<>();
        for (int i=start; i<o.length; i+=2) {
            map.put((String)o[i], new OnlineMeasure((double) o[i+1]));
        }
        return new AssertableMock(name, map);
    }

    public AssertableMock() {
        this("unnamed");
    }

    public AssertableMock(String name) {
        this.name = name;
    }

    public AssertableMock(String name, Map<String, Measure> map) {
        this.name = name;
        this.map.putAll(map);
    }

    public String getName() {
        return name;
    }

    @Override
    public Measure getValue(String testName) {
        return map.get(testName);
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName,
            Ratio confidence) {
        Measure slower = new OnlineMeasure(0);
        for (Measure m : map.values()) {
            ConfidenceInterval mci = m.getConfidenceInterval(confidence);
            ConfidenceInterval sci = slower.getConfidenceInterval(confidence);
            if (mci.compareTo(sci) == 1) {
                slower = m;
            }
        }
        Measure measure = map.get(testName);
        if (measure == null) {
            throw new IllegalStateException("cannot find test '" + testName +
                    "'");
        }
        return new MeasureRatio(measure, slower, confidence);
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
        return "AssertableImpl{" + "name=" + name + ", map=" + map + '}';
    }

}
