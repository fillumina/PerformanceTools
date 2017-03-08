package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
class AssertableImpl implements Assertable {

    private final String name;

    public AssertableImpl() {
        this(null);
    }

    public AssertableImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Measure getValue(String testName) {
        return null;
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.getName());
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
        return true;
    }

    @Override
    public String toString() {
        return "AssertableImpl{" + "name=" + getName() + '}';
    }

}
