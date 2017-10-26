package com.fillumina.performance.mock;

import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleMock extends AbstractSampleMock<SampleMock,StatsMock> {
    private static final long serialVersionUID = 1L;

    public SampleMock(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    protected StatsMock createStats(MultiMeasureSignificance s,
            TNameMap<SingleStats> map) {
        return new StatsMock(s, map);
    }
}
