package com.fillumina.performance.time.sample;

import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati
 */
public class AverageTimeSample extends TimeSample {
    private static final long serialVersionUID = 1L;

    public AverageTimeSample(TNameMap<TimeSampleValue> map, long totalTimeNs) {
        super(map, totalTimeNs);
    }
}
