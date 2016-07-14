package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AssertableMultiStats;
import com.fillumina.performance.util.ComposedName;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsTree<A extends AssertableMultiStats> {
    private final ComposedName name;
    private final Object stats;

    public static final StatsTree<AssertableMultiStats> EMPTY =
            new StatsTree<>(null, (AssertableMultiStats)null);

    @SuppressWarnings("unchecked")
    public static <A extends AssertableMultiStats> StatsTree<A> empty() {
        return (StatsTree<A>) EMPTY;
    }

    public StatsTree(ComposedName name,
            Map<ComposedName, ? extends AssertableMultiStats> stats) {
        this.name = name;
        this.stats = stats;
    }

    public StatsTree(ComposedName name,
            AssertableMultiStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public StatsTree(ComposedName name, Object stats) {
        this.name = name;
        this.stats = stats;
    }

    public boolean isEmpty() {
        return stats == null;
    }

    @SuppressWarnings("unchecked")
    public A getStats(ComposedName name) {
        Object current = stats;
        for (ComposedName cn : name.asList()) {
            current = ((Map<ComposedName, ?>)current).get(cn);
        }
        return (A)current;
    }

    public static interface Visitor {

        void visitStats(ComposedName name, AssertableMultiStats stats);

        void visitTitle(int level, ComposedName name);
    }

    public void traverse(Visitor visitor) {
        new Traverser(visitor).visit(name, stats, 0);
    }

    private static class Traverser {
        private final Visitor visitor;

        public Traverser(Visitor visitor) {
            this.visitor = visitor;
        }

        @SuppressWarnings("unchecked")
        public void visit(ComposedName name, Object value, int level) {
            if (value instanceof Map) {
                if (name != null && !name.isEmpty()) {
                    visitor.visitTitle(level, name);
                }
                int nestedLevel = level + 1;
                for (Map.Entry<ComposedName, ?> entry :
                        ((Map<ComposedName, ?>)value).entrySet()) {
                    visit(entry.getKey(), entry.getValue(), nestedLevel);
                }
            } else if (value instanceof AssertableMultiStats) {
                visitor.visitStats(name, (AssertableMultiStats)value);
            } else if (value == null) {
                throw new NullPointerException("unexepected null value in tree");
            } else {
                throw new RuntimeException("unexpected type in tree: "
                    + value.getClass());
            }
        }
    }
}
