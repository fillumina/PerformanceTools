package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.ExperimentAssertion;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNamed;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Container for statistics of the same type. Each {@link Stats} is composed
 * by a number of tests and their measures. Internally this is represented
 * as a map. But in case parameters or sequences (or both) were used
 * statistics are represented by a tree. This wrapper class allows to treat
 * them independently from their internal representation and offers
 * helpers to check them against assertions and to output results.
 *
 * @author Francesco Illuminati
 */
public class StatsHolder extends Printable<StatsHolder>
        implements StatsTyped, TNamed, Serializable {

    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static class Builder {
        private final StatsType type;
        private final LinkedTree<TName, Stats> tree;
        private final StringGenerator<Stats> generator;
        private LinkedTree<TName, Stats> current;

        private Builder(
                StatsType type,
                TName tname,
                Stats assertable,
                StringGenerator<Stats> generator) {
            this.type = type;
            this.tree = new LinkedTree<>(tname, assertable);
            this.current = this.tree;
            this.generator = generator;
        }

        public Builder addSubExperiment(StatsHolder holder) {
            current.addTreeCopy(holder.tree);
            return this;
        }

        public Builder name(String name) {
            current.put(tname(name), null);
            return this;
        }

        public Builder subExperiment(String name) {
            current = current.add(tname(name), null);
            return this;
        }

        public Builder test(String name, Stats assertable) {
            current.put(tname(name), assertable);
            return this;
        }

        private TName tname(String name) {
            return current.getKey().append(name);
        }

        public Builder endSubExperiment() {
            current = current.getParent();
            return this;
        }

        @SuppressWarnings("unchecked")
        public StatsHolder build() {
            return new StatsHolder(type, tree, generator);
        }
    }

    private final StatsType statsType;
    private final LinkedTree<TName, Stats> tree;
    private final StringGenerator<Stats> formatter;
    private MixedStatsHolder caller;

    /** @return a builder to create a tree statistics */
    public static Builder builder(StatsType type) {
        return builder(type, TN.EMPTY, null, null);
    }

    /** @return a builder to create tree statistics */
    public static Builder builder(
            StatsType type, String name) {
        return builder(type, TN.tname(name), null, null);
    }

    /** @return a builder to create a tree statistics */
    public static Builder builder(
            StatsType type, TName tname) {
        return builder(type, TN.notNull(tname), null, null);
    }

    public StatsHolder(StatsHolder copy) {
        this.statsType = copy.statsType;
        this.tree = new LinkedTree<>(copy.tree);
        this.formatter = copy.formatter;
    }

    /** @return a builder to create a tree statistics */
    public static Builder builder(
            StatsType type,
            TName name,
            Stats stats,
            StringGenerator<Stats> stringGenerator) {
        return new Builder(type, TN.notNull(name), stats, stringGenerator);
    }

    public StatsHolder(final Stats stats) {
        this(TN.EMPTY, stats);
    }

    public StatsHolder(
            final TName name,
            final Stats stats) {
        this(name, stats, null);
    }

    public StatsHolder(
            final TName name,
            final Stats stats,
            final StringGenerator<Stats> formatter) {
        this(stats.getStatsType(), new LinkedTree<>(name, stats), formatter);
    }

    private StatsHolder(
            final StatsType type,
            final LinkedTree<TName,Stats> tree,
            final StringGenerator<Stats> formatter) {
        this.statsType = type;
        this.tree = tree == null ? null : tree.setUnmodifiable();
        this.formatter = formatter;
    }

    /* called by MixedAssertableHolder */
    void setCaller(MixedStatsHolder caller) {
        this.caller = caller;
    }

    /** Still not sure if make it part of the public API */
    public LinkedTree<TName,Stats> getTree() {
        return tree;
    }

    public MixedStatsHolder end() {
        return caller;
    }

    @Override
    public StatsType getStatsType() {
        return statsType;
    }

    /** @return true if no statistics available. */
    public boolean isEmpty() {
        return tree == null || (tree.isEmpty() && tree.getValue() == null);
    }

    /** @return the name of the test. */
    @Override
    public TName getName() {
        return tree.getKey();
    }

    public Stats getStats() {
        return tree.getValue();
    }

    public Stats getStatsAtPath(String... path) {
        String[] array;
        if (!path[0].equals(tree.getKey().getFirstName())) {
            array = new String[path.length + 1];
            array[0] = tree.getKey().getFirstName();
            System.arraycopy(path, 0, array, 1, path.length);
        } else {
            array = path;
        }
        TName tpath = TN.tname(array);
        LinkedTree<TName,Stats> subTree = null;

        for (TName t : tpath.getAllPartialTNames()) {
            if (t.size() == 1 && t.equals(tree.getKey())) {
                subTree = tree;
            } else {
                subTree = subTree.getTree(t);
            }
        }
        return subTree.getValue();
    }

    public Stats getStats(TName path) {
        return tree.getValueAtPath(path);
    }

    private interface LeafVisitor<T extends AssertableExperiment> {
        void visitLeaf(TName name, T stats);
    }

    /**
     *
     * @param <T>     the type of the leaves
     * @param visitor the visitor
     */
    @SuppressWarnings("unchecked")
    private void traverseLeaves(final LeafVisitor<Stats> visitor) {
        tree.<TName,Stats>traverseLeaves(
                (LinkedTree<TName, Stats> t) -> {
                    visitor.visitLeaf(t.getKey(), t.getValue());
                    return false;
                });
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @param consumers
     * @return {@code this}
     */
    public StatsHolder use(Consumer<Stats> consumer) {
        if (consumer != null) {
            traverseLeaves((TName name, Stats stats) -> {
                consumer.accept(stats);
            });
        }
        return this;
    }

    public TNameMatcherAssertion.Builder<StatsHolder> check() {
        return TNameMatcherAssertion.builder((builtObject) -> {
                    return check(builtObject);
                });
    }

    public StatsHolder check(ExperimentAssertion assertion) {
        if (assertion != null) {
            traverseLeaves((TName name, Stats stats) -> {
                assertion.accept(stats);
            });
        }
        return this;
    }

    public TNameMatcherAssertion.Builder<StatsHolder> checkAndAppendTo(
            Appendable appendable) {
        return TNameMatcherAssertion.builder((builtObject) -> {
                return checkAndAppendTo(appendable, builtObject);
            });
    }

    @SuppressWarnings("unchecked")
    public StatsHolder checkAndAppendTo(
            Appendable appendable,
            ExperimentAssertion assertion) {
        if (appendable != null) {
            final AppendableWrapperSentinel wrapped =
                    new AppendableWrapperSentinel(appendable);
            traverseLeaves((TName name, Stats stats) -> {
                try {
                    wrapped.setUnmodified();
                    assertion.appendToCatchingException(wrapped, stats);
                    if (wrapped.isModified()) {
                        appendable.append(System.lineSeparator());
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return this;
    }

    public IndexedHashMap<TName, Stats> getFlattenedAssertableMap() {
        IndexedHashMap<TName, Stats> map = new IndexedHashMap<>();
        traverseLeaves((TName name, Stats stats) -> {
            if (name != null) {
                map.put(name, stats);
            }
        });
        return map;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.statsType);
        hash = 41 * hash + Objects.hashCode(this.tree);
        return hash;
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
        final StatsHolder other = (StatsHolder) obj;
        if (!Objects.equals(this.statsType, other.statsType)) {
            return false;
        }
        if (!Objects.equals(this.tree, other.tree)) {
            return false;
        }
        return true;
    }

    @Override
    public StatsHolder appendTo(final Appendable stats) {
        if (stats != null) {
            try {
                appendTo(stats, tree);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    private void appendTo(Appendable appendable, LinkedTree<TName,Stats>  tree)
            throws IOException {
        TName title = tree.getKey();
        if (title != null && !title.isEmpty()) {
            appendable.append(System.lineSeparator());
            appendable.append(TableFormatter.title(
                    title.toStringWithSeparator(SEPARATOR),
                    tree.isLeaf() ? '-' : '='));
        }
        if (tree.isLeaf()) {
            appendLeafTo(appendable, tree.getValue());
        } else {
            for (LinkedTree<TName,Stats> branch : tree) {
                appendTo(appendable, branch);
            }
        }
    }

    private void appendLeafTo(Appendable appendable, Stats stats)
            throws IOException {
        if (formatter != null) {
            formatter.appendToCatchingException(appendable, stats);
        } else {
            appendable.append(Objects.toString(stats))
                    .append(System.lineSeparator());
        }
    }
}
