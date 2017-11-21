package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.StatsBuilder;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.tname.TName;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreator {

    private final Map<Stats.Type, StatsBuilder> buildersMap = new HashMap<>();
    private final TName name;

    public StatsCreator(TName name) {
        this.name = name;
    }

    public StatsCreator addSample(Map<Stats.Type, Sample> map) {
        map.forEach((Stats.Type type , Sample sample) -> {
            addSample(sample);
        });
        return this;
    }

    public StatsCreator addSample(Sample sample) {
        getStatsBuilderFor(sample.getStatsType()).addSample(sample);
        return this;
    }

    private StatsBuilder getStatsBuilderFor(Stats.Type type) {
        StatsBuilder statsBuilder = buildersMap.get(type);
        if (statsBuilder == null) {
            statsBuilder = new StatsBuilder(type);
            buildersMap.put(type, statsBuilder);
        }
        return statsBuilder;
    }

    public MixedStatsHolder getMixedAssertableHolder(
            ListFilter<Double> filter) {
        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        buildersMap.forEach( (Stats.Type type, StatsBuilder statsBuilder) -> {
                Stats stats = statsBuilder.createStats(filter);
                builder.addAssertable(type, name, stats);
        });
        return builder.build();
    }
}
