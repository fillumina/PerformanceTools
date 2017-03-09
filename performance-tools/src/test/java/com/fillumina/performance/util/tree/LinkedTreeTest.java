package com.fillumina.performance.util.tree;

import java.util.Iterator;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
        LinkedTree<String,Integer> one = tree.getChild("one");
        one.put("one-one", 11);

        assertEquals(11, tree.getChild("one").get("one-one"), 0);
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

        LinkedTree<String,String> one = tree.createChild("one", "one");
        assertEquals(1, tree.size());
        assertEquals(0, one.size());
        one.put("one-one", "one-one");
        assertEquals(1, tree.size());
        assertEquals(1, one.size());
        one.put("one-two", "one-two");
        assertEquals(1, tree.size());
        assertEquals(2, one.size());

        LinkedTree<String,String> two = tree.createChild("two", "two");
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
        Tree<String,Void> subTree = tree.createChild("one", null);

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
}
