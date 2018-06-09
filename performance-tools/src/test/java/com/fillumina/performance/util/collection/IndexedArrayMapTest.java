package com.fillumina.performance.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class IndexedArrayMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new IndexedArrayMap<>();
    }

    @Test(timeout = 500)
    public void shouldUseCopyConstructor() {
        Map<Integer,String> map = new LinkedHashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        Map<Integer,String> copy = new IndexedArrayMap<>(map);
        assertEquals("one", copy.get(1));
        assertEquals("two", copy.get(2));
        assertEquals("three", copy.get(3));
        assertEquals("four", copy.get(4));
    }

    @Test(timeout = 500)
    public void shouldUseCloneConstructor() {
        IndexedArrayMap<Integer,String> map = new IndexedArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        Map<Integer,String> copy = new IndexedArrayMap<>(map);
        assertEquals("one", copy.get(1));
        assertEquals("two", copy.get(2));
        assertEquals("three", copy.get(3));
        assertEquals("four", copy.get(4));
    }

    @Test(timeout = 500)
    public void shouldIterateThroughAllEntries() {
        IndexedArrayMap<Integer,String> map = new IndexedArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        map.put(4, "four");

        List<Integer> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();
        map.forEach((k,v) -> { keys.add(k); values.add(v); } );

        assertEquals(Arrays.asList(1, 2, 3, 4), keys);
        assertEquals(Arrays.asList("one", "two", "three", "four"), values);
    }

    @Test(timeout = 500)
    public void shouldGetTheEntryAtIndex() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        Map.Entry<String,Integer> e = map.getEntryAtIndex(2);

        assertEquals("three", e.getKey());
        assertEquals(3, e.getValue(), 0);
    }

    @Test(timeout = 500)
    public void shouldGetTheKeyAtIndex() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        assertEquals("two", map.getKeyAtIndex(1));
    }

    @Test(timeout = 500)
    public void shouldGetTheValueAtIndex() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        assertEquals(4, map.getValueAtIndex(3), 0);
    }

    @Test(timeout = 500)
    public void shouldDetectUnmodifiableMapWhileNotModifyingTheMap() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        List<Entry<String,Integer>> copyBefore = new ArrayList<>(map.entrySet());

        assertTrue(map.unmodifiable().isUnmodifiable());

        List<Entry<String,Integer>> copyAfter = new ArrayList<>(map.entrySet());

        assertEquals(copyBefore, copyAfter);
    }

    private static class SameHash {
        @Override public int hashCode() { return 0; }
    }

    @Test(timeout = 500)
    public void shouldPutAndGetSameHash() {
        SameHash a = new SameHash();
        SameHash b = new SameHash();
        SameHash c = new SameHash();
        SameHash d = new SameHash();

        IndexedArrayMap<SameHash,Character> map = new IndexedArrayMap<>();
        map.put(a, 'a');
        map.put(b, 'b');
        map.put(c, 'c');
        map.put(d, 'd');

        assertEquals(4, map.size());
        assertEquals(Character.valueOf('a'), map.get(a));
        assertEquals(Character.valueOf('b'), map.get(b));
        assertEquals(Character.valueOf('c'), map.get(c));
        assertEquals(Character.valueOf('d'), map.get(d));
    }

    @Test(timeout = 500)
    public void shouldRemoveSameHash() {
        SameHash a = new SameHash();
        SameHash b = new SameHash();
        SameHash c = new SameHash();
        SameHash d = new SameHash();

        IndexedArrayMap<SameHash,Character> map = new IndexedArrayMap<>();
        map.put(a, 'a');
        map.put(b, 'b');
        map.put(c, 'c');
        map.put(d, 'd');

        map.remove(a);
        assertEquals(3, map.size());
        assertEquals(Character.valueOf('b'), map.get(b));
        assertEquals(Character.valueOf('c'), map.get(c));
        assertEquals(Character.valueOf('d'), map.get(d));

        map.remove(c);
        assertEquals(2, map.size());
        assertEquals(Character.valueOf('b'), map.get(b));
        assertEquals(Character.valueOf('d'), map.get(d));

        map.remove(d);
        assertEquals(1, map.size());
        assertEquals(Character.valueOf('b'), map.get(b));

        map.remove(b);
        assertTrue(map.isEmpty());
    }

    @Test(timeout = 500)
    public void shouldGetIndexOfKey() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        assertEquals("one", map.getKeyAtIndex(map.getIndexOfKey("one")));
        assertEquals("two", map.getKeyAtIndex(map.getIndexOfKey("two")));
        assertEquals("three", map.getKeyAtIndex(map.getIndexOfKey("three")));
        assertEquals("four", map.getKeyAtIndex(map.getIndexOfKey("four")));
    }

    @Test(timeout = 500)
    public void shouldGetIndexOfList() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        List<String> list = map.keyList();

        assertEquals("one", list.get(list.indexOf("one")));
        assertEquals("two", list.get(list.indexOf("two")));
        assertEquals("three", list.get(list.indexOf("three")));
        assertEquals("four", list.get(list.indexOf("four")));
    }

    @Test(timeout = 500)
    public void shouldIncreaseItsSize() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<>();
        for (int i=0; i<128; i++) {
            map.put("" + i, i);
        }

        assertEquals(128, map.size());

        for (int i=0; i<128; i++) {
            String key = "" + i;
            assertEquals(i, map.get(key), 0);
        }
    }

    @Test(timeout = 500)
    public void shouldIterateWithForEach() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        List<String> list = new ArrayList<>();
        map.forEach( (k,v) -> list.add(map.get(k) + k) );

        assertEquals(Arrays.asList("1one", "2two", "3three", "4four"), list);
    }

    @Test(timeout = 500)
    public void shouldIterateWithForEachMaintainingOrder() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("one", 1)
                .add("two", 2)
                .add("three", 3)
                .add("four", 4);

        map.remove("two");
        map.put("five", 5);

        List<String> list = new ArrayList<>();
        map.forEach( (k,v) -> list.add(map.get(k) + k) );

        assertEquals(Arrays.asList("1one", "3three", "4four", "5five"), list);
    }

    @Test(timeout = 500)
    public void shouldClearTheMapIfAllElementsAreRemoved() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<String,Integer>()
                .add("four", 4);

        map.remove("four");
        assertEquals(0, map.size());
    }

    @Test(timeout = 500, expected = UnsupportedOperationException.class)
    public void shouldUnmodifiableEmptyMapBeUnmofiable() {
        IndexedArrayMap<String,Integer> map = new IndexedArrayMap<>();

        map.unmodifiable().put("one", 1);
    }

    @Test(timeout = 500)
    public void shouldGetWithoutInfiniteLoop() {
        IndexedArrayMap<Integer,String> map = new IndexedArrayMap<Integer,String>()
                .add(1, "one")
                .add(2, "two")
                .add(3, "three")
                .add(4, "four");

        assertNull(map.get(5));
    }

    public static class ExtendedIndexedArrayMap
            extends IndexedArrayMap<Integer,String> {
        private static final long serialVersionUID = 1L;

        public ExtendedIndexedArrayMap() {
            super();
        }

        public ExtendedIndexedArrayMap(
                Map<? extends Integer, ? extends String> copy) {
            super(copy);
        }

        public ExtendedIndexedArrayMap(
                IndexedArrayMap<? extends Integer, ? extends String> clone) {
            super(clone);
        }

        public ExtendedIndexedArrayMap(int initialSize) {
            super(initialSize);
        }

        public ExtendedIndexedArrayMap(boolean notUsed, ExtendedIndexedArrayMap copy) {
            super(copy, true);
        }

        public String get(String key) {
            return get(Integer.valueOf(key));
        }

        /**
         * If you want to support unmodifiable views a dedicated
         * unmodifiable view class MUST be created.
         */
        public static class UnmodifiableView extends ExtendedIndexedArrayMap {
            private static final long serialVersionUID = 1L;

            public UnmodifiableView(IndexedArrayMap<Integer, String> copy) {
                super(copy);
            }

            @Override
            public boolean isUnmodifiable() {
                return true;
            }

            @Override
            public void ensureCapacity(int requiredCapacity) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void removeEntryAtIndex(int index) {
                throw new UnsupportedOperationException();
            }

            @Override
            public IndexedArrayMap<Integer, String>clone() {
                return this;
            }

            @Override
            public String setValueAtIndex(int index, String value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String remove(Object key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String put(Integer key, String value) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected ExtendedIndexedArrayMap createUnmodifiable() {
            return new UnmodifiableView(this);
        }

        @Override
        public ExtendedIndexedArrayMap unmodifiable() {
            return (ExtendedIndexedArrayMap) super.unmodifiable();
        }
    }

    @Test
    public void shouldInitializeExtendedMap() {
        ExtendedIndexedArrayMap emap = new ExtendedIndexedArrayMap();
        emap.put(1, "one");
        emap.put(2, "two");
        emap.put(3, "three");

        assertEquals("one", emap.get(1));
        assertEquals("two", emap.get("2")); // using overided method
        assertEquals("three", emap.get(3));
    }

    @Test
    public void shouldGetUnmodifiableExtendedMap() {
        ExtendedIndexedArrayMap emap = new ExtendedIndexedArrayMap();
        emap.put(1, "one");
        emap.put(2, "two");
        emap.put(3, "three");

        ExtendedIndexedArrayMap uemap = emap.unmodifiable();

        assertEquals("one", uemap.get(1));
        assertEquals("two", uemap.get("2")); // using overided method
        assertEquals("three", uemap.get(3));

        try {
            uemap.put(4, "four");
            fail("should not put into an unmodifiable map");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    @Test
    public void shouldUnmodifiableBeAView() {
        IndexedArrayMap<Integer,String> map = new IndexedArrayMap<>();
        map.put(1, "one");

        IndexedArrayMap<Integer,String> umap = map.unmodifiable();
        assertEquals("one", umap.get(1));

        map.remove(1);
        assertTrue(map.isEmpty());
        assertTrue(umap.isEmpty());
    }

    @Test
    public void shouldEntrySetOfUnmodifiableBeUpdatedByClear() {
        IndexedArrayMap<Integer,String> map = new IndexedArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");

        IndexedArrayMap<Integer,String> umap = map.unmodifiable();
        Set<Entry<Integer,String>> uset = umap.entrySet();

        map.clear();

        assertTrue(map.isEmpty());
        assertTrue(umap.isEmpty());
        assertTrue(uset.isEmpty());
    }

    @Test
    public void shouldAddEntriesToUnmodifiableToo() {
        IndexedArrayMap<Integer,String> map = new IndexedArrayMap<>();
        map.put(1, "one");
        map.put(2, "two");

        IndexedArrayMap<Integer,String> umap = map.unmodifiable();
        assertEquals(2, umap.size(), 0);

        map.put(3, "three");
        assertEquals(3, umap.size(), 0);

        map.clear();
        assertEquals(0, umap.size(), 0);

        map.put(4, "four");
        assertEquals(1, umap.size(), 0);
    }
}
