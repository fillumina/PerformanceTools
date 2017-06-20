package com.fillumina.performance.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TreeWrapper<K,V> implements Tree<K,V> {
    private final Tree<K,V> delegate;

    public TreeWrapper(Tree<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Tree<K, V> getNextSibling() {
        return delegate.getNextSibling();
    }

    @Override
    public Tree<K, V> getParent() {
        return delegate.getParent();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public LinkedTree<K, V> getOrAddTree(K key) {
        return delegate.getOrAddTree(key);
    }

    @Override
    public Tree<K, V> addTree(K key, V value) {
        return delegate.addTree(key, value);
    }

    @Override
    public Tree<K, V> getTree(K key) {
        return delegate.getTree(key);
    }

    @Override
    public Tree<K, V> removeTree(K key) {
        return delegate.removeTree(key);
    }

    @Override
    public boolean traverseDepthFirst(Visitor<Tree<K, V>> visitor) {
        return delegate.traverseDepthFirst(visitor);
    }

    @Override
    public boolean traverseBreadthFirst(Visitor<Tree<K, V>> visitor) {
        return delegate.traverseBreadthFirst(visitor);
    }

    @Override
    public void traverseLeaves(Visitor<Tree<K, V>> visitor) {
        delegate.traverseLeaves(visitor);
    }

    @Override
    public Iterator<Tree<K, V>> iterator() {
        return delegate.iterator();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isLeaf() {
        return delegate.isLeaf();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public void putAll(
            Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public K getKey() {
        return delegate.getKey();
    }

    @Override
    public V getValue() {
        return delegate.getValue();
    }

    @Override
    public V setValue(V value) {
        return delegate.setValue(value);
    }

    @Override
    public Tree<K, V> getTreeAtIndex(int index) {
        return delegate.getTreeAtIndex(index);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
