package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.sample.Sample;
import com.fillumina.performance.infrastructure.sample.TestSample;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UsedMemSample extends Sample<UsedMemSample, TestSample> {
    private static final long serialVersionUID = 1L;

    public UsedMemSample(TNameMap<TestSample> map) {
        super(map);
    }
}
