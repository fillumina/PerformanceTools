package com.fillumina.performance.util.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public interface Tree<K,V> extends Iterable<Tree<K,V>>, Map<K,V>, Entry<K,V> {

    List<K> getPath();

    Tree<K,V> getTreeAtPath(K... path);

    Tree<K,V> getTreeAtPath(List<K> path);

    V getValueAtPath(K... path);

    V getValueAtPath(List<K> path);

    V putValueAtPath(V value, K... path);

    V putValueAtPath(V value, List<K> path);

    Tree<K,V> getRoot();

    /** @return the parent of the current Tree or null if it is the root. */
    Tree<K,V> getParent();

    /** @return the next sibling or null if there isn't. */
    Tree<K,V> getNextSibling();

    default Tree<K,V> getPreviousSibling() {
        if (isRoot()) {
            return null;
        }
        Tree<K,V> prev = null;
        for (Tree<K,V> t : getParent()) {
            if (t == this) {
                return prev;
            }
            prev = t;
        }
        return null;
    }

    boolean isRoot();

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

    Map<List<K>,V> getFlattenedMap();

    Map<List<K>,V> flatten(Map<List<K>,V> map);

    <C> Map<C,V> flatten(Map<C,V> map, Function<List<K>,C> converter);

    Iterable<Tree<K,V>> breathFirstIterable();
    Iterator<Tree<K,V>> breathFirstIterator();
    Iterable<Tree<K,V>> depthFirstIterable();
    Iterator<Tree<K,V>> depthFirstIterator();

    /**
     * Visits the nodes of the tree depth first.
     *
     * @param visitor
     * @return true if the traversal has been interrupted
     */
    // TODO transform this visitor into iterators
    boolean traverseDepthFirst(Visitor<Tree<K,V>> visitor);

    /**
     * Visits the nodes of the tree breadth first (i.e. by level).
     *
     * @param visitor
     * @return true if the traversal has been interrupted
     */
    // TODO transform this visitor into iterators
    boolean traverseBreadthFirst(Visitor<Tree<K,V>> visitor);

    /**
     * Visits only the leaves of the tree.
     *
     * @param visitor
     */
    // TODO transform this visitor into iterators
    void traverseLeaves(Visitor<Tree<K,V>> visitor);
}
