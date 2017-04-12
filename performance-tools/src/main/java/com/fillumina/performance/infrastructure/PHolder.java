package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.AbstractAssertable;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.StaticPath;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.collection.Visitor;
import com.fillumina.performance.util.instrument.TelescopicGenerics;
import com.fillumina.performance.util.stats.Measure;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Container for statistics.
 * <p>
 * Statistic results implement the {@link Assertable} interface and can be
 * either simple or complex:
 * <ul>
 * <li><b>Simple</b> statistics contain measures of named experiments in a
 * a single object.
 * <li><b>Complex</b> statistics describes the
 * results of more complicated experiments using parameters.
 * These results are returned as trees where each leaf
 * represents a single experiment and each branch represents a different
 * parameter.
 * </ul>
 * This class manages both types of results in a uniform way by
 * wrapping the tree representation and allowing operations on it.
 * At the same time the use of {@link TelescopicGenerics} allows to
 * statically manage the type of the tree.
 *
 * @param Assertable the type of the statistics. To represent the tree
 *        this type must be telescopic. So in case of simple statistics
 *        it can be {@code PHolder<Sample>}, in case of a complex
 *        statistics with parameters use: {@code PHolder<PHolder<Stats>>}.
 *
 * @author Francesco Illuminati
 */
public class PHolder<A extends Assertable>
        extends AbstractAssertable
        implements Iterable<A>,
                   TelescopicGenerics<PHolder<A>>,
                   Assertable,
                   Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String, Measure> measureMap;

    public static class Builder<A extends Assertable> {
        private final Builder<A> parent;
        private final PHolder<A> holder;

        private Builder(String name) {
            this(null, name, null);
        }

        private Builder(Builder<A> parent, String name, A assertable) {
            this.parent = parent;
            if (parent != null) {
                StaticPath cname = parent.holder.getName().append(name);
                this.holder = new PHolder<>(cname, assertable);
                parent.holder.addChild(holder);
            } else {
                this.holder = new PHolder<>(CName.EMPTY.append(name));
            }
        }

        public Builder<A> name(String name) {
            return new Builder<>(this, name, null);
        }

        public Builder<A> branch(String name) {
            return new Builder<>(this, name, null);
        }

        public Builder<A> leaf(String name, A assertable) {
            new Builder<>(this, name, assertable);
            return this;
        }

        public Builder<A> end() {
            return parent;
        }

        @SuppressWarnings("unchecked")
        public <T extends Assertable> PHolder<T> getRoot() {
            Builder<A> root = this;
            while (root.parent != null) {
                root = root.parent;
            }
            return (PHolder<T>) root.holder;
        }
    }

    private static final PHolder<?> EMPTY =
            new PHolder<Assertable>((StaticPath)null, (Assertable)null) {
                private static final long serialVersionUID = 1L;
                @Override
                public void addChild(PHolder<? extends Assertable> performance) {
                    throw new UnsupportedOperationException();
                }
            };

    private final LinkedTree<StaticPath, A> tree;
    private final StringGenerator<A> formatter;

    public static <A extends Assertable> Builder<A> build(String name) {
        return new Builder<>(name);
    }

    public static <A extends Assertable> PHolder<A> create(String name) {
        return new PHolder<>(CName.EMPTY.append(name));
    }

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <S extends Assertable> PHolder<S> empty() {
        return (PHolder<S>) EMPTY;
    }

    public static <S extends Assertable> PHolder<S> createWithValue(S stats) {
        return new PHolder<>(stats);
    }

    public PHolder(final StaticPath name) {
        this(name, null, null);
    }

    public PHolder(final A stats) {
        this(null, stats, null);
    }

    public PHolder(final StaticPath name, final A stats) {
        this(name, stats, null);
    }

    public PHolder(final StaticPath name,
            final StringGenerator<A> formatter) {
        this(name, null, formatter);
    }

    public PHolder(final StaticPath name,
            final A stats,
            final StringGenerator<A> formatter) {
        this(new LinkedTree<>(name, stats), formatter);
    }

    private PHolder(final LinkedTree<StaticPath,A> tree) {
        this(tree, null);
    }

    private PHolder(
            final LinkedTree<StaticPath,A> tree,
            final StringGenerator<A> formatter) {
        this.tree = tree;
        this.formatter = formatter;
    }

    @Override
    public Collection<String> getTestNames() {
        return getMeasureMap().keySet();
    }

    @Override
    public Measure getMeasure(String testName) {
        return getMeasureMap().get(testName);
    }

    private Map<String,Measure> getMeasureMap() {
        if (measureMap == null) {
            final Map<String, Measure> map = new LinkedHashMap<>();
            traverseLeaves(new LeafVisitor<Assertable>() {
                @Override
                public void visitLeaf(StaticPath treeName, Assertable stats) {
                    for (String testName : stats.getTestNames()) {
                        String name = treeName.toString() + "." + testName;
                        map.put(name, stats.getMeasure(testName));
                    }
                }
            });
            measureMap = map;
        }
        return measureMap;
    }

    /** @return true if no statistics available. */
    public boolean isNull() {
        return tree.isNull();
    }

    /** @return true if has no children. */
    public boolean isChildless() {
        return tree.isEmpty();
    }

    public StaticPath getName() {
        return tree.getKey();
    }

    /**
     * @return the statistics associated with root node of the tree so it
     * is most probably null (with the current default implementation)
     * if it isn't a leaf.
     */
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
        final LinkedTree<StaticPath, A> otherTree =
                (LinkedTree<StaticPath, A>) performance.tree;
        if (otherTree.getKey() == null) {
            throw new IllegalStateException("performances must be named");
        }
        tree.addChild(otherTree);
    }

    /**
     * The value returned by {@link Iterator#next()} is always wrapped into a
     * {@link PHolder}. This iterator is <b>not</b> to be used with leaves
     * (because it will wraps them into {@link PHolder}).
     */
    @Override
    public Iterator<A> iterator() {
        return new Iterator<A>() {
            private final Iterator<Tree<StaticPath,A>> it = tree.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public A next() {
                final Tree<StaticPath, A> next = it.next();
                return (A) new PHolder<>((LinkedTree<StaticPath,A>)next);
            }

            @Override
            public void remove() {
                it.remove();
            }

        };
    }

    public interface LeafVisitor<T extends Assertable> {
        void visitLeaf(StaticPath name, T stats);
    }

    /**
     *
     * @param <T>     the type of the leaves
     * @param visitor the visitor
     */
    @SuppressWarnings("unchecked")
    public <T extends Assertable> void traverseLeaves(
            final LeafVisitor<T> visitor) {
        ((Tree<StaticPath,T>)tree).traverseDepthFirst(
                new Visitor<Tree<StaticPath,T>>() {
                    @Override
                    public boolean visit(Tree<StaticPath, T> tree) {
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
    public PHolder<A> getLeaf(final StaticPath cname) {
        if (cname == null) {
            return null;
        }
        final Holder<A> holder = new Holder<>();
        tree.traverseDepthFirst(new Visitor<Tree<StaticPath,A>>() {
            @Override
            public boolean visit(Tree<StaticPath, A> t) {
                if (t.isLeaf()) {
                    final StaticPath name = t.getKey();
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
