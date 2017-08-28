package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.AbstractSampleProducer;
import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.SampleProducer;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMemConsumtionExecutor
            <S extends AbstractSample<S, SampleValue>>
        extends AbstractSampleProducer<AbstractMemConsumtionExecutor<S>, S>
        implements MemConsumptionExecutor<AbstractMemConsumtionExecutor<S>, S> {

    static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    protected final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory()/ MC.getAlignment());

    protected abstract Class<S> getSampleClass();
    protected abstract S createSample(TNameMap<SampleValue> map);

    @Override
    public Map<Class<?>, S> get() {
        TNameMap<SampleValue> map = new TNameMap<>(getTests().size());
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable runnable = e.getValue();

            long mem = execute(runnable);
            map.put(new SampleValue(name, mem, MemUnit.B));
        }
        S sample =  createSample(map);
        @SuppressWarnings("unchecked")
        Class<S> clazz = (Class<S>)sample.getClass();
        return Collections.singletonMap(clazz, sample);
    }

    /**
     * Set a supervisor able to pilot this {@link MemConsumptionExecutor}.
     */
    @Override
    public <T extends Instrumenter<SampleProducer<AbstractMemConsumtionExecutor<S>, S>>>
        T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
