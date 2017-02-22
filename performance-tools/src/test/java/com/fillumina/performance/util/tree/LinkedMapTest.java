package com.fillumina.performance.util.tree;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedMapTest extends AbstractMapTest {

    @Override
    protected <K, V> LinkedMap<K, V> createMap() {
        return new LinkedMap<>();
    }

}
