package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;
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
            addSample(type, sample);
        });
        return this;
    }

    public StatsCreator addSample(Stats.Type type, Sample sample) {
        getStatsBuilderFor(type).addSample(sample);
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

    public MixedAssertableHolder getMixedAssertableHolder(
            ListFilter<Double> filter) {
        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        buildersMap.forEach( (Stats.Type type, StatsBuilder statsBuilder) -> {
                Stats stats = statsBuilder.createStats(filter);
                builder.addAssertable(type, name, stats);
        });
        return builder.build();
    }
}
