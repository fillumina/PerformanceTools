package com.fillumina.performance.util.collection;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableArrayMapTest {

    private ArrayMap<String,Integer> uMap;

    @Before
    public void init() {
        uMap = new ArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4)
                .unmodifiable();
    }

    @Test
    public void shouldGet() {
        assertEquals(2, uMap.get("two"), 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAdd() {
        uMap.add("five", 5);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotRemove() {
        uMap.remove("three");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotClear() {
        uMap.clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotRemoveViaIterator() {
        Iterator<Map.Entry<String, Integer>> it = uMap.iterator();
        it.next();
        it.next();
        it.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotOverwrite() {
        uMap.put("two", 22);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotRemoveViaView() {
        Set<String> keySet = uMap.keySet();
        keySet.remove("three");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotClearViaView() {
        Set<String> keySet = uMap.keySet();
        keySet.clear();
    }

    @Test
    public void shouldRelinkOnAdd() {
        ArrayMap<String,Integer> map = new ArrayMap<String,Integer>();
        ArrayMap<String,Integer> umap = map.unmodifiable();
        map
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        assertEquals(3, umap.get("three"), 0);

        Set<String> set = umap.keySet();
        assertTrue(set.contains("four"));
    }

    @Test
    public void shouldRelinkOnClear() {
        ArrayMap<String,Integer> map = new ArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);
        ArrayMap<String,Integer> umap = map.unmodifiable();

        assertEquals(3, umap.get("three"), 0);
        Set<String> set = umap.keySet();

        map.clear();

        assertFalse(set.contains("four"));
        assertTrue(set.isEmpty());
    }
}
