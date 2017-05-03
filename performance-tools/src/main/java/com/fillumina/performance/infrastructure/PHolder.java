package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TNameMatcher;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.collection.Visitor;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * Container for statistics.
 *
 * @author Francesco Illuminati
 */
public class PHolder<A extends Assertable> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " : ";
    private static final String ASSERTION_ERROR_PREFIX =
            "***** ASSERTION ERROR:";

    public static class Builder<A extends Assertable> {
        private LinkedTree<TName, A> tree;
        private LinkedTree<TName, A> current;
        private StringGenerator<A> generator;

        public Builder(TName tname,
                A assertable,
                StringGenerator<A> generator) {
            this.tree = new LinkedTree<>(tname, assertable);
            this.current = this.tree;
            this.generator = generator;
        }

        public Builder<A> addChild(PHolder<A> holder) {
            current.addSubTree(holder.tree);
            return this;
        }

        public Builder<A> name(String name) {
            current.put(tname(name), null);
            return this;
        }

        public Builder<A> branch(String name) {
            current = current.addTree(tname(name), null);
            return this;
        }

        public Builder<A> leaf(String name, A assertable) {
            current.put(tname(name), assertable);
            return this;
        }

        private TName tname(String name) {
            return current.getKey().append(name);
        }

        public Builder<A> end() {
            current = current.getParent();
            return this;
        }

        @SuppressWarnings("unchecked")
        public PHolder<A> build() {
            return new PHolder<>(tree, generator);
        }
    }

    private static final PHolder<?> EMPTY =
            new PHolder<Assertable>((TName)null, (Assertable)null);

    private final LinkedTree<TName, A> tree;
    private final StringGenerator<A> formatter;

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> builder() {
        return builder(TN.EMPTY, null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> builder(String name) {
        return builder(TN.n(name), null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> builder(TName tname) {
        return builder(tname, null, null);
    }

    /** @return a builder to create a tree statistics */
    public static <A extends Assertable> Builder<A> builder(TName name,
            A assertable,
            StringGenerator<A> stringGenerator) {
        return new Builder<>(name, assertable, stringGenerator);
    }

    /** @return an empty immutable object. */
    @SuppressWarnings("unchecked")
    public static <S extends Assertable> PHolder<S> empty() {
        return (PHolder<S>) EMPTY;
    }

    public static <S extends Assertable> PHolder<S> createWithValue(S stats) {
        return new PHolder<>(stats);
    }

    public PHolder(final TName name) {
        this(name, null, null);
    }

    public PHolder(final A stats) {
        this(null, stats, null);
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
    public boolean isNull() {
        return tree.isNull();
    }

    /** @return true if has no children. */
    public boolean isChildless() {
        return tree.isEmpty();
    }

    /** @return the name of the test. */
    public TName getName() {
        return tree.getKey();
    }

    public A getStats() {
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
        ((Tree<TName,T>)tree).traverseDepthFirst(new Visitor<Tree<TName,T>>() {
                    @Override
                    public boolean visit(Tree<TName, T> tree) {
                        if (tree.isLeaf()) {
                            visitor.visitLeaf(tree.getKey(), tree.getValue());
                        }
                        return false;
                    }
                });
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @param consumers
     * @return {@code this}
     */
    public PHolder<A> use(PerformanceConsumer<A> consumer) {
        return use(null, consumer);
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @param consumers
     * @return {@code this}
     */
    public PHolder<A> use(TNameMatcher matcher,
            PerformanceConsumer<A> consumer) {
        if (consumer != null) {
            traverseLeaves((TName name, A assertable) -> {
                if (matcher == null || matcher.matches(name)) {
                    consumer.consume(name, assertable);
                }
            });
        }
        return this;
    }

    /**
     * Checks the assertion on all tests (results are printed on standard
     * output).
     *
     * @param assertion to be checked
     * @return {@code this}
     */
    public PHolder<A> check(Assertion<A> assertion) {
        return check(null, null, assertion);
    }

    /**
     * Check the assertion
     *
     * @see #whenever(boolean)
     * @param assertion to be checked
     * @return {@code this}
     */
    public PHolder<A> check(TNameMatcher matcher, Assertion<A> assertion) {
        return check(null, matcher, assertion);
    }

    public PHolder<A> check(Appendable appendable, Assertion<A> assertion) {
        return check(appendable, null, assertion);
    }

    /**
     * Checks the assertion on selected tests and append result messages to the
     * given appendable.
     *
     * @param appendable to append messages (null means {@link System#out}).
     * @param matcher condition to select the tests to apply the assertion to
     * @param assertion to be checked
     * @return {@code this}
     */
    public PHolder<A> check(Appendable appendable,
            TNameMatcher matcher,
            Assertion<A> assertion) {
        final AppendableWrapper buf = appendable != null ?
                new AppendableWrapper(appendable) : null;
        if (assertion != null) {
            traverseLeaves((TName name, A assertable) -> {
                if (matcher == null || matcher.matches(name)) {
                    try {
                        assertion.consume(name, assertable);
                    } catch (AssertionError er) {
                        if (buf != null) {
                            buf.append(appendable, ASSERTION_ERROR_PREFIX);
                            buf.append(appendable, er.getMessage());
                        } else {
                            throw er;
                        }
                    }
                    assertion.append(appendable, assertable);
                }
            });
        }
        return this;
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
        if (!title.isEmpty()) {
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
