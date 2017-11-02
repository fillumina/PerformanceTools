package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the statistics relative to a specific test.
 *
 * @author Francesco Illuminati
 */
public class SingleStats implements TNamed, Serializable {
    private static final long serialVersionUID = 1L;

    private final TName name;
    private final DimensionalMeasure measure;

    public SingleStats(TName name, DimensionalMeasure measure) {
        this.name = name;
        this.measure = measure;
    }

    @Override
    public TName getName() {
        return name;
    }

    public DimensionalMeasure getMeasure() {
        return measure;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.measure);
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
        @SuppressWarnings("unchecked")
        final SingleStats other = (SingleStats) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.measure, other.measure)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name + "= " + measure.toString();
    }
}
