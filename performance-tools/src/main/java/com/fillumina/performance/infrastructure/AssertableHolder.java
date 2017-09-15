package com.fillumina.performance.infrastructure;

import com.fillumina.performance.util.StringGenerator;
import com.fillumina.performance.util.Printable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * Container for statistics. Each {@link Assertable} statistics is composed
 * by a number of tests and their measures. Internally this can be represented
 * as a map. But in case parameters or sequences (or both) were used
 * statistics are represented by a tree. This wrapper class allows to treat
 * them independently from their internal representation and offers
 * helpers to check them against assertions and to output results.
 *
 * @author Francesco Illuminati
 */
public class AssertableHolder<A extends Assertable>
        extends Printable<AssertableHolder<A>>
        implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static class Builder<A extends Assertable> {
        private final Class<A> type;
        private final LinkedTree<TName, A> tree;
        private final StringGenerator<A> generator;
        private LinkedTree<TName, A> current;

        private Builder(
                Class<A> type,
                TName tname,
                A assertable,
                StringGenerator<A> generator) {
            this.type = type;
            this.tree = new LinkedTree<>(tname, assertable);
            this.current = this.tree;
            this.generator = generator;
        }

        public Builder<A> addSubExperiment(AssertableHolder<A> holder) {
            current.addSubTree(holder.tree);
            return this;
        }

        public Builder<A> name(String name) {
            current.put(tname(name), null);
            return this;
        }

        public Builder<A> subExperiment(String name) {
            current = current.addTree(tname(name), null);
            return this;
        }

        public Builder<A> test(String name, A assertable) {
            current.put(tname(name), assertable);
            return this;
        }

        private TName tname(String name) {
            return current.getKey().append(name);
        }

        public Builder<A> endSubExperiment() {
            current = current.getParent();
            return this;
        }

        @SuppressWarnings("unchecked")
        public AssertableHolder<A> build() {
            return new AssertableHolder<>(type, tree, generator);
        }
    }

    private final Class<A> statsType;
    private final LinkedTree<TName, A> tree;
    private final StringGenerator<A> formatter;
    private MixedAssertableHolder caller;

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment(Class<A> type) {
        return experiment(type, TN.EMPTY, null, null);
    }

    /** @return a builder to create tree statistics */
    public static <A extends Assertable> Builder<A> experiment(
            Class<A> type, String name) {
        return experiment(type, TN.tname(name), null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment(
            Class<A> type, TName tname) {
        return experiment(type, TN.notNull(tname), null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment(
            Class<A> type,
            TName name,
            A assertable,
            StringGenerator<A> stringGenerator) {
        return new Builder<>(type, TN.notNull(name), assertable, stringGenerator);
    }

    public AssertableHolder(
            final Class<A> type,
            final A stats) {
        this(type, null, stats, null);
    }

    public AssertableHolder(
            final Class<A> type,
            final TName name,
            final A stats) {
        this(type, name, stats, null);
    }

    public AssertableHolder(
            final Class<A> type,
            final String name,
            final A stats) {
        this(type, TN.tname(name), stats, null);
    }

    public AssertableHolder(
            final Class<A> type,
            final TName name,
            final StringGenerator<A> formatter) {
        this(type, name, null, formatter);
    }

    public AssertableHolder(
            final Class<A> type,
            final TName name,
            final A stats,
            final StringGenerator<A> formatter) {
        this(type, new LinkedTree<>(name, stats), formatter);
    }

    private AssertableHolder(
            final Class<A> type,
            final LinkedTree<TName,A> tree,
            final StringGenerator<A> formatter) {
        this.statsType = type;
        this.tree = tree;
        this.formatter = formatter;
    }

    /* called by MixedAssertableHolder */
    void setCaller(MixedAssertableHolder caller) {
        this.caller = caller;
    }

    /* test only */ LinkedTree<TName,A> getTree() {
        return tree;
    }

    public MixedAssertableHolder end() {
        return caller;
    }

    public Class<A> getAssertableType() {
        return statsType;
    }

    /** @return true if no statistics available. */
    public boolean isEmpty() {
        return tree.isNull();
    }

    /** @return the name of the test. */
    public TName getName() {
        return tree.getKey();
    }

    public A getAssertable() {
        return tree.getValue();
    }

    private interface LeafVisitor<T extends Assertable> {
        void visitLeaf(TName name, T assertable);
    }

    /**
     *
     * @param <T>     the type of the leaves
     * @param visitor the visitor
     */
    @SuppressWarnings("unchecked")
    private <T extends Assertable> void traverseLeaves(
            final LeafVisitor<T> visitor) {
        ((Tree<TName,T>)tree).<TName,T>traverseLeaves(
                (Tree<TName, T> t) -> {
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
    public AssertableHolder<A> use(AssertableConsumer<A> consumer) {
        if (consumer != null) {
            traverseLeaves((TName name, A assertable) -> {
                consumer.consumeAssertable(assertable);
            });
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public TNameMatcherAssertion.Builder<AssertableHolder<A>> check() {
        return TNameMatcherAssertion.builder((builtObject) -> {
                    return check(builtObject);
                });
    }

    @SuppressWarnings("unchecked")
    public AssertableHolder<A> check(Assertion assertion) {
        return use((AssertableConsumer<A>) assertion);
    }

    public TNameMatcherAssertion.Builder<AssertableHolder<A>> checkAndAppendTo(
            Appendable appendable) {
        return TNameMatcherAssertion.builder((builtObject) -> {
                return checkAndAppendTo(appendable, builtObject);
            });
    }

    @SuppressWarnings("unchecked")
    public AssertableHolder<A> checkAndAppendTo(
            Appendable appendable,
            Assertion assertion) {
        if (appendable != null) {
            final AppendableWrapperSentinel wrapped =
                    new AppendableWrapperSentinel(appendable);
            traverseLeaves((TName name, A assertable) -> {
                try {
                    wrapped.setUnmodified();
                    assertion.appendToCatchingException(wrapped, assertable);
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

    public LinkedMap<TName, Assertable> getFlattenedAssertableMap() {
        LinkedMap<TName, Assertable> map = new LinkedMap<>();
        traverseLeaves((TName name, Assertable assertable) -> {
            map.put(name, assertable);
        });
        return map;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.tree);
        hash = 17 * hash + Objects.hashCode(this.formatter);
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
        @SuppressWarnings("unchecked")
        final AssertableHolder<A> other = (AssertableHolder<A>) obj;
        if (!Objects.equals(this.statsType, other.statsType)) {
            return false;
        }
        if (!Objects.equals(this.tree, other.tree)) {
            return false;
        }
        if (!Objects.equals(this.formatter, other.formatter)) {
            return false;
        }
        return true;
    }

    @Override
    public AssertableHolder<A> appendTo(final Appendable appendable) {
        if (appendable != null) {
            try {
                appendTo(appendable, tree);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    private void appendTo(Appendable appendable, Tree<TName,A>  tree)
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
            for (Tree<TName,A> branch : tree) {
                appendTo(appendable, branch);
            }
        }
    }

    private void appendLeafTo(Appendable appendable, A assertable)
            throws IOException {
        if (formatter != null) {
            formatter.appendToCatchingException(appendable, assertable);
        } else {
            appendable.append(Objects.toString(assertable))
                    .append(System.lineSeparator());
        }
    }
}
