package com.fillumina.performance.executor.stats;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

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
    public String toString() {
        return name + "= " + measure.toString();
    }
}
