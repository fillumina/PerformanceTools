package com.fillumina.performance.util.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to check the test.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class HashMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new HashMap<>();
    }

}
