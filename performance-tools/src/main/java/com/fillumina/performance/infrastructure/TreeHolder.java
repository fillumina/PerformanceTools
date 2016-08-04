package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.ComposedNamedTree;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ComposedName;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * It's an helper useful in case of
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent interfaces
 * </a></i> which are
 * extensively used by this API. It allows to process a performance
 in place without having to use a variable or to enclose a long chain of
 methods as a parameter.
 *
 * @param T the tree
 * @param S the leaves of the tree
 * @author Francesco Illuminati
 */
public class TreeHolder<S,T>
        implements ComposedNamedTree<S>, Serializable {
    private static final long serialVersionUID = 1L;
    public static final TreeHolder<?,?> EMPTY =
            new TreeHolder<>(null);

    private final T tree;
    private final ComposedName name;
    private final StringGenerator<T> formatter;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <S,T> TreeHolder<S,T> empty() {
        return (TreeHolder<S,T>) EMPTY;
    }

    public TreeHolder(final T stats) {
        this(null, stats, null);
    }

    public TreeHolder(final ComposedName name,
            final T tree,
            final StringGenerator<T> formatter) {
        this.name = name;
        this.tree = tree;
        this.formatter = formatter;
    }

    /** There are no performance available. */
    public boolean isEmpty() {
        return tree == null ||
                (tree instanceof Map && ((Map)tree).isEmpty());
    }

    /** *  Use this method to getTree the enclosed {@link SpeedSample}. */
    public T getTree() {
        return tree;
    }

    /**
     * Pass the performance directly to the consumer.
     *
     * @see #whenever(boolean)
     * @param consumers
     * @return {@code this}
     */
    public TreeHolder<S,T> use(PerformanceConsumer<T> consumer) {
        if (consumer != null) {
            consumer.consume(name, tree);
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
    public TreeHolder<S,T> check(Assertion<T> assertion) {
        if (assertion != null) {
            assertion.check(getTree());
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
    public TreeHolder<S,T> checkAndPrintIf(Appendable appendable,
            Assertion<T> assertion) {
        if (assertion != null) {
            assertion.check(getTree());
            if (appendable != null) {
                try {
                    appendable
                            .append("ASSERTION:")
                            .append(System.lineSeparator())
                            .append(toString())
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
    public TreeHolder<S,T> printIf(final boolean condition) {
        if (condition) {
            print();
        }
        return this;
    }

    public TreeHolder<S,T> print() {
        print(System.out);
        return this;
    }


    public TreeHolder<S,T> print(final Appendable appendable) {
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
            return formatter.toString(name, tree);
        }
        return tree.toString();
    }

    /**
     * Returns the element found following the path specified by the
     * composed name.
     * @param name the path
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public S get(ComposedName name) {
        if (name == null) {
            return (S) tree;
        }
        Object current = tree;
        for (ComposedName cn : name.asComposedNameList()) {
            if (current instanceof Map) {
                current = ((Map<ComposedName,?>)current).get(cn);
            } else if (current != null) {
                return (S) current;
            }
        }
        return (S) current;
    }

    public interface Visitor<S> {
        void visitTitle(int level, ComposedName name);
        void visitStats(ComposedName name, S stats);
    }

    public void traverse(Visitor<S> visitor) {
        new Traverser<>(visitor).visit(name, tree, -1);
    }

    private static class Traverser<S> {
        private final Visitor<S> visitor;

        public Traverser(Visitor<S> visitor) {
            this.visitor = visitor;
        }

        @SuppressWarnings("unchecked")
        public void visit(ComposedName name, Object value, int level) {
            if (value instanceof Map) {
                visitor.visitTitle(level, name);
                int nestedLevel = level + 1;
                for (Map.Entry<ComposedName, ?> entry :
                        ((Map<ComposedName, ?>)value).entrySet()) {
                    visit(entry.getKey(), entry.getValue(), nestedLevel);
                }

            } else if (value != null) {
                visitor.visitStats(name, (S) value);

            } else {
                throw new NullPointerException("unexepected null value in tree");
            }
        }
    }

}
