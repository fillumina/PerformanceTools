package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.collection.IndexedArrayMap;
import com.fillumina.performance.util.tname.TName;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Container for {@link StatsHolder}s for different types of statistics.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedStatsHolder extends Printable<MixedStatsHolder> {

    public static final MixedStatsHolder EMPTY =
            new MixedStatsHolder(
                    Collections.<StatsType, StatsHolder>emptyMap());

    public static class Builder {
        private final MixedStatsHolder mixedHolder =
                new MixedStatsHolder();

        public Builder addAssertable(
                final StatsType type,
                final TName name,
                final Stats stats) {
            StatsHolder statsHolder = new StatsHolder(type, name, stats);
            statsHolder.setCaller(mixedHolder);
            mixedHolder.map.put(type, statsHolder);
            return this;
        }

        public Builder addAssertable(final StatsType type,
                final StatsHolder assertableHolder) {
            assertableHolder.setCaller(mixedHolder);
            mixedHolder.map.put(type, assertableHolder);
            return this;
        }

        public MixedStatsHolder build() {
            return mixedHolder;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Joiner {
        private final TName name;
        private final Map<StatsType,StatsHolder.Builder> map =
                new LinkedHashMap<>();

        public Joiner(TName name) {
            this.name = name;
        }

        public Joiner addSubExperiment(
                MixedStatsHolder mixedHolder) {
            if (mixedHolder != null) {
                for (Map.Entry<StatsType, StatsHolder> e :
                        mixedHolder.getStatsMap().entrySet()) {
                    StatsType type = e.getKey();
                    StatsHolder stats = e.getValue();
                    getBuilder(type).addSubExperiment(stats);
                }
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        private StatsHolder.Builder getBuilder(
                StatsType t) {
            StatsHolder.Builder builder = map.get(t);
            if (builder == null) {
                builder = StatsHolder.builder(t, name);
                map.put(t, builder);
            }
            return builder;
        }

        public MixedStatsHolder join() {
            Map<StatsType, StatsHolder> statsHolderMap = new LinkedHashMap<>();
            for (Map.Entry<StatsType,
                    StatsHolder.Builder> entry: map.entrySet()) {
                StatsType type = entry.getKey();
                StatsHolder.Builder builder = entry.getValue();
                statsHolderMap.put(type, builder.build());
            }
            return new MixedStatsHolder(statsHolderMap);
        }
    }

    public static Joiner joiner(String name) {
        return new Joiner(TN.tname(name));
    }

    public static Joiner joiner(TName name) {
        return new Joiner(name);
    }

    private final Map<StatsType, StatsHolder> map;
    private final Map<StatsType, StatsHolder> uMap;

    public MixedStatsHolder(StatsHolder... stats) {
        this(new IndexedArrayMap<>());
        for (StatsHolder s : stats) {
            s.setCaller(this);
            map.put(s.getStatsType(), s);
        }
    }

    private MixedStatsHolder(Map<StatsType, StatsHolder> map) {
        this.map = map;
        this.uMap = Collections.unmodifiableMap(map);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<StatsType> getTypes() {
        return uMap.keySet();
    }

    public Map<StatsType, StatsHolder> getStatsMap() {
        return uMap;
    }

    /** Use this when there is only one statistic available. */
    public StatsHolder getFirstStatsHolder() {
        if (map.size() != 1) {
            throw new RuntimeException("more than 1 stats present");
        }
        return map.values().iterator().next();
    }

    public StatsHolder getStatsHolder(StatsType type) {
        StatsHolder holder = map.get(type);
        return holder;
    }

    @Override
    public MixedStatsHolder appendTo(final Appendable appendable) {
        if (appendable != null) {
            boolean first = true;
            for (StatsHolder holder : map.values()) {
                if (first) {
                    first = false;
                } else {
                    try {
                        appendable.append(System.lineSeparator());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                holder.appendTo(appendable);
            }
        }
        return this;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MixedStatsHolder other = (MixedStatsHolder) obj;
        if (!Objects.equals(this.map, other.map)) {
            return false;
        }
        return true;
    }
}
