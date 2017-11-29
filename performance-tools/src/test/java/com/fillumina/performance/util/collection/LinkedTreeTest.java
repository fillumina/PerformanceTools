package com.fillumina.performance.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedTreeTest extends AbstractMapTest {
    private static final String NL = System.lineSeparator();

    public static void main(final String[] args) {
        System.out.println(new LinkedTreeTest().createTree().toString());
    }

    @Override
    protected <K, V> LinkedTree<K, V> createMap() {
        return new LinkedTree<>();
    }

    @Test
    public void shouldCreateFromOtherTree() {
        LinkedTree<String,Integer> tree =
                LinkedTree.<String,Integer>builder("0", -12)
                .branch("1", 111)
                    .leaf("11", 1)
                    .leaf("12", 2)
                .end()
                .branch("2", 222)
                    .leaf("21", 222_000)
                .end()
                .getRoot();

        LinkedTree<Integer,String> modified = LinkedTree.createFrom(tree,
                k -> Integer.valueOf(k), v -> "" + v);

        assertEquals("2", modified.getTreeAtPath(1, 12).getValue());
    }

    @Test(timeout=300)
    public void shouldAddAndGetAsMap() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);
        tree.put("three", 3);

        assertEquals(2, tree.get("two"), 0);
    }

    @Test(timeout=300)
    public void shoulSetKeyAndValue() {
        LinkedTree<String,Integer> tree = new LinkedTree<>("key", 1);
        assertEquals("key", tree.getKey());
        assertEquals(1, tree.getValue(), 0);
    }

    @Test(timeout=300)
    public void shouldSetNewValue() {
        LinkedTree<String,Integer> tree = new LinkedTree<>("key", 1);
        tree.setValue(3);
        assertEquals(3, tree.getValue(), 0);
    }

    @Test(timeout=300)
    public void shouldIterateUsingTreeIterator() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);

        Iterator<Tree<String,Integer>> it = tree.iterator();

        assertTrue(it.hasNext());
        Entry<String,Integer> entry = it.next();
        assertEquals("one", entry.getKey());
        assertEquals(1, entry.getValue(), 0);

        assertTrue(it.hasNext());
        entry = it.next();
        assertEquals("two", entry.getKey());
        assertEquals(2, entry.getValue(), 0);

        assertFalse(it.hasNext());
    }

    @Test(timeout=300)
    public void shouldFindTheChildren() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        LinkedTree<String,Integer> one = tree.getTree("one");
        one.put("one-one", 11);

        assertEquals(11, tree.getTree("one").get("one-one"), 0);
    }

    @Test
    public void shouldIterateDepthFirst() {
        LinkedTree<String,Integer> tree =
                LinkedTree.<String,Integer>builder("root", -1)
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        List<Integer> results = new ArrayList<>();
        for (Tree<String,Integer> t : tree.depthFirstIterable()) {
            results.add(t.getValue());
        }

        assertEquals(Arrays.asList(-1, 111, 1, 2, 3, 4, 222, 0, 333, 0, 1),
                results);
    }

    @Test
    public void shouldRemoveWhileIteratingDepthFirst() {
        LinkedTree<String,Integer> tree =
                LinkedTree.<String,Integer>builder("root", -1)
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        Iterator<Tree<String,Integer>> it = tree.depthFirstIterator();
        while(it.hasNext()) {
            Tree<String,Integer> t = it.next();
            if (t.getKey().equals("10")) {
                it.remove();
            }
        }

        List<Integer> results = new ArrayList<>();
        for (Tree<String,Integer> t : tree.depthFirstIterable()) {
            results.add(t.getValue());
        }

        assertEquals(Arrays.asList(-1, 111, 1, 2, 3, 4, 222, 0, 1),
                results);
    }

    @Test
    public void shouldIterateBreathFirst() {
        LinkedTree<String,Integer> tree =
                LinkedTree.<String,Integer>builder("root", -1)
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 444)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        List<Integer> results = new ArrayList<>();
        for (Tree<String,Integer> t : tree.breathFirstIterable()) {
            results.add(t.getValue());
        }

        assertEquals(Arrays.asList(-1, 111, 222, 1, 2, 3, 4, 0, 333, 1, 444),
                results);
    }

    @Test(timeout=300)
    public void shouldVisitDepthFirst() {
        LinkedTree<String,String> tree = createTree();
        final StringBuilder buf = new StringBuilder();
        tree.traverseDepthFirst(new Visitor<Tree<String, String>>() {
            @Override
            public boolean visit(Tree<String, String> tree) {
                buf.append(tree.getKey()).append(System.lineSeparator());
                return false;
            }
        });
        assertEquals(
                "zero" + NL +
                "one" + NL +
                "one-one" + NL +
                "one-two" + NL +
                "two" + NL +
                "two-one" + NL +
                "two-two" + NL,
                buf.toString());
    }

    @Test(timeout=300)
    public void shouldVisitBreadthFirst() {
        LinkedTree<String,String> tree = createTree();
        final StringBuilder buf = new StringBuilder();
        tree.traverseBreadthFirst(new Visitor<Tree<String, String>>() {
            @Override
            public boolean visit(Tree<String, String> tree) {
                buf.append(tree.getKey()).append(System.lineSeparator());
                return false;
            }
        });
        assertEquals(
                "zero" + NL +
                "one" + NL +
                "two" + NL +
                "one-one" + NL +
                "one-two" + NL +
                "two-one" + NL +
                "two-two" + NL,
                buf.toString());
    }

    @Test(timeout=300)
    public void shouldVisitDepthFirstAndStop() {
        LinkedTree<String,String> tree = createTree();
        final StringBuilder buf = new StringBuilder();
        tree.traverseDepthFirst(new Visitor<Tree<String, String>>() {
            @Override
            public boolean visit(Tree<String, String> tree) {
                buf.append(tree.getKey()).append(System.lineSeparator());
                return "two-one".equals(tree.getKey());
            }
        });
        assertEquals(
                "zero" + NL +
                "one" + NL +
                "one-one" + NL +
                "one-two" + NL +
                "two" + NL +
                "two-one" + NL,
                buf.toString());
    }

    @Test(timeout=300)
    public void shouldVisitBreadthFirstAndStop() {
        LinkedTree<String,String> tree = createTree();
        final StringBuilder buf = new StringBuilder();
        tree.traverseBreadthFirst(new Visitor<Tree<String, String>>() {
            @Override
            public boolean visit(Tree<String, String> tree) {
                buf.append(tree.getKey()).append(System.lineSeparator());
                return "two-one".equals(tree.getKey());
            }
        });
        assertEquals(
                "zero" + NL +
                "one" + NL +
                "two" + NL +
                "one-one" + NL +
                "one-two" + NL +
                "two-one" + NL,
                buf.toString());
    }

    private LinkedTree<String,String> createTree() {
        LinkedTree<String,String> tree = new LinkedTree<>("zero", "zero");

        LinkedTree<String,String> one = tree.addTree("one", "one");
        assertEquals(1, tree.size());
        assertEquals(0, one.size());
        one.put("one-one", "one-one");
        assertEquals(1, tree.size());
        assertEquals(1, one.size());
        one.put("one-two", "one-two");
        assertEquals(1, tree.size());
        assertEquals(2, one.size());

        LinkedTree<String,String> two = tree.addTree("two", "two");
        two.put("two-one", "two-one");
        two.put("two-two", "two-two");
        return tree;
    }

    @Test(timeout=300)
    public void shouldTestEquals() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();

        assertTrue(tree1.equals(tree2));
        assertTrue(tree2.equals(tree1));
    }

    @Test(timeout=300)
    public void shouldNotTestEquals() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();
        tree2.remove("one");

        assertFalse(tree1.equals(tree2));
        assertFalse(tree2.equals(tree1));
    }

    @Test(timeout=300)
    public void shouldHaveEqualHashCode() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();

        assertEquals(tree1.hashCode(), tree2.hashCode());
    }

    @Test(timeout=300)
    public void shouldNotHaveEqualHashCode() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();
        tree2.remove("one");

        assertNotEquals(tree1.hashCode(), tree2.hashCode());
    }

    @Test(timeout=300)
    public void shouldEmptyTreeBeEmpty() {
        assertTrue(LinkedTree.empty().isEmpty());
    }

    @Test(timeout=300)
    public void shouldCreateAClone() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = new LinkedTree<>(tree1);

        assertEquals(tree1, tree2);
    }

    @Test(timeout=300)
    public void shouldReturnTheValue() {
        LinkedTree<String,String> tree = new LinkedTree<>();
        tree.setValue("hello");

        assertEquals("hello", tree.getValue());
    }

    private static class LinkedTreeImpl extends LinkedTree<String,Void> {
        private static final long serialVersionUID = 1L;

        public LinkedTreeImpl() {
            super();
        }

        public LinkedTreeImpl(String key, Void value) {
            super(key, value);
        }

        @Override
        protected LinkedTree<String, Void> createNew(String key, Void value) {
            return new LinkedTreeImpl(key, value);
        }
    }

    @Test(timeout=300)
    public void shouldAllowSubclassing() {
        LinkedTreeImpl tree = new LinkedTreeImpl();
        Tree<String,Void> subTree = tree.addTree("one", null);

        assertTrue(subTree instanceof LinkedTreeImpl);
    }

    @Test(timeout=300)
    public void shouldReturnHeight() {
        assertEquals(0, new LinkedTree<>().getHeight());

        Tree<String,Void> tree = new LinkedTree<>("alfa", null);
        tree.put("beta", null);
        assertEquals(1, tree.getHeight());

        assertEquals(2, createTree().getHeight());
    }

    @Test
    public void shouldGetTheEntryAtGivenIndex() {
        Tree<String,Integer> tree = LinkedTree.<String,Integer>builder()
                .leaf("one", 1)
                .leaf("two", 2)
                .leaf("three", 3)
                .leaf("four", 4)
                .getRoot();

        assertEquals(1, tree.getTreeAtIndex(0).getValue(), 0);
        assertEquals(2, tree.getTreeAtIndex(1).getValue(), 0);
        assertEquals(3, tree.getTreeAtIndex(2).getValue(), 0);
        assertEquals(4, tree.getTreeAtIndex(3).getValue(), 0);

        assertEquals("one", tree.getTreeAtIndex(0).getKey());
        assertEquals("two", tree.getTreeAtIndex(1).getKey());
        assertEquals("three", tree.getTreeAtIndex(2).getKey());
        assertEquals("four", tree.getTreeAtIndex(3).getKey());
    }

    @Test
    public void shouldReturnTheParent() {
        Tree<String,Integer> tree = LinkedTree.<String,Integer>builder()
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .leaf("beta", 1)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                .end()
                .getRoot();

        Tree<String,Integer> oo = tree.getTree("1").getTree("10").getTree("oo");

        assertEquals(0, oo.getValue(), 0);
        assertEquals(333, oo.getParent().getValue(), 0);
        assertEquals(222, oo.getParent().getParent().getValue(), 0);
        assertNotNull(oo.getParent().getParent().getParent());
        assertNull(oo.getParent().getParent().getParent().getParent());
    }

    @Test
    public void shouldGetSiblings() {
        Tree<String,Integer> tree = LinkedTree.<String,Integer>builder()
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        Tree<String,Integer> oo = tree.getTree("1").getTree("10").getTree("oo");

        assertEquals(0, oo.getValue(), 0);
        assertEquals(333, oo.getParent().getValue(), 0);

        assertEquals(222, oo.getParent().getParent().getValue(), 0);
        assertEquals("beta", oo.getParent().getNextSibling().getKey());
    }

    @Test
    public void shouldGetOrAddTree() {
        Tree<String,Integer> tree = new LinkedTree<>();
        tree.getOrAddTree("one").put("first", 1);
        tree.getOrAddTree("one").put("second", 2);
        tree.getOrAddTree("two").put("third", 3);
        tree.getOrAddTree("two").put("fourth", 4);

        assertTrue(tree.getTree("one").keySet()
                .containsAll(Arrays.asList("first", "second")));

        assertTrue(tree.getTree("two").keySet()
                .containsAll(Arrays.asList("third", "fourth")));
    }

    @Test
    public void shouldFlatten() {
        Tree<String,Integer> tree =
                LinkedTree.<String,Integer>builder()
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        Map<String,Integer> map = new LinkedHashMap<>();
        tree.flatten(map, (List<String> list) -> {
            StringBuilder buf = new StringBuilder();
            for (String s : list) {
                buf.append(s).append(":");
            }
            return buf.toString();
        });

        assertEquals("{null:=null, " +
                "0:=111, 0:one:=1, 0:two:=2, 0:three:=3, 0:four:=4, " +
                "1:=222, 1:alfa:=0, 1:10:=333, 1:10:oo:=0, 1:beta:=1}",
                map.toString());
    }

    @Test
    public void shouldFlattenToList() {
        Tree<String,Integer> tree =
                LinkedTree.<String,Integer>builder("666", 666)
                        .branch("0", 111)
                            .leaf("one", 1)
                            .leaf("two", 2)
                            .leaf("three", 3)
                            .leaf("four", 4)
                        .end()
                        .branch("1", 222)
                            .leaf("alfa", 0)
                            .branch("10", 333)
                                .leaf("oo", 0)
                            .end()
                            .leaf("beta", 1)
                        .end()
                        .getRoot();

        Map<List<String>,Integer> listMap = new LinkedHashMap<>();
        tree.flattenTo(listMap);

        AssertMap assertion = new AssertMap(listMap);
        assertion.assertValue(666, "666");
        assertion.assertValue(111, "0");
        assertion.assertValue(1, "0", "one");
        assertion.assertValue(2, "0", "two");
        assertion.assertValue(3, "0", "three");
        assertion.assertValue(4, "0", "four");

        assertion.assertValue(222, "1");
        assertion.assertValue(0, "1", "alfa");
        assertion.assertValue(333, "1", "10");
        assertion.assertValue(0, "1", "10", "oo");
        assertion.assertValue(1, "1", "beta");

    }

    @Test
    public void shouldFlattenToMap() {
        Tree<String,Integer> tree = LinkedTree.<String,Integer>builder()
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        Map<List<String>,Integer> listMap = tree.getFlattenedMap();

        AssertMap assertion = new AssertMap(listMap);
        assertion.assertValue(111, "0");
        assertion.assertValue(1, "0", "one");
        assertion.assertValue(2, "0", "two");
        assertion.assertValue(3, "0", "three");
        assertion.assertValue(4, "0", "four");

        assertion.assertValue(222, "1");
        assertion.assertValue(0, "1", "alfa");
        assertion.assertValue(333, "1", "10");
        assertion.assertValue(0, "1", "10", "oo");
        assertion.assertValue(1, "1", "beta");

    }

    static class AssertMap {
        private final Map<List<String>,Integer> map;

        public AssertMap(Map<List<String>, Integer> map) {
            this.map = map;
        }

        void assertValue(Integer value, String... path) {
            Integer calculatedValue = map.get(Arrays.asList(path));
            assertEquals(value, calculatedValue);
        }
    }

    @Test
    public void shouldGetPath() {
        Tree<String,Integer> tree = LinkedTree.<String,Integer>builder()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                .end()
                .getRoot();

        List<String> listPath = Arrays.asList("1", "10", "oo");

        Tree<String,Integer> t = tree.getTreeAtPath(listPath);
        List<String> path = t.getPath();
        assertEquals(listPath, path);
    }

    @Test
    public void shouldGetValieAtPath() {
        Tree<String,Integer> tree = LinkedTree.<String,Integer>builder()
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        Tree<String,Integer> subt = tree.getTreeAtPath("1", "10");
        assertEquals(333, subt.getValue(), 0);
    }

    @Test
    public void shouldSetPath() {
        Tree<String,Integer> tree = new LinkedTree<>();
        tree.putValueAtPath(3, "0", "1", "2");

        //System.out.println("tree:" + tree);
        assertEquals(3, tree.getValueAtPath("0", "1", "2"), 0);
    }

    @Test
    public void shouldSet2Paths() {
        Tree<String,Integer> tree = new LinkedTree<>();
        tree.putValueAtPath(3, "0", "1", "2");
        tree.putValueAtPath(4, "0", "1", "3");

//        System.out.println("tree:" + tree);
        assertEquals(3, tree.getValueAtPath("0", "1", "2"), 0);
        assertEquals(4, tree.getValueAtPath("0", "1", "3"), 0);
    }

    @Test
    public void shouldMerge() {
        Tree<String,Integer> t1 = LinkedTree.<String,Integer>builder()
                .branch("0", 111)
                    .leaf("one", 1)
                    .leaf("two", 2)
                    .leaf("three", 3)
                    .leaf("four", 4)
                .end()
                .getRoot();

        Tree<String,Integer> t2 = LinkedTree.<String,Integer>builder()
                .branch("1", 222)
                    .leaf("alfa", 0)
                    .branch("10", 333)
                        .leaf("oo", 0)
                    .end()
                    .leaf("beta", 1)
                .end()
                .getRoot();

        Tree<String,Integer> m = LinkedTree.mergeTrees(t1, t2, (u, v) -> {
            return u != null ? u : v;
        });

//        System.out.println("treem: " + m);
        Map<List<String>,Integer> listMap = m.getFlattenedMap();

        AssertMap assertion = new AssertMap(listMap);
        assertion.assertValue(111, "0");
        assertion.assertValue(1, "0", "one");
        assertion.assertValue(2, "0", "two");
        assertion.assertValue(3, "0", "three");
        assertion.assertValue(4, "0", "four");

        assertion.assertValue(222, "1");
        assertion.assertValue(0, "1", "alfa");
        assertion.assertValue(333, "1", "10");
        assertion.assertValue(0, "1", "10", "oo");
        assertion.assertValue(1, "1", "beta");
    }

    @Test
    public void shouldMergeSameTree() {
        Tree<String,Integer> t1 = LinkedTree.<String,Integer>builder()
                .branch("first", 1)
                    .leaf("one", 2)
                    .leaf("two", 3)
                    .leaf("three", 4)
                    .leaf("four", 5)
                .end()
                .getRoot();

        Tree<String,Character> t2 = LinkedTree.<String,Character>builder()
                .branch("first", 'a')
                    .leaf("one", 'b')
                    .leaf("two", 'c')
                    .leaf("three", 'd')
                    .leaf("four", 'e')
                .end()
                .getRoot();

        Tree<String,String> m = LinkedTree.mergeTrees(t1, t2, (u,v) -> {
                return v != null && u != null ? "" + v + u : "root";
        });

        List<String> list = new ArrayList<>(m.getFlattenedMap().values());

        assertEquals(Arrays.asList("root", "a1", "b2", "c3", "d4", "e5"), list);
    }
}
