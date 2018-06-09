package com.fillumina.performance.util.collection;

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
public class ArrayMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new ArrayMap<>();
    }


    @Test
    public void shouldUnmodifiableBeAView() {
        ArrayMap<Integer,String> map = new ArrayMap<>();
        map.put(1, "one");

        ArrayMap<Integer,String> umap = map.unmodifiable();
        assertEquals("one", umap.get(1));

        map.remove(1);
        assertTrue(map.isEmpty());
        assertTrue(umap.isEmpty());
    }

    @Test
    public void shouldEntrySetOfUnmodifiableBeUpdatedByClear() {
        ArrayMap<Integer,String> map = new ArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");

        ArrayMap<Integer,String> umap = map.unmodifiable();
        Set<Entry<Integer,String>> uset = umap.entrySet();

        map.clear();

        assertTrue(map.isEmpty());
        assertTrue(umap.isEmpty());
        assertTrue(uset.isEmpty());
    }

    @Test
    public void shouldAddEntriesToUnmodifiableToo() {
        ArrayMap<Integer,String> map = new ArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");

        ArrayMap<Integer,String> umap = map.unmodifiable();
        assertEquals(2, umap.size(), 0);

        map.put(3, "three");
        assertEquals(3, umap.size(), 0);

        map.clear();
        assertEquals(0, umap.size(), 0);

        map.put(4, "four");
        assertEquals(1, umap.size(), 0);
    }

}
