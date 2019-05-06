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
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultAssertableExperiment implements AssertableExperiment {

    private final Map<CharSequence, DimensionalMeasure> map;

    private Unit<?> unit;

    public static DefaultAssertableExperiment create(Object ... values) {
        DefaultAssertableExperiment dae = new DefaultAssertableExperiment();
        for (int i=0; i<values.length; i+=2) {
            dae.add(values[i].toString(), Double.valueOf(values[i+1].toString()));
        }
        return dae;
    }

    public DefaultAssertableExperiment() {
        this(Absolute.UNIT);
    }

    public DefaultAssertableExperiment(Unit<?> unit) {
        this(unit, new LinkedHashMap<>());
    }

    public DefaultAssertableExperiment(Unit<?> unit,
            Map<CharSequence, DimensionalMeasure> map) {
        this.unit = unit;
        this.map = map;
    }

    public DefaultAssertableExperiment add(CharSequence name, double... values) {
        map.put(name, DimensionalMeasure.of(unit, values));
        return this;
    }

    public DefaultAssertableExperiment add(CharSequence name,
            DimensionalMeasure measure) {
        if (this.unit == null) {
            this.unit = measure.getUnit();
        } else {
            this.unit.assertSameTypeWith(measure.getUnit());
        }
        map.put(name, measure);
        return this;
    }

    public DefaultAssertableExperiment add(CharSequence name, Measure measure) {
        return add(name, new ImmutableDimensionalMeasure(measure, unit));
    }

    @Override
    public Collection<? extends CharSequence> getNames() {
        return map.keySet();
    }

    @Override
    public DimensionalMeasure getMeasure(CharSequence name)
            throws NoSuchElementException {
        final DimensionalMeasure measure = map.get(name);
        if (measure == null) {
            throw new MeasureNotFoundException(name.toString(), getNames());
        }
        return measure;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.map);
        hash = 37 * hash + Objects.hashCode(this.unit);
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
        final DefaultAssertableExperiment other =
                (DefaultAssertableExperiment) obj;
        if (!Objects.equals(this.map, other.map)) {
            return false;
        }
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
