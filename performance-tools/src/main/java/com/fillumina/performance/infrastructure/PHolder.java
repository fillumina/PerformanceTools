package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.instrument.TelescopicGenerics;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.tree.LinkedTree;
import com.fillumina.performance.util.tree.Tree;
import com.fillumina.performance.util.tree.Visitor;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;

/**
 * Container for statistics.
 * <p>
 * Statistic results implement the {@link Assertable} interface and can be
 * either simple or complex:
 * <ul>
 * <li><b>Simple</b> statistics contain measures of named experiments in a
 * a single object.
 * <li><b>Complex</b> statistics can describe the
 * results of complicated experiments including parameters of several orders.
 * These results are returned as trees where each leaf
 * represents a single experiment and each branch represents a different
 * parameter.
 * There could be many branches of many orders.
 * </ul>
 * This class manages both types of results in a uniform way by
 * wrapping the tree representation and allowing operations on it.
 * At the same time the use of {@link TelescopicGenerics} allows to
 * statically manage the type of the tree.
 *
 * @param Assertable the type of the statistics. To represent the tree
 *        this type must be telescopic. So in case of simple statistics
 *        it can be {@code PHolder<Sample>}, and in case of a complex
 *        statistics with parameters: {@code PHolder<PHolder<Stats>>}.
 *
 * @author Francesco Illuminati
 */
public class PHolder<A extends Assertable>
        implements Iterable<A>,
                   TelescopicGenerics<PHolder<A>>,
                   Assertable,
                   Serializable {

    private static final long serialVersionUID = 1L;

    private static final PHolder<?> EMPTY =
            new PHolder<Assertable>((ComposedName)null, (Assertable)null) {
                private static final long serialVersionUID = 1L;
                @Override
                public void addChild(PHolder<? extends Assertable> performance) {
                    // do nothing
                }
            };

    private final LinkedTree<ComposedName, A> tree;
    private final StringGenerator<A> formatter;


    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <S extends Assertable> PHolder<S> empty() {
        return (PHolder<S>) EMPTY;
    }

    public static <S extends Assertable> PHolder<S>
            createWithValue(S stats) {
        return new PHolder<>(stats);
    }

    public PHolder(final ComposedName name) {
        this(name, null, null);
    }

    public PHolder(final A stats) {
        this(null, stats, null);
    }

    public PHolder(final ComposedName name, final A stats) {
        this(name, stats, null);
    }

    public PHolder(final ComposedName name,
            final StringGenerator<A> formatter) {
        this(name, null, formatter);
    }

    public PHolder(final ComposedName name,
            final A stats,
            final StringGenerator<A> formatter) {
        this(new LinkedTree<>(name, stats), formatter);
    }

    private PHolder(final LinkedTree<ComposedName,A> tree) {
        this(tree, null);
    }

    private PHolder(final LinkedTree<ComposedName,A> tree,
            final StringGenerator<A> formatter) {
        this.tree = tree;
        this.formatter = formatter;
    }

    /** Not implemented: it is only used to implement {@link Assertable}. */
    @Override
    public Measure getValue(String testName) {
        throw new UnsupportedOperationException();
    }

    /** Not implemented: it is only used to implement {@link Assertable}. */
    @Override
    public MeasureRatio getRatioWithSlowestTest(final String testName) {
        throw new UnsupportedOperationException();
    }

    /** @return true if no statistics available. */
    public boolean isNull() {
        return tree.isNull();
    }

    /** @return true if has no children. */
    public boolean isChildless() {
        return tree.isEmpty();
    }

    public ComposedName getName() {
        return tree.getKey();
    }

    public A getStats() {
        return tree.getValue();
    }

    /**
     * Inserts a new subtree.
     * <p>
     * WARNING! inserting a {@link Tree<K,V>} with a null name is not allowed.
     *
     * @param performance
     * @throws IllegalStateException if the new tree lacks a name
     */
    @SuppressWarnings("unchecked")
    public void addChild(PHolder<? extends Assertable> performance) {
        final LinkedTree<ComposedName, A> otherTree =
                (LinkedTree<ComposedName, A>) performance.tree;
        if (otherTree.getKey() == null) {
            throw new IllegalStateException("performances must be named");
        }
        tree.addChild(otherTree);
    }

    @Override
    public Iterator<A> iterator() {
        return new Iterator<A>() {
            private final Iterator<Tree<ComposedName,A>> it = tree.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public A next() {
                final Tree<ComposedName, A> next = it.next();
                return (A) new PHolder<>((LinkedTree<ComposedName,A>)next);
            }

            @Override
            public void remove() {
                it.remove();
            }

        };
    }

    public interface LeafVisitor<T extends Assertable> {
        void visitLeaf(ComposedName name, T stats);
    }

    /**
     *
     * @param <T>     the type of the leaves
     * @param visitor the visitor
     */
    @SuppressWarnings("unchecked")
    public <T extends Assertable> void traverseLeaves(
            final LeafVisitor<T> visitor) {
        ((Tree<ComposedName,T>)tree).traverseDepthFirst(
                new Visitor<Tree<ComposedName,T>>() {
            @Override
            public boolean visit(Tree<ComposedName, T> tree) {
                if (tree.isLeaf()) {
                    visitor.visitLeaf(tree.getKey(), tree.getValue());
                }
                return false;
            }
        });
    }

    /**
     * Returns the element found following the path specified by the
     * composed name.
     * @param cname the path
     * @return
     */
    public PHolder<A> getLeaf(final ComposedName cname) {
        if (cname == null) {
            return null;
        }
        final Holder<A> holder = new Holder<>();
        tree.traverseDepthFirst(new Visitor<Tree<ComposedName,A>>() {
            @Override
            public boolean visit(Tree<ComposedName, A> t) {
                if (t.isLeaf()) {
                    final ComposedName name = t.getKey();
                    final A stats = t.getValue();
                    if (stats != null && cname.equals(name)) {
                        holder.setValue(stats);
                        return true;
                    }
                }
                return false;
            }
        });
        return new PHolder<>(cname, holder.getValue());
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @param consumers
     * @return {@code this}
     */
    public PHolder<A> use(PerformanceConsumer<A> consumer) {
        if (consumer != null) {
            consumer.consume(this);
        }
        return this;
    }

    /**
     * Check the assertion
     *
     * @param assertion to be checked
     * @return {@code this}
     */
    public PHolder<A> check(Assertion<A> assertion) {
        if (assertion != null) {
            assertion.check(this);
        }
        return this;
    }

    /**
     * Check the assertion
     *
     * @see #whenever(boolean)
     * @param assertion to be checked
     * @return {@code this}
     */
    public PHolder<A> checkAndPrint(Appendable appendable,
            Assertion<A> assertion) {
        if (assertion != null) {
            assertion.check(this);
            if (appendable != null) {
                try {
                    appendable
                            .append(System.lineSeparator())
                            .append(assertion.toString(this))
                            .append(System.lineSeparator());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
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
        if (formatter != null) {
            return formatter.toString(this);
        } else {
            return getClass().getSimpleName() +
                    "{name=" + getName() +
                    ", value=" + Objects.toString(getStats()) + "}";
        }
    }

}
