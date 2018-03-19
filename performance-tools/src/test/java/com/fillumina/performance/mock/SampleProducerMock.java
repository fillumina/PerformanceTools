package com.fillumina.performance.mock;

import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.util.unit.Unit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleProducerMock
        extends AbstractSampleProducer<SampleProducerMock> {

    private final List<SampleData> samples = new ArrayList<>();
    private final Unit<?> unit;
    private int index;

    public SampleProducerMock(Unit<?> unit) {
        this.unit = unit;
    }

    public static class SampleData {
        private final String name;
        private final double[] values;

        public SampleData(String name, double[] values) {
            this.name = name;
            this.values = values;
        }

        public String getName() { return name; }
        public double[] getValues() { return values; }
    }

    public List<SampleData> getSamples() {
        return samples;
    }

    public SampleProducerMock addSamples(String name, double... values) {
        samples.add(new SampleData(name, values));
        return this;
    }

    @Override
    public Map<StatsType, Sample> executeWithIterations(int... iterations) {
        return get();
    }

    @Override
    public Map<StatsType, Sample> get() {
        SampleCreator.Builder builder = SampleCreator.builder(unit);
        for (SampleData s : samples) {
            builder.add(s.name, s.values[index % s.values.length]);
        }
        index++;
        Sample sample = new Sample(MockStatsType.INSTANCE, builder.getMap());
        dispatchToConsumers(sample);
        return Collections.singletonMap(MockStatsType.INSTANCE, sample);
    }
}
