package com.fillumina.performance.mem.sample;

import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.AbstractSampleProducer;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.test.LfsrRunnable;
import com.fillumina.performance.mem.stats.MemStats;
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
            <A extends AbstractSample<A, SampleValue, S>,
             S extends MemStats>
        extends AbstractSampleProducer<AbstractMemSampleProducer<A,S>, A>
        implements MemSampleProducer<AbstractMemSampleProducer<A,S>, A> {

    static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    protected abstract A createSample(TNameMap<SampleValue> map);

    @Override
    public Map<Class<?>, A> executeWithIterations(int... iterations) {
        A sample = getSampleWithIterations(iterations);
        return wrapIntoSingletonMap(sample);
    }

    @Override
    public Map<Class<?>, A> get() {
        A sample = getSampleWithIterations();
        return wrapIntoSingletonMap(sample);
    }

    public A getSample() {
        return getSampleWithIterations();
    }

    public A getSampleWithIterations(int... iterations) {
        TNameMap<SampleValue> map = new TNameMap<>(getTests().size());
        int index = 0;
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();

            long mem = 0;
            int it = getIterations(iterations, index);
            for (int i=0,l=it; i<l; i++) {
                long zero = execute(new LfsrRunnable());
                mem += execute(test) - zero;
            }
            double memory = mem * 1.0 / it;

            map.put(new SampleValue(name, memory, MemUnit.B));
            index++;
        }
        A sample = createSample(map);
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

    private Map<Class<?>, A> wrapIntoSingletonMap(A sample) {
        @SuppressWarnings("unchecked")
                Class<A> clazz = (Class<A>)sample.getClass();
        return Collections.singletonMap(clazz, sample);
    }
}
