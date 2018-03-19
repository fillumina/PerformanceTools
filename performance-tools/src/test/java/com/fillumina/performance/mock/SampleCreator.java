package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Unit;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCreator {

    /** @param array couples of (String name,double value) */
    public static Sample createSample(Object... array) {
        return createSample(MockStatsType.INSTANCE, Magnitude.UNIT, array);
    }

    /** @param array couples of (String name,double value) */
    public static Sample createSample(StatsType type, Unit<?> unit,
            Object... array) {
        TNameMap<SampleValue> map = createMap(unit, array);
        return new Sample(type, map);
    }

    public static TNameMap<SampleValue> createMap(Unit<?> unit, Object... array)
            throws NumberFormatException {
        TNameMap<SampleValue> map = new TNameMap<>();
        for (int i=0,l=array.length; i<l; i+=2) {
            String name = (String) array[i];
            double value = Double.valueOf(Objects.toString(array[i+1]));
            SampleValue sampleValue = new SampleValue(
                    TN.tname(name), value, unit);
            map.add(sampleValue);
        }
        return map;
    }

    public static Builder builder(Unit<?> unit) {
        return new Builder(unit,
                map -> new Sample(MockStatsType.INSTANCE, map));
    }

    public static Builder builder(Unit<?> unit,
            Function<TNameMap<SampleValue>,Sample> sampleCreator) {
        return new Builder(unit, sampleCreator);
    }

    public static class Builder {
        private final Function<TNameMap<SampleValue>,Sample> sampleCreator;
        private final TNameMap<SampleValue> map = new TNameMap<>();
        private final Unit<?> unit;

        public Builder(Unit<?> unit,
                Function<TNameMap<SampleValue>,Sample> sampleCreator) {
            this.unit = unit;
            this.sampleCreator = sampleCreator;
        }

        public Builder add(CharSequence name, double value) {
            SampleValue sampleValue =
                    new SampleValue(TN.tname(name), value, unit);
            map.add(sampleValue);
            return this;
        }

        public Sample buildSample() {
            return sampleCreator.apply(map);
        }

        public TNameMap<SampleValue> getMap() {
            return new TNameMap<>(map);
        }
    }
}
