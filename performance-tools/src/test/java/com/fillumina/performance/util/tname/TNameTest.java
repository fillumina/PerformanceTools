package com.fillumina.performance.util.tname;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameTest {

    private final TName ROOT = TName.createRoot();

    public static void main(final String[] args) {
        new TNameTest().shouldCleanTheTree();
    }

    @Test
    public void shouldAppendFluently() {
        TName a = ROOT.append("1", "2", "3");
        TName b = ROOT.append("1").append("2").append("3");
        assertTrue(a == b);
    }

    @Test
    public void shouldAppendIterable() {
        TName a = ROOT.append("1", "2", "3");
        TName b = ROOT.append(Arrays.asList("1", "2", "3"));
        assertTrue(a == b);
    }

    @Test
    public void shouldSelectCommonPrefix() {
        TName a = ROOT.append("1", "2", "3");
        TName b = ROOT.append("1", "2", "3");
        TName c = ROOT.append("1", "2", "X", "Y");
        TName d = ROOT.append("1", "2");

        TName common = TName.commonPrefix(Arrays.asList(a, b, c, d));
        assertEquals(d, common);
    }

    @Test
    public void shouldReturnNullIfNoCommonPrefix() {
        TName a = ROOT.append("1", "2", "3");
        TName b = ROOT.append("1", "2", "3");
        TName c = ROOT.append("1", "2", "X", "Y");
        TName d = ROOT.append("A", "2");

        TName common = TName.commonPrefix(Arrays.asList(a, b, c, d));
        assertNotNull(common);
        assertEquals(ROOT, common);
    }

    @Test
    public void shouldEMPTYBeRoot() {
        assertTrue(TName.ROOT.getRoot() == TName.ROOT);
    }

    @Test
    public void shouldReturnTheRoot() {
        TName cn = ROOT.append("hello").append("world");
        assertTrue(ROOT == cn.getRoot());
    }

    @Test
    public void shouldDetectEqualRoot() {
        TName cn1 = ROOT.append("hello").append("world");
        TName cn2 = ROOT.append("one");

        assertTrue(cn2.isSameRoot(cn1));
    }

    @Test
    public void shouldDetectNotEqualRoot() {
        TName alterntativeRoot = TName.createRoot();
        TName cn1 = alterntativeRoot.append("hello").append("world");
        TName cn2 = ROOT.append("one");

        assertFalse(cn2.isSameRoot(cn1));
    }

    @Test
    public void shouldCleanTheTree() {
        TName cn = ROOT.append("alfa").append("beta").append("delta");
        assertFalse(ROOT.isChildrenEmpty());

        cn = null;

        // force a GC to collect ROOT unused data
        Object[] array = new Object[10];
        for (int i=0; i<array.length; i++) {
            array[0] = new double[1<<24];
            System.gc();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {

            }
            ROOT.clean();
            if (ROOT.isChildrenEmpty()) {
                break; // ok!
            }
        }

        // could EVENTUALLY fail if GC fails to collect cn
        assertTrue(ROOT.isChildrenEmpty());
        cn = ROOT.append("another");
        assertEquals("another", cn.toString());
    }

    @Test
    public void shouldEmptyNameOutputEmptyString() {
        assertEquals("", ROOT.toString());
    }

    @Test
    public void shouldOutputTheSingleName() {
        assertEquals("name", ROOT.append("name").toString());
    }

    @Test
    public void shouldOutputDoubleNames() {
        assertEquals("alfa : beta",
                ROOT.append("alfa").append("beta").toString());
    }

    @Test
    public void shouldOutputTripleNames() {
        assertEquals("alfa : beta : delta",
                ROOT.append("alfa").append("beta").append("delta")
                        .toString());
    }

    @Test
    public void shouldNotCreateANewElementWithTheSameName() {
        TName cn = ROOT.append("alfa");
        TName beta = cn.append("beta");

        assertTrue(beta == cn.append("beta"));
    }

    @Test
    public void shouldReturnTheFirstName() {
        TName cn = ROOT.append("alfa").append("beta").append("delta");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnTheFirstNameWithOnlyOneName() {
        TName cn = ROOT.append("alfa");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnNullAsFirstNameIfEmpty() {
        assertEquals(null, ROOT.getFirstName());
    }

    @Test
    public void shouldReturnNullAsTheLastNameIfEmpty() {
        assertEquals(null, ROOT.getLastName());
    }

    @Test
    public void shouldReadAsList() {
        TName cn = ROOT.append("alfa", "beta", "gamma");
        assertEquals("alfa", cn.get(0));
        assertEquals("beta", cn.get(1));
        assertEquals("gamma", cn.get(2));
    }

    @Test
    public void shouldIterate() {
        Iterator<String> it = TName.ROOT.append("one", "two", "three")
                .iterator();

        assertTrue(it.hasNext());
        assertEquals("one", it.next());

        assertTrue(it.hasNext());
        assertEquals("two", it.next());

        assertTrue(it.hasNext());
        assertEquals("three", it.next());

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldReadAsArray() {
        TName tn = TName.createRoot().append("one", "two", "three");
        String[] array = tn.toArray();
        assertEquals(3, array.length);
        assertEquals("one", array[0]);
        assertEquals("two", array[1]);
        assertEquals("three", array[2]);
    }

    @Test
    public void shouldCompare() {
        TName root = TName.createRoot();
        assertEquals(0,
                root.compareTo(root));
        assertEquals(0,
                root.append("a").compareTo(root.append("a")));
        assertEquals(0,
                root.append("a", "b").compareTo(root.append("a", "b")));
        assertEquals(0,
                root.append("a", "b", "c").compareTo(root.append("a", "b", "c")));


        assertEquals(-1,
                root.compareTo(root.append("a")));
        assertEquals(1,
                root.append("a").compareTo(root));

        assertEquals(-1,
                root.append("a", "b").compareTo(root.append("a", "b", "c")));
        assertEquals(1,
                root.append("a", "b", "c").compareTo(root.append("a", "b")));

        assertEquals(-1,
                root.append("a", "b").compareTo(root.append("a", "c")));
        assertEquals(1,
                root.append("a", "c").compareTo(root.append("a", "b")));
    }

    @Test
    public void shouldReverseIterate() {
        TName name = TName.createRoot().append("one", "two", "three");

        Iterator<String> it = name.reverseIterator();
        assertTrue(it.hasNext());
        assertEquals("three", it.next());

        assertTrue(it.hasNext());
        assertEquals("two", it.next());

        assertTrue(it.hasNext());
        assertEquals("one", it.next());

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldReverseIterateWithOneElement() {
        TName name = TName.createRoot().append("one");

        Iterator<String> it = name.reverseIterator();

        assertTrue(it.hasNext());
        assertEquals("one", it.next());

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldReverseIterateWithRoot() {
        TName name = TName.createRoot();

        Iterator<String> it = name.reverseIterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void shouldDetectIsRoot() {
        TName root = TName.createRoot();

        assertTrue(root.isRoot());
    }

    @Test
    public void shouldDetectIsNotRoot() {
        TName name = TName.createRoot().append("one", "two", "three");

        assertFalse(name.isRoot());
    }

    @Test
    public void shouldMatchTheSize() {
        assertEquals(0, ROOT.size());
        assertEquals(1, ROOT.append("one").size());
        assertEquals(2, ROOT.append("one", "two").size());
        assertEquals(3, ROOT.append("one", "two", "three").size());
    }

    @Test
    public void shouldNotHaveParent() {
        assertFalse(ROOT.hasParent());
    }

    @Test
    public void shouldHaveParent() {
        assertTrue(ROOT.append("one").hasParent());
    }

    @Test
    public void shouldReturnNullIfHasNoParent() {
        assertNull(ROOT.getParent());
    }

    @Test
    public void shouldGetParent() {
        assertEquals(ROOT, ROOT.append("something").getParent());
    }

    @Test
    public void shouldReturngetAllPartialTNames() {
        List<TName> items = ROOT.append("one", "two", "three").getAllPartialTNames();
        assertEquals("one", items.get(0).getLastName());
        assertEquals("two", items.get(1).getLastName());
        assertEquals("three", items.get(2).getLastName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldTheReturnedgetAllPartialTNamesBeImmutable() {
        List<TName> items = ROOT.append("one", "two", "three").getAllPartialTNames();
        items.set(2, ROOT.append("bla"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotRemove() {
        ROOT.append("one", "two", "three").remove(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotClear() {
        ROOT.append("one", "two", "three").clear();
    }

    @Test
    public void shouldTheReturnedArrayBeAClone() {
        TName tn = ROOT.append("one", "two", "three");
        String[] a = tn.toArray();
        String[] b = tn.toArray();
        assertTrue(a != b);

        a[0] = "XXX";
        assertEquals("one", b[0]);
    }
}
