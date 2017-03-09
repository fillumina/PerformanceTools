package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.stats.ConfidenceInterval;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO make it to source pkg
public class AssertableImpl implements Assertable {

    private final String name;
    private final Map<String, Measure> map = new ConcurrentHashMap<>();

    public AssertableImpl() {
        this("unnamed");
    }

    public AssertableImpl(String name) {
        this.name = name;
    }

    public AssertableImpl(String name, Map<String, OnlineMeasure> map) {
        this.name = name;
        this.map.putAll(map);
    }

    /**
     *
     * @param name test name
     * @param o is an array of pairs where:
     * <ol>
     * <li>test name (String)
     * <li>measure (double)
     * </ol>
     */
    public AssertableImpl(Object... o) {
        this.name = "test";
        for (int i=0; i<o.length; i+=2) {
            map.put((String)o[i], new OnlineMeasure((double)o[i+1]));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public Measure getValue(String testName) {
        return map.get(testName);
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        Measure slower = new OnlineMeasure(0);
        for (Measure m : map.values()) {
            ConfidenceInterval mci = m.getConfidenceInterval(Ratio.P_99);
            ConfidenceInterval sci = slower.getConfidenceInterval(Ratio.P_99);
            if (mci.compareTo(sci) == 1) {
                slower = m;
            }
        }
        Measure measure = map.get(testName);
        if (measure == null) {
            throw new IllegalStateException("cannot find test '" + testName +
                    "'");
        }
        return new MeasureRatio(measure, slower, Ratio.P_99);
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
        final AssertableImpl other = (AssertableImpl) obj;
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
