package com.fillumina.performance.infrastructure.sample;

import com.fillumina.performance.infrastructure.stats.SampleCollector;
import com.fillumina.performance.infrastructure.stats.SingleStats;
import com.fillumina.performance.infrastructure.stats.Stats;
import com.fillumina.performance.util.tname.TNameMap;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class Sample
        extends AbstractSample<Sample,SampleValue,Stats<SingleStats>,SingleStats> {
    private static final long serialVersionUID = 1L;

    public Sample(TNameMap<SampleValue> map) {
        super(map);
    }

    @Override
    public SampleCollector<Stats<SingleStats>, SingleStats, Sample, SampleValue>
        getSampleCollector() {
        return new SampleCollector<Stats<SingleStats>, SingleStats, Sample, SampleValue>() {
            @Override
            protected Stats<SingleStats> createNewStats(
                    SampleCollector<Stats<SingleStats>, SingleStats, Sample, SampleValue>.Measures m) {
                return new Stats<>(m.getMultiMeasure(),
                        m.getSingleStatsList((t, u) -> new SingleStats(t, u)) );
            }
        };
    }

}
