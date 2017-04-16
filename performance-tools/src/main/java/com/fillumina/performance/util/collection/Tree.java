package com.fillumina.performance.util.collection;

import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO rename to SimpleTree
public interface Tree<K,V> extends Iterable<Tree<K,V>>, Map<K,V>, Entry<K,V> {

    /** @return true if the tree has no children. */
    boolean isLeaf();

    /** @return the maximum level of all the sub trees. */
    int getHeight();

    /** @return the created children. */
    Tree<K,V> addChild(K key, V value);

    /** @return the children with the same key. */
    Tree<K,V> getChild(K key);

    /** @return the removed children. */
    Tree<K,V> removeChild(K key);

    /**
     * Visits the nodes of the tree depth first.
     *
     * @param visitor
     * @return true if you want to stop visiting
     */
    boolean traverseDepthFirst(Visitor<Tree<K,V>> visitor);

    /**
     * Visits the nodes of the tree breadth first (i.e. by level).
     *
     * @param visitor
     * @return true if you want to stop visiting
     */
    boolean traverseBreadthFirst(Visitor<Tree<K,V>> visitor);
}
