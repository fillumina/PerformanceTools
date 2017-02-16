package com.fillumina.performance.util.tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LightMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new LightMap<>();
    }

    @Test
    public void testIterator() {
        Iterator<Entry<String,Integer>> it =
                ((LightMap<String,Integer>)popolateMap()).iterator();

        Set<String> set = new HashSet<>();
        while (it.hasNext()) {
            set.add(it.next().getKey());
        }

        assertEquals(5, set.size(), 0);
        assertTrue(set.contains("one"));
        assertTrue(set.contains("two"));
        assertTrue(set.contains("three"));
        assertTrue(set.contains("four"));
        assertTrue(set.contains("five"));
    }
}
