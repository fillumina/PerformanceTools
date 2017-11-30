package com.fillumina.performance.util.collection;

import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ArrayMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ArrayMap<>();
    }

}
