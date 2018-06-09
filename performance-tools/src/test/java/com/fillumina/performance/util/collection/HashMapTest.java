package com.fillumina.performance.util.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

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

    @Test(expected = UnsupportedOperationException.class)
    public void shouldAnUnmodifiableEntryBeUnmodifiable() {
        final Map<String, Integer> map = populateMap();
        final Map<String, Integer> umap = Collections.unmodifiableMap(map);
        Map.Entry<String,Integer> uentry = umap.entrySet().iterator().next();
        uentry.setValue(666);
    }
}
