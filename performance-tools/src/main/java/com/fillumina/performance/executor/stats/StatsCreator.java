package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.sample.AbstractSample;
import com.fillumina.performance.executor.sample.StatsBuilder;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreator<S extends Stats<?>,
                          A extends AbstractSample<A,?,S>> {

    private final Map<Class<?>,StatsBuilder<S,A>> creatorsMap = new HashMap<>();
    private final TName name;

    public StatsCreator(TName name) {
        this.name = name;
    }

    public StatsCreator<S,A> addSample(A sample) {
        getCollectorFor(sample).addSample(sample);
        return this;
    }

    @SuppressWarnings("unchecked")
    public StatsCreator<S,A> addSample(Map<Class<?>, A> map) {
        map.values().forEach(sample -> {
            addSample(sample);
        });
        return this;
    }

    private StatsBuilder<S,A> getCollectorFor(A sample) {
        StatsBuilder<S,A> statsBuilder = creatorsMap.get(sample.getClass());
        if (statsBuilder == null) {
            statsBuilder = sample.getStatsBuilder();
            creatorsMap.put(sample.getClass(), statsBuilder);
        }
        return statsBuilder;
    }

    public MixedAssertableHolder getMixedAssertableHolder(
            ListFilter<Double> filter) {
        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        for (StatsBuilder<S,A> sb : creatorsMap.values()) {
            S stats = sb.createStats(filter);
            builder.addAssertable(stats.getClass(), name, stats);
        }
        return builder.build();
    }
}
