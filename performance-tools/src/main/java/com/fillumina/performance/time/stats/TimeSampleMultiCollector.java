package com.fillumina.performance.time.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.MixedAssertableHolder;
import com.fillumina.performance.time.sample.AverageTimeSample;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.ValueReMapper;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TimeSampleMultiCollector {

    private static final
            Map<Class<? extends Assertable>, Supplier<TimeSampleCollector<?>>>
            DEFAULT_COLLECTOR_SUPPLIERS = LinkedMap.create(
                    AverageTimeStats.class,
                    new Supplier<TimeSampleCollector<?>>() {
                        @Override
                        public TimeSampleCollector<?> get() {
                            return TimeSampleCollector
                                    .createAverageTimeCollector();
                        }
                    },

                    ThroughputStats.class,
                    new Supplier<TimeSampleCollector<?>>() {
                        @Override
                        public TimeSampleCollector<?> get() {
                            return TimeSampleCollector
                                    .createThroughputCollector();
                        }
                    }
            );

    private final TName name;
    private final boolean filterSamples;
    private final Map<Class<? extends Assertable>, TimeSampleCollector<?>> map;
    private final Map<Class<? extends Assertable>, TimeStats> statsMap;

    public TimeSampleMultiCollector(TName name, boolean filterSamples) {
        this(name, filterSamples, DEFAULT_COLLECTOR_SUPPLIERS);
    }

    public TimeSampleMultiCollector(TName name,
            boolean filterSamples,
            Map<Class<? extends Assertable>, Supplier<TimeSampleCollector<?>>>
                    suppliers) {
        this.name = name;
        this.filterSamples = filterSamples;
        this.map = initMap(suppliers == null ?
                DEFAULT_COLLECTOR_SUPPLIERS : suppliers);
        this.statsMap = initStatsMap(map, (t) -> {
            return t.createStatsAndFilterIf(this.filterSamples);
        });
    }

    private static Map<Class<? extends Assertable>, TimeSampleCollector<?>> initMap(
            Map<Class<? extends Assertable>, Supplier<TimeSampleCollector<?>>> suppliers) {
        Map<Class<? extends Assertable>, TimeSampleCollector<?>> map = new LinkedMap<>();
        for (Map.Entry<Class<? extends Assertable>, Supplier<TimeSampleCollector<?>>> e :
                suppliers.entrySet()) {
            Class<? extends Assertable> type = e.getKey();
            Supplier<TimeSampleCollector<?>> supplier = e.getValue();
            map.put(type, supplier.get());
        }
        return map;
    }

    public static Map<Class<? extends Assertable>, TimeStats> initStatsMap(
            Map<Class<? extends Assertable>, TimeSampleCollector<?>> map,
            Function<TimeSampleCollector<?>, TimeStats> mapperFunct) {
        Map<Class<? extends Assertable>, TimeStats> mapper =
                new ValueReMapper<>(map, mapperFunct);
        return mapper;
    }

    public void add(AverageTimeSample speedSample) {
        map.values().stream().forEach((coll) -> coll.add(speedSample));
    }

    public Map<Class<? extends Assertable>, TimeStats> getStatsMap() {
        return statsMap;
    }

    public MixedAssertableHolder getMixedAssertableHolder() {
        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        for (Map.Entry<Class<? extends Assertable>, TimeSampleCollector<?>> e :
                map.entrySet()) {
            Class<? extends Assertable> type = e.getKey();
            TimeStats stats = e.getValue().createStatsAndFilterIf(filterSamples);
            builder.addAssertable(type, name, stats);
        }
        return builder.build();
    }

}
