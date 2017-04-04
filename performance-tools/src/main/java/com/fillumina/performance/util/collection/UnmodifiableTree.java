package com.fillumina.performance.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableTree<K,V> extends TreeWrapper<K,V> {

    public UnmodifiableTree(Tree<K,V> delegate) {
        super(delegate);
    }

    @Override
    public Iterator<Tree<K,V>> iterator() {
        return new Iterator<Tree<K,V>>() {
            private final Iterator<Tree<K,V>> it =
                    UnmodifiableTree.super.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Tree<K,V> next() {
                return new UnmodifiableTree<>(it.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    @Override
    public boolean traverseBreadthFirst(final Visitor<Tree<K, V>> visitor) {
        return super.traverseBreadthFirst(new Visitor<Tree<K, V>>() {
            @Override
            public boolean visit(Tree<K, V> tree) {
                return visitor.visit(new UnmodifiableTree<>(tree));
            }
        });
    }

    @Override
    public boolean traverseDepthFirst(final Visitor<Tree<K, V>> visitor) {
        return super.traverseBreadthFirst(new Visitor<Tree<K, V>>() {
            @Override
            public boolean visit(Tree<K, V> tree) {
                return visitor.visit(new UnmodifiableTree<>(tree));
            }
        });
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tree<K,V> getChild(K key) {
        return new UnmodifiableTree<>(super.getChild(key));
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tree<K, V> createChild(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tree<K, V> removeChild(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
