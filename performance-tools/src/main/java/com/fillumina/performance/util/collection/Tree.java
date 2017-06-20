package com.fillumina.performance.util.collection;

import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Tree<K,V> extends Iterable<Tree<K,V>>, Map<K,V>, Entry<K,V> {

    /** @return the parent of the current Tree or null if it is the root. */
    Tree<K,V> getParent();

    /** @return the next sibling or null if there isn't. */
    Tree<K,V> getNextSibling();

    /** @return true if the node has no children. */
    boolean isLeaf();

    /** @return the maximum level of all the sub trees. */
    int getHeight();

    Tree<K,V> getTreeAtIndex(int index);

    /** @return the tree matching the key or create new one if not existent. */
    LinkedTree<K, V> getOrAddTree(K key);

    /** @return the created children. */
    Tree<K,V> addTree(K key, V value);

    /** @return the children with the same key. */
    Tree<K,V> getTree(K key);

    /** @return the removed children. */
    Tree<K,V> removeTree(K key);

    /**
     * Visits the nodes of the tree depth first.
     *
     * @param visitor
     * @return true if the traversal has been interrupted
     */
    boolean traverseDepthFirst(Visitor<Tree<K,V>> visitor);

    /**
     * Visits the nodes of the tree breadth first (i.e. by level).
     *
     * @param visitor
     * @return true if the traversal has been interrupted
     */
    boolean traverseBreadthFirst(Visitor<Tree<K,V>> visitor);

    /**
     * Visits only the leaves of the tree.
     *
     * @param visitor
     */
    void traverseLeaves(Visitor<Tree<K,V>> visitor);
}
