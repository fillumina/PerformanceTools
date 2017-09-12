package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.mem.MemStats;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.MemUnit;
import com.fillumina.performance.util.unit.Unit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class AbstractMemSample
            <I extends AbstractMemSample<I,S>, S extends MemStats>
        extends AbstractSample<I, SampleValue, S> {
    private static final long serialVersionUID = 1L;

    public AbstractMemSample(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    public Unit getUnit() {
        return MemUnit.B;
    }
}