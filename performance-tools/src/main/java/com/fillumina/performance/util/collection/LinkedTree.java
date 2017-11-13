package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link Tree} with low memory requirements. Every node implements
 * a {@link Map} interface and can iterate through its children.
 * Insertion order is preserved.
 * <p>
 * This class is not thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedTree<K,V> implements Tree<K,V>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Tree<Object, Object> EMPTY =
            UnmodifiableTree.wrap(new LinkedTree<>());

    public class OrderedLinkedTree<K,V> extends LinkedTree<K,V> {
        private static final long serialVersionUID = 1L;
        private final Predicate<Tree<K,V>> orderSelector;

        public OrderedLinkedTree(Predicate<Tree<K, V>> orderSelector) {
            this.orderSelector = orderSelector;
        }

        public OrderedLinkedTree(Predicate<Tree<K, V>> orderSelector,
                LinkedTree<K, V> clone) {
            super(clone);
            this.orderSelector = orderSelector;
        }

        public OrderedLinkedTree(Predicate<Tree<K, V>> orderSelector,
                Map<K, V> map) {
            super(map);
            this.orderSelector = orderSelector;
        }

        public OrderedLinkedTree(Predicate<Tree<K, V>> orderSelector,
                Collection<? extends Entry<K, V>> copy) {
            super(copy);
            this.orderSelector = orderSelector;
        }

        public OrderedLinkedTree(Predicate<Tree<K, V>> orderSelector,
                K key, V value) {
            super(key, value);
            this.orderSelector = orderSelector;
        }

        @Override
        protected void addTree(LinkedTree<K, V> tree) {
            super.addTreeAfter(tree, orderSelector);
        }
    }

    public class ReversedLinkedTree<K,V> extends LinkedTree<K,V> {
        private static final long serialVersionUID = 1L;

        public ReversedLinkedTree() {
            super();
        }

        public ReversedLinkedTree(LinkedTree<K, V> clone) {
            super(clone);
        }

        public ReversedLinkedTree(Map<K, V> map) {
            super(map);
        }

        public ReversedLinkedTree(
                Collection<? extends Entry<K, V>> copy) {
            super(copy);
        }

        public ReversedLinkedTree(K key, V value) {
            super(key, value);
        }

        @Override
        protected void addTree(LinkedTree<K, V> tree) {
            addTreeAtBeginning(tree);
        }
    }

    public static class Builder<K,V> {
        private final Builder<K,V> parent;
        private final LinkedTree<K,V> holder;

        private Builder(K key, V value) {
            this.parent = null;
            this.holder = new LinkedTree<>(key, value);
        }

        private Builder(Builder<K,V> parent, K key, V value) {
            this.parent = parent;
            this.holder = new LinkedTree<>(key, value);
            parent.holder.addSubTreeDirectly(holder);
        }

        public Builder<K,V> branch(K key) {
            return new Builder<>(this, key, null);
        }

        public Builder<K,V> branch(K key, V value) {
            return new Builder<>(this, key, value);
        }

        public Builder<K,V> leaf(K key, V value) {
            new Builder<>(this, key, value);
            return this;
        }

        public Builder<K,V> end() {
            return parent;
        }

        @SuppressWarnings("unchecked")
        public LinkedTree<K,V> getRoot() {
            Builder<K,V> root = this;
            while (root.parent != null) {
                root = root.parent;
            }
            return root.holder;
        }
    }

    public static <K,V> Builder<K,V> builder(K k, V v) {
        return new Builder<>(k, v);
    }

    public static <K,V> Builder<K,V> builder() {
        return new Builder<>(null, null);
    }

    private K key;
    private V value;
    private LinkedTree<K,V> parent;
    private LinkedTree<K,V> head; // link to children
    private LinkedTree<K,V> next; // link to siblings

    @SuppressWarnings("unchecked")
    public static final <K,V> Tree<K,V> empty() {
        return (Tree<K, V>) EMPTY;
    }

    public static final <K,X,V,W> Tree<K,X> mergeTrees(
            Tree<K,V> a, Tree<K,W> b, BiFunction<V,W,X> merger) {
        LinkedTree<K,X> out = new LinkedTree<>();
        mergeTrees(out, a, b, merger);
        mergeTrees(out, b, a, (t, u) -> {
            return merger.apply(u, t);
        });
        return out;
    }

    private static <K,X,V,W> void mergeTrees(Tree<K,X> out,
            Tree<K,V> a, Tree<K,W> b,
            BiFunction<V,W,X> merger) {
        a.traverseDepthFirst((Tree<K,V> t) -> {
            List<K> path = t.getPath();
            if (out.getValueAtPath(path) == null) {
                K k = t.getKey();
                V v = t.getValue();
                W w = b.getValueAtPath(path);
                X x = merger.apply(v, w);
                out.putValueAtPath(x, path);
            }
            return false;
        });
    }

    public LinkedTree() {}

    /** Clone constructor. */
    public LinkedTree(LinkedTree<K,V> clone) {
        // parent is not copied, it clones only the subtree
        this.parent = null;
        this.key = clone.key;
        this.value = clone.value;
        addAll(this, clone);
    }

    /** Deep copies all elements from src to dst. */
    public static <K,V> void addAll(Tree<K,V> dst,
            Tree<? extends K, ? extends V> src) {
        for (Tree<? extends K, ? extends V> srcSubTree : src) {
            Tree<K,V> dstSubTree =
                    dst.addTree(srcSubTree.getKey(), srcSubTree.getValue());
            addAll(dstSubTree, srcSubTree);
        }
    }

    public static <K,V,X,Y> LinkedTree<K,V> createFrom(Tree<X,Y> src,
            Function<X, K> keyTransformer, Function<Y, V> valueTransformer) {
        LinkedTree<K,V> result = new LinkedTree<>();
        addAll(result, src, keyTransformer, valueTransformer);
        return result;
    }

    /**
     * Deep copies all elements from src to dst transforming one into the other.
     */
    public static <K,V,X,Y> void addAll(Tree<K,V> dst, Tree<X,Y> src,
            Function<X, K> keyTransformer, Function<Y, V> valueTransformer) {
        for (Tree<X, Y> srcSubTree : src) {
            Tree<K,V> dstSubTree = dst.addTree(
                    keyTransformer.apply(srcSubTree.getKey()),
                    valueTransformer.apply(srcSubTree.getValue()));
            addAll(dstSubTree, srcSubTree, keyTransformer, valueTransformer);
        }
    }

    /** Map import constructor. */
    public LinkedTree(Map<K,V> map) {
        putAll(map);
    }

    /** Collection of entries import constructor. */
    public LinkedTree(Collection<? extends Entry<K,V>> copy) {
        for (Entry<K,V> t : copy) {
            addSubTreeDirectly(createNew(t.getKey(), t.getValue()));
        }
    }

    /** Creates the entry. */
    public LinkedTree(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * <b>Overwrite</b> if you extend the class so the entire tree will use
     * the new class.
     */
    protected LinkedTree<K,V> createNew(K key, V value) {
        return new LinkedTree<>(key, value);
    }

    @Override
    public LinkedTree<K, V> getRoot() {
        LinkedTree<K,V> p = this;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p;
    }

    @Override
    public List<K> getPath() {
        ArrayList<K> list = new ArrayList<>();
        Tree<K,V> p = this;
        while (p != null && !p.isRoot()) {
            list.add(p.getKey());
            p = p.getParent();
        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public V putValueAtPath(V value, K... path) {
        return putValueAtPath(value, Arrays.asList(path));
    }

    @Override
    public V putValueAtPath(V value, List<K> path) {
        Tree<K,V> t = this;
        if (path.isEmpty()) {
            V oldValue = getValue();
            setValue(value);
            return oldValue;
        }
        int size = path.size() - 1;
        for (int i=0; i<size; i++) {
            K k = path.get(i);
            Tree<K,V> n = t.getTree(k);
            if (n == null) {
                t = t.addTree(k, null);
            } else {
                t = n;
            }
        }
        K last = path.get(size);
        V v = t.get(last);
        t.addTree(last, value);
        return v;
    }

    @Override
    public Tree<K,V> getTreeAtPath(K... path) {
        return getTreeAtPath(Arrays.asList(path));
    }

    @Override
    public Tree<K,V> getTreeAtPath(List<K> path) {
        Tree<K,V> t = this;
        for (K k : path) {
            t = t.getTree(k);
            if (t == null) {
                return null;
            }
        }
        return t;
    }

    @Override
    public V getValueAtPath(K... path) {
        return getValueAtPath(Arrays.asList(path));
    }

    @Override
    public V getValueAtPath(List<K> path) {
        Tree<K,V> t = this;
        for (K k : path) {
            t = t.getTree(k);
            if (t == null) {
                return null;
            }
        }
        return t.getValue();
    }

    @Override
    public int getHeight() {
        int max = 0;
        for (Tree<K,V> t : this) {
            int h = 1 + t.getHeight();
            if (h > max) {
                max = h;
            }
        }
        return max;
    }

    /** @return the parent node or null if it is the root. */
    @Override
    public LinkedTree<K,V> getParent() {
        return parent;
    }

    /** @return the next sibling or null if there is none. */
    @Override
    public Tree<K, V> getNextSibling() {
        return next;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V setValue(V value) {
        V oldValue = value;
        this.value = value;
        return oldValue;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void clear() {
        head = null;
    }

    @Override
    public boolean isRoot() {
        return getParent() == null;
    }

    @Override
    public boolean isLeaf() {
        return head == null;
    }

    /** @return true if has null key, null value and no children. */
    public boolean isNull() {
        return key == null && value == null && head == null;
    }

    /** @return true if has no children (follows {@link java.util.Map}) */
    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        int size = 0;
        LinkedTree<K,V> current = this.head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    @Override
    public V put(K key, V value) {
        LinkedTree<K,V> node = getTree(key);
        V oldValue = null;
        if (node != null) {
            oldValue = node.getValue();
            node.setValue(value);
        } else {
            addTree(createNew(key, value));
        }
        return oldValue;
    }

    @Override
    public LinkedTree<K, V> getOrAddTree(K key) {
        LinkedTree<K,V> tree = getTree(key);
        if (tree == null) {
            tree = addTree(key, null);
        }
        return tree;
    }

    @Override
    public LinkedTree<K, V> addTree(K key, V value) {
        return addSubTreeDirectly(createNew(key, value));
    }

    public void merge(LinkedTree<K,V> tree) {
        for (Tree<K,V> t : tree) {
            addTree((LinkedTree<K, V>) t);
        }
    }

    /** Adds a <b>copy</b> of the given tree. */
    public void addSubTree(LinkedTree<K,V> tree) {
        addTree(new LinkedTree<>(tree));
    }

    // never add an external tree because its structure will be modified!!
    private LinkedTree<K,V> addSubTreeDirectly(LinkedTree<K,V> tree) {
        if (tree == this) {
            throw new IllegalArgumentException("trying to add itself");
        }
        if (substituteChildren(tree) == null) {
            addTree(tree);
        }
        return tree;
    }

    /** Preserves insertion order. */
    protected void addTree(LinkedTree<K, V> tree) {
        addTreeAtEnd(tree);
    }

    protected void addTreeAfter(LinkedTree<K,V> tree,
            Predicate<Tree<K,V>> previousSelector) {
        LinkedTree<K,V> last = head;
        if (last == null) {
            head = tree;
        } else {
            while (last.next != null && !previousSelector.test(last)) {
                last = last.next;
            }
            LinkedTree<K,V> lastNext = last.next;
            last.next = tree;
            if (lastNext == null) {
                tree.next = null; // to be sure!
            } else {
                tree.next = lastNext;
            }
        }
        tree.parent = this;
    }

    protected void addTreeAtEnd(LinkedTree<K,V> tree) {
        LinkedTree<K,V> last = head;
        if (last == null) {
            head = tree;
        } else {
            while (last.next != null) {
                last = last.next;
            }
            last.next = tree;
        }
        tree.parent = this;
        tree.next = null; // to be sure!
    }

    /** Reverses insertion order but faster. */
    protected void addTreeAtBeginning(LinkedTree<K,V> tree) {
        tree.next = head;
        head = tree;
        tree.parent = this;
    }

    @Override
    public LinkedTree<K,V> getTreeAtIndex(int index) {
        LinkedTree<K,V> current = head;
        int i = index;
        while (i > 0 && current.next != null) {
            current = current.next;
            i--;
        }
        return current;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        LinkedTree<K,V> result = getTree((K)key);
        if (result != null) {
            return result.getValue();
        }
        return null;
    }

    @Override
    public LinkedTree<K,V> getTree(K key) {
        LinkedTree<K,V> current = head;
        while(current != null) {
            if (current.key == key || current.key.equals(key)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private LinkedTree<K,V> substituteChildren(LinkedTree<K,V> tree) {
        K key = tree.getKey();
        LinkedTree<K,V> current = head;
        LinkedTree<K,V> prev = this;
        while(current != null) {
            if (current.key == key || current.key.equals(key)) {
                if (prev != null) {
                    prev.next = tree;
                    tree.next = current.next;
                }
                current.next = null;
                return current;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        LinkedTree<K,V> removed = removeTree((K)key);
        if (removed != null) {
            return removed.getValue();
        }
        return null;
    }

    @Override
    public LinkedTree<K,V> removeTree(K key) {
        LinkedTree<K,V> current = head;
        LinkedTree<K,V> prev = this;
        while(current != null) {
            if (current.key == key || current.key.equals(key)) {
                if (current == head) {
                    head = current.next;
                } else {
                    prev.next = current.next;
                }
                current.next = null;
                return current;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    private static final LinkedTree<?,?> START =
            new LinkedTree<Object,Object>(null, null);

    @Override
    public Iterator<Tree<K,V>> iterator() {
        return new Iterator<Tree<K,V>>() {
            @SuppressWarnings("unchecked")
            private LinkedTree<K,V> current = (LinkedTree.this.head == null) ?
                    null :
                    (LinkedTree<K,V>)START;
            private LinkedTree<K,V> prev = null;

            @Override
            public boolean hasNext() {
                return current == START ||
                        (current != null && current.next != null);
            }

            @Override
            public Tree<K,V> next() {
                if (current == START) {
                    current = LinkedTree.this.head;
                    return current;
                }
                prev = current;
                current = current.next;
                return current;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void remove() {
                if (current != null && current != START) {
                    if (current == prev) {
                        throw new IllegalStateException(
                                "cannot call remove() twice");
                    }
                    if (prev != null) {
                        prev.next = current.next;
                        current.next = null;
                        current = prev;
                    } else {
                        LinkedTree.this.head = current.next;
                        current.next = null;
                        current = (LinkedTree<K, V>) START;
                    }
                } else {
                    throw new IllegalStateException(
                                "next() was not called first");
                }
            }

        };
    }

    @Override
    public Map<List<K>,V> getFlattenedMap() {
        LinkedHashMap<List<K>, V> map = new LinkedHashMap<>();
        flatten(map);
        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<List<K>,V> flatten(Map<List<K>,V> map) {
        flatten(map,
                (List<K> l) -> {
                    return Arrays.asList((K[])l.toArray());
                },
                new ArrayDeque<>(), this);
        return map;
    }

    @Override
    public <C> Map<C,V> flatten(Map<C,V> map, Function<List<K>,C> converter) {
        flatten(map, converter, new ArrayDeque<>(), this);
        return map;
    }

    private <C> void flatten(
            Map<C, ? super V> map,
            Function<List<K>,C> converter,
            Deque<K> path,
            LinkedTree<K, V> tree) {
        for (Tree<K,V> t : tree) {
            K k = t.getKey();
            V v = t.getValue();
            path.addLast(k);
            @SuppressWarnings("unchecked")
            List<K> ulist = Arrays.asList((K[]) path.toArray());
            C c = converter.apply(ulist);
            map.put(c, v);
            flatten(map, converter, path, (LinkedTree<K,V>)t);
            path.removeLast();
        }
    }

    /** @InheritDoc */
    @Override
    public void traverseLeaves(Visitor<Tree<K, V>> visitor) {
        traverseDepthFirst((t) -> {
            if (t.isLeaf()) {
                visitor.visit(t);
            }
            return false;
        });
    }

    @Override
    public Iterable<Tree<K,V>> depthFirstIterable() {
        return () -> {
            return depthFirstIterator();
        };
    }

    @Override
    public Iterator<Tree<K,V>> depthFirstIterator() {
        return new Iterator<Tree<K,V>>() {
            private LinkedTree<K,V> current;
            private LinkedTree<K,V> nextTree = LinkedTree.this;

            @Override
            public boolean hasNext() {
                return nextTree != null;
            }

            @Override
            public Tree<K, V> next() {
                current = nextTree;
                nextTree = innerNext();
                return current;
            }

            private LinkedTree<K,V> innerNext() {
                LinkedTree<K,V> n = nextTree.head;
                if (n == null) {
                    n = nextTree.next;
                    if (n == null) {
                        LinkedTree<K,V> father = nextTree.getParent();
                        if (father == null) {
                            return null;
                        }
                        n = father.next;
                    }
                }
                return n;
            }

            @Override
            public void remove() {
                if (current != null) {
                    LinkedTree<K,V> father = current.getParent();
                    father.remove(current.getKey());
                    current = null;
                }
            }
        };
    }

    /** @InheritDoc */
    @Override
    public boolean traverseDepthFirst(Visitor<Tree<K,V>> visitor) {
        if (visitor.visit(this)) {
            return true;
        }
        return innerVisitDepthFirst(this, visitor);
    }

    private boolean innerVisitDepthFirst(LinkedTree<K,V> tree,
            Visitor<Tree<K, V>> visitor) {
        for (Tree<K,V> node : tree) {
            LinkedTree<K,V> subTree = (LinkedTree<K,V>) node;
            if (visitor.visit(subTree)) {
                return true;
            }
            if (subTree.innerVisitDepthFirst(subTree, visitor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterable<Tree<K,V>> breathFirstIterable() {
        return () -> {
            return breathFirstIterator();
        };
    }

    @Override
    public Iterator<Tree<K,V>> breathFirstIterator() {
        return new Iterator<Tree<K,V>>() {
            private LinkedTree<K,V> current;
            private LinkedTree<K,V> nextTree = LinkedTree.this;
            // cannot be a set: tree can be repeated on different branches
            private List<Tree<K,V>> visited = new ArrayList<>();
            private int maxDepth;

            @Override
            public boolean hasNext() {
                return nextTree != null;
            }

            @Override
            public Tree<K, V> next() {
                current = nextTree;
                nextTree = innerNext();
                return current;
            }

            private LinkedTree<K,V> innerNext() {
                LinkedTree<K,V> n = nextTree.next;
                if (n != null && !visited.contains(n)) {
                    visited.add(n);
                    return n;
                }
                LinkedTree<K,V> t = nextTree.getRoot();
                do {
                    n = t.head;
                    if (n == null) {
                        n = t.next;
                        if (n == null) {
                            LinkedTree<K,V> father = t.getParent();
                            if (father == null) {
                                return null;
                            }
                            n = father.next;
                        }
                    }
                    t = n;
                } while (n != null && visited.contains(n));
                visited.add(n);
                return n;
            }

            @Override
            public void remove() {
                if (current != null) {
                    LinkedTree<K,V> father = current.getParent();
                    father.remove(current.getKey());
                    current = null;
                }
            }
        };
    }

    /** @InheritDoc */
    @Override
    public boolean traverseBreadthFirst(Visitor<Tree<K,V>> visitor) {
        if (visitor.visit(this)) {
            return true;
        }
        int i=0;
        while (!depthVisit(i, visitor)) {
            i++;
        }
        return false;
    }

    private boolean depthVisit(int depth, Visitor<Tree<K,V>> visitor) {
        if (isNull()) {
            return false;
        }
        boolean noVisit = true;
        int nextDepth = depth - 1;
        for (Tree<K,V> node : this) {
            if (depth > 0) {
                if (((LinkedTree<K,V>)node).depthVisit(nextDepth, visitor)) {
                    return true;
                }
                noVisit = false;
            } else if (depth == 0) {
                if (visitor.visit(node)) {
                    return true;
                }
                noVisit = false;
            }
        }
        return noVisit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.key);
        hash = 53 * hash + Objects.hashCode(this.value);
        hash = 53 * hash + Objects.hashCode(this.next);
        for (Entry<K,V> entry: this) {
            hash = 53 * hash + Objects.hashCode(entry);
        }
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
        final LinkedTree<K, V> other = (LinkedTree<K, V>) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        Iterator<Tree<K,V>> it = iterator();
        Iterator<Tree<K,V>> ot = other.iterator();
        while (it.hasNext()) {
            if (!ot.hasNext()) {
                return false;
            }
            Tree<K, V> itnext = it.next();
            Tree<K, V> otnext = ot.next();
            if (!Objects.equals(itnext, otnext)) {
                return false;
            }
        }
        return !ot.hasNext();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        append(buf, "");
        return buf.toString();
    }

    private void append(StringBuilder buf, String indentation) {
        buf.append(indentation);
        buf.append("{key=").append(Objects.toString(key));
        if (value != null) {
            buf.append(", value=").append(Objects.toString(value));
        }
        if (!isEmpty()) {
            buf.append(", children={").append(System.lineSeparator());
            for (Tree<K,V> child : this) {
                ((LinkedTree<K,V>)child).append(buf, indentation + "   ");
            }
            buf.append(indentation);
        }
        buf.append('}').append(System.lineSeparator());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return getTree((K)key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Tree<K,V> t : this) {
            final V v = t.getValue();
            if ((value == null && v == null) ||
                    (value != null && value.equals(v)) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K,? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Iterator<Tree<K,V>> it = LinkedTree.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public K next() {
                        return it.next().getKey();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return LinkedTree.this.size();
            }

        };
    }

    @Override
    public Collection<V> values() {
        return new AbstractSet<V>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<Tree<K,V>> it = LinkedTree.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public V next() {
                        return it.next().getValue();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return LinkedTree.this.size();
            }

        };
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K,V>>() {
                    private final Iterator<Tree<K,V>> it =
                            LinkedTree.this.iterator();

                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        return (Entry<K,V>) it.next();
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return LinkedTree.this.size();
            }
        };
    }
}
