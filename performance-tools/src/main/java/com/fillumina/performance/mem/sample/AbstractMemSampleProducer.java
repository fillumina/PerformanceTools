package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.stats.MemStats;
import com.fillumina.performance.sample.AbstractSample;
import com.fillumina.performance.sample.AbstractSampleProducer;
import com.fillumina.performance.sample.SampleValue;
import com.fillumina.performance.test.LfsrRunnable;
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
    public Map<Class<?>, A> get() {
        A sample = getSample();
        @SuppressWarnings("unchecked")
        Class<A> clazz = (Class<A>)sample.getClass();
        return Collections.singletonMap(clazz, sample);
    }

    public A getSample() {
        TNameMap<SampleValue> map = new TNameMap<>(getTests().size());
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();

            long zero = execute(new LfsrRunnable());
            long mem = execute(test) - zero;

            map.put(new SampleValue(name, mem, MemUnit.B));
        }
        A sample = createSample(map);
        return sample;
    }
}
