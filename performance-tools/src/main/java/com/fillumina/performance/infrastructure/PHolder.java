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

/**
 *
 * @param Assertable the test
 * @author Francesco Illuminati
 */
public class PHolder<A extends Assertable>
        implements Iterable<A>,
                   TelescopicGenerics<PHolder<A>>,
                   Assertable,
                   Serializable {

    private static final long serialVersionUID = 1L;

    private static final PHolder<?> EMPTY =
            new PHolder<>((ComposedName)null, (Assertable)null);

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

    @Override
    public Measure getValue(String testName) {
        if (!tree.isLeaf()) {
            throw new IllegalStateException("not a leaf");
        }
        return ((Assertable)getStats()).getValue(testName);
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        if (!tree.isLeaf()) {
            throw new IllegalStateException("not a leaf");
        }
        return ((Assertable)getStats()).getRatioWithSlowestTest(testName);
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    public ComposedName getName() {
        return tree.getKey();
    }

    public A getStats() {
        return tree.getValue();
    }

    @SuppressWarnings("unchecked")
    public void addChild(PHolder<? extends Assertable> performance) {
        final LinkedTree<ComposedName, A> otherTree =
                (LinkedTree<ComposedName, A>) performance.tree;
        if (otherTree.isEmpty()) {
            tree.put(otherTree.getKey(), otherTree.getValue());
        } else {
            tree.addChild(otherTree);
        }
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
                return (A) new PHolder<>((LinkedTree<ComposedName,A>)it.next());
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
     * @param clazz   the type of the leaves
     * @param visitor the visitor
     */
    public void traverseLeaves(final LeafVisitor<A> visitor) {
        tree.traverseDepthFirst(new Visitor<Tree<ComposedName,A>>() {
            @Override
            public boolean visit(Tree<ComposedName, A> tree) {
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
    public String toString() {
        if (formatter != null) {
            return formatter.toString(this);
        } else {
            return getClass().getSimpleName() + "{}";
        }
    }

}
