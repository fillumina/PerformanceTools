package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleValueAccumulator implements TNamed {
    private final List<Double> values = new ArrayList<>();
    private DimensionalMeasure measure;
    private TName name;

    void setName(TName name) {
        this.name = name;
    }

    void setMeasure(DimensionalMeasure measure) {
        this.measure = measure;
    }

    void addValue(double value) {
        values.add(value);
    }

    public List<Double> getValues() {
        return Collections.unmodifiableList(values);
    }

    public DimensionalMeasure getMeasure() {
        return measure;
    }

    @Override
    public TName getName() {
        return name;
    }
}
