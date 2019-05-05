package com.fillumina.performance.assertion;

import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultAssertableExperiment implements AssertableExperiment {

    private final Map<CharSequence, DimensionalMeasure> map =
            new LinkedHashMap<>();

    public DefaultAssertableExperiment add(String name, double... votes) {
        map.put(name, DimensionalMeasure.of(votes));
        return this;
    }

    @Override
    public Collection<? extends CharSequence> getNames() {
        return map.keySet();
    }

    @Override
    public DimensionalMeasure getMeasure(CharSequence name)
            throws NoSuchElementException {
        return map.get(name.toString());
    }

    @Override
    public String toString() {
        return "DefaultAssertableExperiment{" + map + '}';
    }
}
