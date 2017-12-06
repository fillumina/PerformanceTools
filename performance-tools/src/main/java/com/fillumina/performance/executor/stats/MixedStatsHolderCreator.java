package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedStatsHolderCreator {

    private final Map<StatsType, StatsCreator> buildersMap = new HashMap<>();
    private final TName name;

    public MixedStatsHolderCreator(TName name) {
        this.name = name;
    }

    public MixedStatsHolderCreator addSample(Map<StatsType, Sample> map) {
        map.values().forEach( (Sample sample) -> addSample(sample) );
        return this;
    }

    public MixedStatsHolderCreator addSample(Sample sample) {
        getStatsBuilderFor(sample.getStatsType()).addSample(sample);
        return this;
    }

    private StatsCreator getStatsBuilderFor(StatsType type) {
        StatsCreator statsBuilder = buildersMap.get(type);
        if (statsBuilder == null) {
            statsBuilder = new StatsCreator(type);
            buildersMap.put(type, statsBuilder);
        }
        return statsBuilder;
    }

    public MixedStatsHolder getMixedAssertableHolder(
            ListFilter<Double> filter) {
        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        buildersMap.forEach( (StatsType type, StatsCreator statsBuilder) -> {
                Stats stats = statsBuilder.createStats(filter);
                builder.addAssertable(type, name, stats);
        });
        return builder.build();
    }
}
