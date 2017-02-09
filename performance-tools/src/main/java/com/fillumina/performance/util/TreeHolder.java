package com.fillumina.performance.util;

import java.io.Serializable;
import java.util.Map;

/**
 * It's an helper useful in case of
 * <i><a href='http://en.wikipedia.org/wiki/Fluent_interface'>fluent interfaces
 * </a></i> which are
 * extensively used by this API. It allows to process a tree
 * in place without having to use a variable or to enclose a long chain of
 * methods as a parameter.
 *
 * @param T the tree
 * @param L the leaves of the tree
 * @author Francesco Illuminati
 */
public class TreeHolder<L,T>
        implements ComposedNamed<L>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final TreeHolder<?,?> EMPTY = new TreeHolder<>(null);

    private final T tree;
    private final ComposedName name;

    /**
     * Returns an empty object. Note that holders are not final classes so
     *  a static object cannot be shared.
     */
    @SuppressWarnings("unchecked")
    public static <L,T> TreeHolder<L,T> empty() {
        return (TreeHolder<L,T>) EMPTY;
    }

    public TreeHolder(final T tree) {
        this(null, tree);
    }

    public TreeHolder(final ComposedName name, final T tree) {
        this.name = name;
        this.tree = tree;
    }

    public boolean isEmpty() {
        return tree == null ||
                (tree instanceof Map && ((Map)tree).isEmpty());
    }

    public T getTree() {
        return tree;
    }

    public ComposedName getName() {
        return name;
    }

    /**
     * Returns the element found following the path specified by the
     * composed name.
     * @param cname the path
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public L get(ComposedName cname) {
        if (cname == null) {
            return (L) tree;
        }
        Object current = tree;
        for (int i=1; i<=cname.size(); i++) {
            if (current instanceof Map) {
                final ComposedName levelName = cname.getComposedNameAtIndex(i);
                final Object value = ((Map<ComposedName,?>)current).get(levelName);
                if (value != null) {
                    current = value;
                }
            }
        }
        return (L) current;
    }

    public interface Visitor<S> {
        void visitTitle(int level, ComposedName name);
        void visitStats(ComposedName name, S stats);
    }

    public void traverse(Visitor<L> visitor) {
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
