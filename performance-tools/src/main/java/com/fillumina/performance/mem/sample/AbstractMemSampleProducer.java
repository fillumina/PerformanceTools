package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.StatsType;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.pathname.PathNamedMap;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMemSampleProducer
        extends AbstractSampleProducer<AbstractMemSampleProducer>
        implements StatsTyped, MemSampleProducer<AbstractMemSampleProducer> {

    static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    @Override
    public Map<StatsType, Sample> executeWithIterations(int... iterations) {
        Sample sample = getSampleWithIterations(iterations);
        return wrapIntoSingletonMap(sample);
    }

    @Override
    public Map<StatsType, Sample> get() {
        Sample sample = getSampleWithIterations();
        return wrapIntoSingletonMap(sample);
    }

    public Sample getSample() {
        return getSampleWithIterations();
    }

    public Sample getSampleWithIterations(int... iterations) {
        PathNamedMap<SampleValue> map = new PathNamedMap<>(getTests().size());
        int index = 0;
        for (Map.Entry<PathName,Runnable> e : getTests()) {
            PathName name = e.getKey();
            Runnable test = e.getValue();

            long mem = 0;

            int it = getIterations(iterations, index);
            for (int i=0,l=it; i<l; i++) {
                long zero = execute(new LfsrRunnable());
                mem += execute(test) - zero;
            }
            double memory = mem * 1.0 / it;

            map.add(new SampleValue(name, memory, MemUnit.B));
            index++;
        }
        Sample sample = new Sample(getStatsType(), map);
        return sample;
    }

    private int getIterations(int[] iterations, int index) {
        if (iterations == null ||
                iterations.length != getTests().size()) {
            return 1;
        }
        int result = iterations[index];
        if (result < 1) {
            throw new RuntimeException("invalid iteraion number: " + result);
        }
        return result;
    }

    private Map<StatsType, Sample> wrapIntoSingletonMap(Sample sample) {
        return Collections.singletonMap(getStatsType(), sample);
    }
}
