package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.test.LfsrRunnable;
import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.AbstractSampleProducer;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.mem.MemStats;
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

    protected final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory()/ MC.getAlignment());

    protected abstract Class<A> getSampleClass();
    protected abstract A createSample(TNameMap<SampleValue> map);

    @Override
    public Map<Class<?>, A> get() {
        TNameMap<SampleValue> map = new TNameMap<>(getTests().size());
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable test = e.getValue();

            long zero = execute(new LfsrRunnable());
            long mem = execute(test) - zero;

            map.put(new SampleValue(name, mem, MemUnit.B));
        }
        A sample = createSample(map);
        @SuppressWarnings("unchecked")
        Class<A> clazz = (Class<A>)sample.getClass();
        return Collections.singletonMap(clazz, sample);
    }
}
