package com.fillumina.performance.util.tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LightMapTest extends AbstractMapTest {

    @Override
    protected <K, V> LightMap<K, V> createMap() {
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

    @Test
    public void shouldNotRemoveTheNextElement() {
        final LightMap<String, Integer> map =
                (LightMap<String,Integer>)popolateMap();

        Iterator<Entry<String,Integer>> it = map.iterator();

        it.next();

        assertTrue(map.containsKey("two"));

        it.remove();

        assertFalse(map.containsKey("two"));
        assertTrue(map.containsKey("three"));

        it.remove();

        assertFalse(map.containsKey("three"));
    }

    @Test
    public void shouldRemoveFirstAndOnlyElementInIterator() {
        LightMap<String,Integer> map = createMap();
        map.put("first", -1);

        Iterator<Entry<String,Integer>> it = map.iterator();

        it.remove();

        assertTrue(map.isEmpty());
    }

}
