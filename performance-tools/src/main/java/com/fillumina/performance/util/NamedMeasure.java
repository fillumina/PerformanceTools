package com.fillumina.performance.util;

import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NamedMeasure implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CharSequence name;
    private final DimensionalMeasure measure;

    public NamedMeasure(CharSequence name, DimensionalMeasure measure) {
        this.name = name;
        this.measure = measure;
    }

    public CharSequence getName() {
        return name;
    }

    public DimensionalMeasure getMeasure() {
        return measure;
    }

    @Override
    public String toString() {
        return name + " (" + measure + ')';
    }
}
