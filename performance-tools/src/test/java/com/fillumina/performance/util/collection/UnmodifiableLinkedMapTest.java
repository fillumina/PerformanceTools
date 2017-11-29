package com.fillumina.performance.util.collection;

import com.fillumina.performance.util.collection.LinkedMap.LinkedEntry;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnmodifiableLinkedMapTest {

    @Test
    public void shouldCopyTheOriginalMap() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        assertEquals(2, umap.size());
        assertEquals(1, umap.get("one"), 0);
        assertEquals(2, umap.get("two"), 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowPuttinANewKeyValue() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        umap.put("three", 3);
    }

    private static class LinkedEntryImpl extends LinkedEntry<String,Integer> {
        private LinkedEntry<String,Integer> next;
        private String key;
        private Integer value;

        public LinkedEntryImpl(String key, Integer value) {
            super(key, value);
        }

        @Override
        public LinkedEntry<String, Integer> getNext() {
            return next;
        }

        @Override
        public void setNext(LinkedEntry<String, Integer> entry) {
            this.next = entry;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public Integer setValue(Integer value) {
            Integer old = value;
            this.value = value;
            return old;
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowPuttinANewEntry() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        umap.addEntry(new LinkedEntryImpl("three", 3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowPuttinAll() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(
                        new LinkedMap<>());

        umap.putAll(map);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemoving() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        umap.remove("one");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingFirstItemFromIterator() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Iterator<Entry<String,Integer>> it = umap.iterator();
        assertEquals("one", it.next().getKey());

        it.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingLastItemFromIterator() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Iterator<Entry<String,Integer>> it = umap.iterator();
        assertEquals("one", it.next().getKey());
        assertEquals("two", it.next().getKey());

        it.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingFromEntrySet() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Set<Entry<String,Integer>> set = umap.entrySet();
        Iterator<Entry<String,Integer>> it = set.iterator();
        assertEquals("one", it.next().getKey());
        assertEquals("two", it.next().getKey());

        it.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingFirstFromKeySet() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Set<String> set = umap.keySet();
        set.remove("one");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowAddingToKeySet() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Set<String> set = umap.keySet();
        set.add("NEW_KEY");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingLastFromKeySet() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Set<String> set = umap.keySet();
        set.remove("two");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingFirstFromValues() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Collection<Integer> coll = umap.values();
        coll.remove(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowRemovingLastFromValues() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Collection<Integer> coll = umap.values();
        coll.remove(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAlloAddingToValues() {
        LinkedMap<String,Integer> map = LinkedMap.create("one", 1, "two", 2);
        LinkedMap<String,Integer> umap =
                UnmodifiableLinkedMap.<String,Integer>copy(map);

        Collection<Integer> coll = umap.values();
        coll.add(33);
    }
}
