package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.ImmutableDimensionalMeasure;
import com.fillumina.performance.util.unit.Unit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultAssertableExperiment
        extends LinkedHashMap<CharSequence, DimensionalMeasure>
        implements AssertableExperiment {
    private static final long serialVersionUID = 1L;

    public static DefaultAssertableExperiment create(Object ... values) {
        return create(Absolute.UNIT, values);
    }

    public static DefaultAssertableExperiment create(Unit<?> unit,
            Object ... values) {
        DefaultAssertableExperiment dae = new DefaultAssertableExperiment();
        for (int i=0; i<values.length; i+=2) {
            dae.add(values[i].toString(), unit, Double.valueOf(values[i+1].toString()));
        }
        return dae;
    }

    public DefaultAssertableExperiment() {
        super();
    }

    public DefaultAssertableExperiment(
            Map<CharSequence, DimensionalMeasure> map) {
        super(map);
    }

    public DefaultAssertableExperiment add(
            CharSequence name, double... values) {
        put(name, DimensionalMeasure.of(Absolute.UNIT, values));
        return this;
    }

    public DefaultAssertableExperiment add(
            CharSequence name, Unit<?> unit, double... values) {
        put(name, DimensionalMeasure.of(unit, values));
        return this;
    }

    public DefaultAssertableExperiment add(CharSequence name,
            DimensionalMeasure measure) {
        put(name, measure);
        return this;
    }

    public DefaultAssertableExperiment add(CharSequence name,
            Measure measure) {
        return add(name, new ImmutableDimensionalMeasure(measure, Absolute.UNIT));
    }

    public DefaultAssertableExperiment add(CharSequence name,
            Unit<?> unit, Measure measure) {
        return add(name, new ImmutableDimensionalMeasure(measure, unit));
    }

    @Override
    public Collection<? extends CharSequence> getNames() {
        return keySet();
    }

    @Override
    public DimensionalMeasure getMeasure(CharSequence name)
            throws NoSuchElementException {
        final DimensionalMeasure measure = get(name);
        if (measure == null) {
            throw new MeasureNotFoundException(name.toString(), getNames());
        }
        return measure;
    }
}
