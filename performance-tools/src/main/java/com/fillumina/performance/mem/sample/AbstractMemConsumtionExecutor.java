package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.AbstractAssertableProducer;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.sample.Sample;
import com.fillumina.performance.infrastructure.sample.TestSample;
import com.fillumina.performance.util.instrument.Instrumenter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.MemUnit;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMemConsumtionExecutor
            <S extends Sample<S, TestSample>>
        extends AbstractAssertableProducer
                    <AbstractMemConsumtionExecutor<S>, Runnable>
        implements MemConsumptionExecutor<S> {

    static final MemoryConsumption MC = MemoryConsumption.INSTANCE;

    protected final int REPETITIONS =
            (int) (MC.getMinimalAllocableMemory()/ MC.getAlignment());

    protected abstract Class<S> getSampleClass();
    protected abstract S createSample(TNameMap<TestSample> map);

    @Override
    public MixedAssertableHolder execute() {
        TNameMap<TestSample> map = new TNameMap<>(getTests().size());
        for (Map.Entry<TName,Runnable> e : getTests()) {
            TName name = e.getKey();
            Runnable runnable = e.getValue();

            long mem = execute(runnable);
            map.put(new TestSample(name, mem, MemUnit.B));
        }
        S sample = createSample(map);
        return MixedAssertableHolder.builder()
                .addAssertable(getSampleClass(), getName(), sample)
                .build();
    }

    /**
     * Set a supervisor able to pilot this {@link MemConsumptionExecutor}.
     */
    @Override
    public <T extends Instrumenter<MemConsumptionExecutor<S>>> T instrumentedBy(
            T instrumenter) {
        instrumenter.instrument(this);
        return instrumenter;
    }
}
