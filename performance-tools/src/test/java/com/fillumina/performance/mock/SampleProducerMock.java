package com.fillumina.performance.mock;

import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.stats.Stats;
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

    private final List<Samples> samples = new ArrayList<>();
    private int index;

    public static class Samples {
        private final String name;
        private final double[] values;

        public Samples(String name, double[] values) {
            this.name = name;
            this.values = values;
        }

        public String getName() { return name; }
        public double[] getValues() { return values; }
    }

    public List<Samples> getSamples() {
        return samples;
    }

    public SampleProducerMock addSamples(String name, double... values) {
        samples.add(new Samples(name, values));
        return this;
    }

    @Override
    public Map<Stats.Type, Sample> executeWithIterations(int... iterations) {
        return get();
    }

    @Override
    public Map<Stats.Type, Sample> get() {
        SampleCreator.Builder builder = SampleCreator.builder();
        for (Samples s : samples) {
            builder.add(s.name, s.values[index % s.values.length]);
        }
        index++;
        Sample sample = new Sample(MockStatsType.INSTANCE, builder.getMap());
        dispatchToConsumers(sample);
        return Collections.singletonMap(MockStatsType.INSTANCE, sample);
    }
}
