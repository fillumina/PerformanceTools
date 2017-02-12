package com.fillumina.performance.util.tree;

import java.util.Iterator;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedTreeTest {

    @Test
    public void shouldAddAndGetAsMap() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);
        tree.put("three", 3);

        assertEquals(2, tree.get("two"), 0);
    }

    @Test
    public void shouldRetunrTheValue() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        assertEquals(1, tree.get("one"), 0);
    }

    @Test
    public void shouldClear() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.clear();
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testIsNotEmpty() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        assertFalse(tree.isEmpty());
    }

    @Test
    public void shouldReturnSize0() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        assertEquals(0, tree.size());
    }

    @Test
    public void shouldReturnSize2() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);
        assertEquals(2, tree.size());
    }

    @Test
    public void shouldOverwritePreviousEntryWithSameKey() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("one", 2);
        assertEquals(1, tree.size());
        assertEquals(2, tree.get("one"), 0);
    }

    @Test
    public void shouldReturnNullForANotExistentValue() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        assertNull(tree.get("one"));
    }

    @Test
    public void shouldReturnSize1() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        assertEquals(1, tree.size());
    }

    @Test
    public void shouldRemove() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.remove("one");
        assertTrue(tree.isEmpty());
    }

    @Test
    public void shouldRemoveFirst() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);
        tree.put("three", 3);
        tree.remove("one");
        assertEquals(2, tree.size());
    }

    @Test
    public void shouldRemoveMiddle() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);
        tree.put("three", 3);
        tree.remove("two");
        assertEquals(2, tree.size());
    }

    @Test
    public void shouldRemoveLast() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        tree.put("two", 2);
        tree.put("three", 3);
        tree.remove("three");
        assertEquals(2, tree.size());
    }

    @Test
    public void shoulSetKeyAndValue() {
        LinkedTree<String,Integer> tree = new LinkedTree<>("key", 1);
        assertEquals("key", tree.getKey());
        assertEquals(1, tree.getValue(), 0);
    }

    @Test
    public void shouldSetNewValue() {
        LinkedTree<String,Integer> tree = new LinkedTree<>("key", 1);
        tree.setValue(3);
        assertEquals(3, tree.getValue(), 0);
    }

    @Test
    public void shouldIterate() {
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

    @Test
    public void shouldFindTheChildren() {
        LinkedTree<String,Integer> tree = new LinkedTree<>();
        tree.put("one", 1);
        LinkedTree<String,Integer> one = tree.getChild("one");
        one.put("one-one", 11);

        assertEquals(11, tree.getChild("one").get("one-one"), 0);
    }

    @Test
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
                "zero\n" +
                "one\n" +
                "one-one\n" +
                "one-two\n" +
                "two\n" +
                "two-one\n" +
                "two-two\n",
                buf.toString());
    }

    @Test
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
                "zero\n" +
                "one\n" +
                "two\n" +
                "one-one\n" +
                "one-two\n" +
                "two-one\n" +
                "two-two\n",
                buf.toString());
    }

    @Test
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
                "zero\n" +
                "one\n" +
                "one-one\n" +
                "one-two\n" +
                "two\n" +
                "two-one\n",
                buf.toString());
    }

    @Test
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
                "zero\n" +
                "one\n" +
                "two\n" +
                "one-one\n" +
                "one-two\n" +
                "two-one\n",
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

    @Test
    public void shouldTestEquals() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();

        assertTrue(tree1.equals(tree2));
        assertTrue(tree2.equals(tree1));
    }

    @Test
    public void shouldNotTestEquals() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();
        tree2.remove("one");

        assertFalse(tree1.equals(tree2));
        assertFalse(tree2.equals(tree1));
    }

    @Test
    public void shouldHaveEqualHashCode() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();

        assertEquals(tree1.hashCode(), tree2.hashCode());
    }

    @Test
    public void shouldNotHaveEqualHashCode() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = createTree();
        tree2.remove("one");

        assertNotEquals(tree1.hashCode(), tree2.hashCode());
    }

    @Test
    public void shouldEmptyTreeBeEmpty() {
        assertTrue(LinkedTree.empty().isEmpty());
    }

    @Test
    public void shouldCreateAClone() {
        LinkedTree<String,String> tree1 = createTree();
        LinkedTree<String,String> tree2 = new LinkedTree<>(tree1);

        assertEquals(tree1, tree2);
    }

    @Test
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

    @Test
    public void shouldAllowSubclassing() {
        LinkedTreeImpl tree = new LinkedTreeImpl();
        Tree<String,Void> subTree = tree.createChild("one", null);

        assertTrue(subTree instanceof LinkedTreeImpl);
    }

    @Test
    public void shouldReturnHeight() {
        assertEquals(0, new LinkedTree<>().getHeight());

        Tree<String,Void> tree = new LinkedTree<>("alfa", null);
        tree.put("beta", null);
        assertEquals(1, tree.getHeight());

        assertEquals(2, createTree().getHeight());
    }
}
