package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.Sample;
import com.fillumina.performance.infrastructure.sample.TestSample;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AllocatedMemSample extends Sample<AllocatedMemSample, TestSample> {
    private static final long serialVersionUID = 1L;

    public AllocatedMemSample(TNameMap<TestSample> map) {
        super(map);
    }
}
