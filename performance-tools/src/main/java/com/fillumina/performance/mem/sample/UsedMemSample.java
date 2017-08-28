package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.SampleValue;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemSample extends AbstractSample<UsedMemSample, SampleValue> {
    private static final long serialVersionUID = 1L;

    public UsedMemSample(TNameMap<SampleValue> map) {
        super(map);
    }
}
