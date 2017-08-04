package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.util.TName;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Container for {@link AssertableHolder}s of different type of statistics.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertableHolder
        extends Printable<MixedAssertableHolder> {

    public static final MixedAssertableHolder EMPTY = new MixedAssertableHolder();

    public static class Builder {
        private final MixedAssertableHolder mixedHolder =
                new MixedAssertableHolder();

        public Builder addAssertableHolder(
                final Class<? extends Assertable> type,
                final TName name,
                final Assertable stats) {
            @SuppressWarnings("unchecked")
            AssertableHolder<?> assertableHolder = new AssertableHolder<>(
                    (Class<Assertable>)type, name, stats);
            assertableHolder.setCaller(mixedHolder);
            mixedHolder.map.put(type, assertableHolder);
            return this;
        }

        public MixedAssertableHolder build() {
            return mixedHolder;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Joiner {
        private final TName name;
        private final Map<Class<? extends Assertable>,
                          AssertableHolder.Builder<?>> map = new LinkedHashMap<>();

        public Joiner(TName name) {
            this.name = name;
        }

        public <A extends Assertable> Joiner addSubExperiment(
                MixedAssertableHolder msh) {
            for (Class<? extends Assertable> t : msh.getTypes()) {
                addToBuilder(t, msh);
            }
            return this;
        }

        private <A extends Assertable> void addToBuilder(Class<A> t,
                MixedAssertableHolder mah) {
            AssertableHolder.Builder<A> builder = getBuilder(t);
            if (builder == null) {
                builder = AssertableHolder.experiment(t, name);
                map.put(t, builder);
            }
            AssertableHolder<A> stats = mah.getStats(t);
            builder.addSubExperiment(stats);
        }

        @SuppressWarnings("unchecked")
        private <A extends Assertable> AssertableHolder.Builder<A> getBuilder(
                Class<A> t) {
            return (AssertableHolder.Builder<A>) map.get(t);
        }

        public MixedAssertableHolder join() {
            Map<Class<? extends Assertable>, AssertableHolder<?>> statsHolderMap =
                    new LinkedHashMap<>();
            for (Map.Entry<Class<? extends Assertable>,
                    AssertableHolder.Builder<?>> entry: map.entrySet()) {
                Class<? extends Assertable> type = entry.getKey();
                AssertableHolder.Builder<?> builder = entry.getValue();
                statsHolderMap.put(type, builder.build());
            }
            return new MixedAssertableHolder(statsHolderMap);
        }
    }

    public static Joiner joiner(String name) {
        return new Joiner(TN.tname(name));
    }

    public static Joiner joiner(TName name) {
        return new Joiner(name);
    }

    private final Map<Class<? extends Assertable>, AssertableHolder<?>> map;
    private final Map<Class<? extends Assertable>, AssertableHolder<?>> uMap;

    public MixedAssertableHolder(AssertableHolder<?>... stats) {
        this();
        for (AssertableHolder<?> s : stats) {
            s.setCaller(this);
            map.put(s.getAssertableType(), s);
        }
    }

    private MixedAssertableHolder() {
        this(new LinkedHashMap<>());
    }

    private MixedAssertableHolder(Map<Class<? extends Assertable>,
            AssertableHolder<?>> map) {
        this.map = map;
        this.uMap = Collections.unmodifiableMap(map);
    }

    public Set<Class<? extends Assertable>> getTypes() {
        return uMap.keySet();
    }

    public Map<Class<? extends Assertable>, AssertableHolder<?>> getStatsMap() {
        return uMap;
    }

    /** Use this when there is only one statistic available. */
    @SuppressWarnings("unchecked")
    public <A extends Assertable> AssertableHolder<A> getStats() {
        if (map.size() != 1) {
            throw new RuntimeException("more than 1 stats present");
        }
        return (AssertableHolder<A>) map.values().iterator().next();
    }

    @SuppressWarnings("unchecked")
    public <A extends Assertable> AssertableHolder<A> getStats(Class<A> type) {
        AssertableHolder<A> holder = (AssertableHolder<A>) map.get(type);
        return holder;
    }

    @Override
    public MixedAssertableHolder appendTo(final Appendable appendable) {
        if (appendable != null) {
            boolean first = true;
            for (AssertableHolder<?> ah : map.values()) {
                if (first) {
                    first = false;
                } else {
                    try {
                        appendable.append(System.lineSeparator());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                ah.appendTo(appendable);
            }
        }
        return this;
    }
}
