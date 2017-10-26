package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.IntervalUnit;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Unit;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCreator {

    /** @param array couples of (String name,double values) */
    public static Sample createSample(Object... array) {
        TNameMap<SampleValue> map = createMap(array);
        return new Sample(map);
    }

    public static TNameMap<SampleValue> createMap(Object... array)
            throws NumberFormatException {
        TNameMap<SampleValue> map = new TNameMap<>();
        for (int i=0,l=array.length; i<l; i+=2) {
            String name = (String) array[i];
            double value = Double.valueOf(Objects.toString(array[i+1]));
            SampleValue sampleValue = new SampleValue(
                    TN.tname(name), value, IntervalUnit.MILLISECONDS);
            map.add(sampleValue);
        }
        return map;
    }

    public static Builder<Sample> builder() {
        return new Builder<>(Magnitude.UNIT, map -> new Sample(map));
    }

    public static <S extends AbstractSample<S,SampleValue,?>> Builder<S> builder(
            Function<TNameMap<SampleValue>,S> sampleCreator) {
        return new Builder<>(Magnitude.UNIT, sampleCreator);
    }

    public static class Builder<S extends AbstractSample<S,SampleValue,?>> {
        private final Function<TNameMap<SampleValue>,S> sampleCreator;
        private final TNameMap<SampleValue> map = new TNameMap<>();
        private final Unit<?> unit;

        public Builder(Unit<?> unit,
                Function<TNameMap<SampleValue>,S> sampleCreator) {
            this.unit = unit;
            this.sampleCreator = sampleCreator;
        }

        public Builder<S> add(String name, double value) {
            SampleValue sampleValue =
                    new SampleValue(TN.tname(name), value, unit);
            map.add(sampleValue);
            return this;
        }

        public S buildSample() {
            return sampleCreator.apply(map);
        }

        public TNameMap<SampleValue> getMap() {
            return new TNameMap<>(map);
        }
    }
}
