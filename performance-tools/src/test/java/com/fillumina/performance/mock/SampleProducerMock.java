package com.fillumina.performance.mock;

import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleProducerMock
        extends AbstractSampleProducer<SampleProducerMock, SampleMock> {

    private final List<Samples> samples = new ArrayList<>();
    private int index;

    private static class Samples {
        private String name;
        private double[] values;

        public Samples(String name, double[] values) {
            this.name = name;
            this.values = values;
        }
    }

    public SampleProducerMock addSamples(String name, double... values) {
        samples.add(new Samples(name, values));
        return this;
    }

    @Override
    public Map<Class<?>, SampleMock> executeWithIterations(int... iterations) {
        return get();
    }

    @Override
    public Map<Class<?>, SampleMock> get() {
        SampleCreator.Builder<?> builder = SampleCreator.builder();
        for (Samples s : samples) {
            builder.add(s.name, s.values[index % s.values.length]);
        }
        SampleMock sample = new SampleMock(builder.getMap());
        return Collections.singletonMap(SampleMock.class, sample);
    }
}
