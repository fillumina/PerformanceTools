package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TNameMatcherAssertion;
import com.fillumina.performance.util.AppendableWrapperSentinel;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Container for statistics.
 *
 * @author Francesco Illuminati
 */
public class PHolder<A extends Assertable> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";

    public static class Builder<A extends Assertable> {
        private LinkedTree<TName, A> tree;
        private LinkedTree<TName, A> current;
        private StringGenerator<A> generator;

        private Builder(TName tname,
                A assertable,
                StringGenerator<A> generator) {
            this.tree = new LinkedTree<>(tname, assertable);
            this.current = this.tree;
            this.generator = generator;
        }

        public Builder<A> addSubExperiment(PHolder<A> holder) {
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
        public PHolder<A> build() {
            return new PHolder<>(tree, generator);
        }
    }

    private final LinkedTree<TName, A> tree;
    private final StringGenerator<A> formatter;
    private final List<Assertion<A>> assertions = new ArrayList<>();

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment() {
        return experiment(TN.EMPTY, null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment(String name) {
        return experiment(TN.tname(name), null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment(TName tname) {
        return experiment(TN.notNull(tname), null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> experiment(TName name,
            A assertable,
            StringGenerator<A> stringGenerator) {
        return new Builder<>(TN.notNull(name), assertable, stringGenerator);
    }

    public PHolder(final String... name) {
        this(TN.tname(name), null, null);
    }

    public PHolder(final TName name) {
        this(name, null, null);
    }

    public PHolder(final A stats) {
        this(null, stats, null);
    }

    public PHolder(final String name, final A stats) {
        this(TN.tname(name), stats, null);
    }

    public PHolder(final TName name, final A stats) {
        this(name, stats, null);
    }

    public PHolder(final TName name,
            final StringGenerator<A> formatter) {
        this(name, null, formatter);
    }

    public PHolder(final TName name,
            final A stats,
            final StringGenerator<A> formatter) {
        this(new LinkedTree<>(name, stats), formatter);
    }

    private PHolder(final LinkedTree<TName,A> tree) {
        this(tree, null);
    }

    private PHolder(
            final LinkedTree<TName,A> tree,
            final StringGenerator<A> formatter) {
        this.tree = tree;
        this.formatter = formatter;
    }

    /* test only */ LinkedTree<TName,A> getTree() {
        return tree;
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
        ((Tree<TName,T>)tree).<TName,T>traverseLeaves((
            Tree<TName, T> t) -> {
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
    public PHolder<A> use(PerformanceConsumer<A> consumer) {
        if (consumer != null) {
            traverseLeaves((TName name, A assertable) -> {
                consumer.consume(assertable);
            });
        }
        return this;
    }

    /**
     * Checks the assertion on all tests.
     *
     * @param assertion to be checked
     * @return {@code this}
     */
    public PHolder<A> check(Assertion<A> assertion) {
        return use(assertion);
    }

    public PHolder<A> addAssertion(Assertion<A> assertion) {
        assertions.add(assertion);
        return this;
    }

    public PHolder<A> clearAssertions() {
        assertions.clear();
        return this;
    }

    //TODO use CallBackBuilder to evaluate assertion directly
    public TNameMatcherAssertion<PHolder<A>, A> addAssertion() {
        final TNameMatcherAssertion<PHolder<A>, A> assertion =
                new TNameMatcherAssertion<>(this);
        addAssertion(assertion);
        return assertion;
    }

    public PHolder<A> checkAssertions() {
        traverseLeaves((TName name, A assertable) -> {
            for (Assertion<A> a : assertions) {
                a.check(assertable);
            }
        });
        return this;
    }

    public void evaluateAssertionsTo(Appendable appendable) {
        final AppendableWrapperSentinel wrapped =
                new AppendableWrapperSentinel(appendable);
        if (appendable != null) {
            traverseLeaves((TName name, A assertable) -> {
                for (Assertion<A> a : assertions) {
                    try {
                        wrapped.setUnmodified();
                        a.append(wrapped, assertable);
                        if (wrapped.isModified()) {
                            appendable.append(System.lineSeparator());
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    }

    public LinkedMap<TName, Assertable> getFlattenedAssertableMap() {
        LinkedMap<TName, Assertable> map = new LinkedMap<>();
        traverseLeaves((TName name, A assertable) -> {
            map.put(name, assertable);
        });
        return map;
    }

    /**
     * Prints the statistics to standard output if the {@code condition} is
     * true.
     */
    public PHolder<A> printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    /**
     * Prints the statistics to standard output.
     */
    public PHolder<A> print() {
        printTo(System.out);
        return this;
    }


    public PHolder<A> printTo(final Appendable appendable) {
        if (appendable != null) {
            try {
                appendable.append(toString()).append(System.lineSeparator());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
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
        final PHolder<?> other = (PHolder<?>) obj;
        if (!Objects.equals(this.tree, other.tree)) {
            return false;
        }
        if (!Objects.equals(this.formatter, other.formatter)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString(buf, tree);
        return buf.toString();
    }

    private void toString(StringBuilder buf, Tree<TName,A>  tree) {
        TName title = tree.getKey();
        if (title != null && !title.isEmpty()) {
            buf.append(System.lineSeparator());
            buf.append(TableFormatter.title(
                    title.toStringWithSeparator(SEPARATOR),
                    tree.isLeaf() ? '-' : '='));
        }
        if (tree.isLeaf()) {
            toStringLeaf(buf, tree.getValue());
        } else {
            for (Tree<TName,A> branch : tree) {
                toString(buf, branch);
            }
        }
    }

    private void toStringLeaf(StringBuilder buf, A assertable) {
        if (formatter != null) {
            formatter.append(buf, assertable);
        } else {
            buf.append(Objects.toString(assertable))
                    .append(System.lineSeparator());
        }
    }
}
