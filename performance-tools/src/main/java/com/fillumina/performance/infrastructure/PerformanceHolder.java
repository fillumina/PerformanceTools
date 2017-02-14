package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.Holder;
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
public class PerformanceHolder<A extends Assertable>
        implements Iterable<PerformanceHolder<A>>, Serializable {
    private static final long serialVersionUID = 1L;

    private static final PerformanceHolder<?> EMPTY =
            new PerformanceHolder<>((ComposedName)null, (Assertable)null);

    private final LinkedTree<ComposedName, A> tree;
    private final StringGenerator<A> formatter;


    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <S extends Assertable> PerformanceHolder<S> empty() {
        return (PerformanceHolder<S>) EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <S extends Assertable> PerformanceHolder<S>
            createWithValue(S stats) {
        return new PerformanceHolder<>(stats);
    }

    public PerformanceHolder(final A stats) {
        this(null, stats, null);
    }

    public PerformanceHolder(final ComposedName name, final A stats) {
        this(name, stats, null);
    }

    public PerformanceHolder(final ComposedName name,
            final StringGenerator<A> formatter) {
        this(name, null, formatter);
    }

    public PerformanceHolder(final ComposedName name,
            final A stats,
            final StringGenerator<A> formatter) {
        this(new LinkedTree<>(name, stats), formatter);
    }

    private PerformanceHolder(final LinkedTree<ComposedName,A> tree) {
        this(tree, null);
    }

    private PerformanceHolder(final LinkedTree<ComposedName,A> tree,
            final StringGenerator<A> formatter) {
        this.tree = tree;
        this.formatter = formatter;
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
    public void addChild(PerformanceHolder<? extends Assertable> performance) {
        final LinkedTree<ComposedName, A> otherTree =
                (LinkedTree<ComposedName, A>) performance.tree;
        if (otherTree.isEmpty()) {
            tree.put(otherTree.getKey(), otherTree.getValue());
        } else {
            tree.addChild(otherTree);
        }
    }

    @Override
    public Iterator<PerformanceHolder<A>> iterator() {
        return new Iterator<PerformanceHolder<A>>() {
            private final Iterator<Tree<ComposedName,A>> it = tree.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public PerformanceHolder<A> next() {
                return new PerformanceHolder<>((A)it.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    public interface PerformanceVisitor<A> {
        void visitStats(ComposedName name, A stats);
    }

    public void traverse(final PerformanceVisitor<A> visitor) {
        tree.traverseDepthFirst(new Visitor<Tree<ComposedName,A>>() {
            @Override
            public boolean visit(Tree<ComposedName, A> tree) {
                if (tree.isLeaf()) {
                    visitor.visitStats(tree.getKey(), tree.getValue());
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
    public PerformanceHolder<A> getLeaf(final ComposedName cname) {
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
        return new PerformanceHolder<>(cname, holder.getValue());
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @param consumers
     * @return {@code this}
     */
    public PerformanceHolder<A> use(PerformanceConsumer<A> consumer) {
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
    public PerformanceHolder<A> check(Assertion<A> assertion) {
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
    public PerformanceHolder<A> checkAndPrint(Appendable appendable,
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
    public PerformanceHolder<A> printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    public PerformanceHolder<A> print() {
        printTo(System.out);
        return this;
    }


    public PerformanceHolder<A> printTo(final Appendable appendable) {
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
