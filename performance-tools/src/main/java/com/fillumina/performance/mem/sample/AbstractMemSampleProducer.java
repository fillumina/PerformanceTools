package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsTyped;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.util.StopWatch;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
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
    public Map<Stats.Type, Sample> executeWithIterations(int... iterations) {
        Sample sample = getSampleWithIterations(iterations);
        return wrapIntoSingletonMap(sample);
    }

    @Override
    public Map<Stats.Type, Sample> get() {
        Sample sample = getSampleWithIterations();
        return wrapIntoSingletonMap(sample);
    }

    public Sample getSample() {
        return getSampleWithIterations();
    }

    public Sample getSampleWithIterations(int... iterations) {
        TNameMap<SampleValue> map = new TNameMap<>(getTests().size());
        int index = 0;
        StopWatch stopWatch = new StopWatch();
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();

            long mem = 0;

            int it = getIterations(iterations, index);
            stopWatch.start();
            for (int i=0,l=it; i<l; i++) {
                long zero = execute(new LfsrRunnable());
                mem += execute(test) - zero;
            }
            long elapsedNs = stopWatch.stop();
            double memory = mem * 1.0 / it;

            map.put(new SampleValue(name, memory, MemUnit.B,
                    getStatsType().toString(),
                    (long)it, elapsedNs));
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

    private Map<Stats.Type, Sample> wrapIntoSingletonMap(Sample sample) {
        return Collections.singletonMap(getStatsType(), sample);
    }
}
