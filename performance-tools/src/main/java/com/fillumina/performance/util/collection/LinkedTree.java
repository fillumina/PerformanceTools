package com.fillumina.performance.util.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link Tree} with low memory requirements. It's slow but
 * acceptable for few elements.
 * <p>
 * This class is not thread safe.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedTree<K,V> implements Serializable, Tree<K,V> {
    private static final long serialVersionUID = 1L;
    private static final Tree<Object, Object> EMPTY =
            new UnmodifiableTree<>(new LinkedTree<>());

    // TODO finish and test builder
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
            parent.holder.addChild(holder);
        }

        public Builder<K,V> branch(K key) {
            return new Builder<>(this, key, null);
        }

        public Builder<K,V> child(K key, V value) {
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

    private K key;
    private V value;
    private LinkedTree<K,V> head;
    private LinkedTree<K,V> next;

    @SuppressWarnings("unchecked")
    public static final <K,V> Tree<K,V> empty() {
        return (Tree<K, V>) EMPTY;
    }

    public LinkedTree() {}

    /** Clone constructor. */
    public LinkedTree(LinkedTree<K,V> clone) {
        this.key = clone.key;
        this.value = clone.value;
        addAll(this, clone);
    }

    public static <K,V> void addAll(LinkedTree<K,V> tree, LinkedTree<K,V> other) {
        for (Entry<K,V> entry : other) {
            final LinkedTree<K, V> otherSubTree = (LinkedTree<K,V>)entry;
            LinkedTree<K,V> subTree = tree.addChild(otherSubTree);
            addAll(subTree, otherSubTree);
        }
    }

    /** Copy constructor. */
    public LinkedTree(Collection<Tree<K,V>> copy) {
        for (Tree<K,V> t : copy) {
            addChild(createNew(t.getKey(), t.getValue()));
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
    public boolean isLeaf() {
        return head == null;
    }

    /** @return true if has no values and no children */
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
        LinkedTree<K,V> node = getChild(key);
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
    public LinkedTree<K, V> createChild(K key, V value) {
        return addChild(createNew(key, value));
    }

    public LinkedTree<K,V> addChild(K key, V value) {
        return addChild(new LinkedTree<>(key, value));
    }

    public LinkedTree<K,V> addChild(LinkedTree<K,V> tree) {
        if (tree == this) {
            throw new IllegalArgumentException("trying to add itself");
        }
        if (substituteChildren(tree) == null) {
            addTree(tree);
        }
        return tree;
    }

    /** Preserves insertion order. */
    private void addTree(LinkedTree<K, V> tree) {
        LinkedTree<K,V> last = head;
        if (last == null) {
            head = tree;
        } else {
            while (last.next != null) {
                last = last.next;
            }
            last.next = tree;
        }
        tree.next = null; // to be sure!
    }

    /** Reverses insertion order but faster. */
    private void addTreeAtBeginning(LinkedTree<K,V> tree) {
        tree.next = head;
        head = tree;
    }

    @Override
    public V get(Object key) {
        @SuppressWarnings("unchecked")
        LinkedTree<K,V> result = getChild((K)key);
        if (result != null) {
            return result.getValue();
        }
        return null;
    }

    @Override
    public LinkedTree<K,V> getChild(K key) {
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
        LinkedTree<K,V> removed = removeChild((K)key);
        if (removed != null) {
            return removed.getValue();
        }
        return null;
    }

    @Override
    public LinkedTree<K,V> removeChild(K key) {
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
        boolean novisit = true;
        int nextDepth = depth - 1;
        for (Tree<K,V> node : this) {
            if (depth > 0) {
                if (((LinkedTree<K,V>)node).depthVisit(nextDepth, visitor)) {
                    return true;
                }
                novisit = false;
            } else if (depth == 0) {
                if (visitor.visit(node)) {
                    return true;
                }
                novisit = false;
            }
        }
        return novisit;
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
        return getChild((K)key) != null;
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
