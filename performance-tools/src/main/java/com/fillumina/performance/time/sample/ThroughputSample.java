package com.fillumina.performance.time.sample;

import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati
 */
public class ThroughputSample extends TimeSample {
    private static final long serialVersionUID = 1L;

    public ThroughputSample(TNameMap<TimeSampleValue> map, long totalTimeNs) {
        super(map, totalTimeNs);
    }
}
