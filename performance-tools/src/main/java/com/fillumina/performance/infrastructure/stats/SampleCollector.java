package com.fillumina.performance.infrastructure.stats;

import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.infrastructure.sample.AbstractSample;
import com.fillumina.performance.infrastructure.sample.StatsBuilder;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCollector<S extends Stats<?>, A extends AbstractSample<A,?,S>> {

    private final Map<Class<?>,StatsBuilder<S,A>> creatorsMap = new HashMap<>();
    private final TName name;

    public SampleCollector(TName name) {
        this.name = name;
    }

    public SampleCollector<S,A> addSample(A sample) {
        getCreator(sample).addSample(sample);
        return this;
    }

    public SampleCollector<S,A> addSample(Map<Class<?>, A> map) {
        map.values().forEach(sample -> {
            getCreator(sample).addSample(sample);
        });
        return this;
    }

    private StatsBuilder<S,A> getCreator(A sample) {
        StatsBuilder<S,A> sc = creatorsMap.get(sample.getClass());
        if (sc == null) {
            sc = sample.getStatsBuilder();
        }
        return sc;
    }

    public MixedAssertableHolder getStats(ListFilter<Double> filter) {
        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        for (StatsBuilder<S,A> sc : creatorsMap.values()) {
            S stats = sc.createStats(filter);
            builder.addAssertable(stats.getClass(), name, stats);
        }
        return builder.build();
    }
}
