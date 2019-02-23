package com.fillumina.performance.util.pathname;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PathNameTest {

    private final PathName ROOT = PathName.createRoot();

    public static void main(final String[] args) {
        new PathNameTest().shouldCleanTheTree();
    }

    @Test
    public void shouldAppendFluently() {
        PathName a = ROOT.append("1", "2", "3");
        PathName b = ROOT.append("1").append("2").append("3");
        assertTrue(a == b);
    }

    @Test
    public void shouldAppendIterable() {
        PathName a = ROOT.append("1", "2", "3");
        PathName b = ROOT.append(Arrays.asList("1", "2", "3"));
        assertTrue(a == b);
    }

    @Test
    public void shouldSelectCommonPrefix() {
        PathName a = ROOT.append("1", "2", "3");
        PathName b = ROOT.append("1", "2", "3");
        PathName c = ROOT.append("1", "2", "X", "Y");
        PathName d = ROOT.append("1", "2");

        PathName common = PathName.getCommonPrefix(Arrays.asList(a, b, c, d));
        assertEquals(d, common);
    }

    @Test
    public void shouldReturnNullIfNoCommonPrefix() {
        PathName a = ROOT.append("1", "2", "3");
        PathName b = ROOT.append("1", "2", "3");
        PathName c = ROOT.append("1", "2", "X", "Y");
        PathName d = ROOT.append("A", "2");

        PathName common = PathName.getCommonPrefix(Arrays.asList(a, b, c, d));
        assertNotNull(common);
        assertEquals(ROOT, common);
    }

    @Test
    public void shouldEMPTYBeRoot() {
        assertTrue(PathName.ROOT.getRoot() == PathName.ROOT);
    }

    @Test
    public void shouldReturnTheRoot() {
        PathName cn = ROOT.append("hello").append("world");
        assertTrue(ROOT == cn.getRoot());
    }

    @Test
    public void shouldDetectEqualRoot() {
        PathName cn1 = ROOT.append("hello").append("world");
        PathName cn2 = ROOT.append("one");

        assertTrue(cn2.isSameRoot(cn1));
    }

    @Test
    public void shouldDetectNotEqualRoot() {
        PathName alterntativeRoot = PathName.createRoot();
        PathName cn1 = alterntativeRoot.append("hello").append("world");
        PathName cn2 = ROOT.append("one");

        assertFalse(cn2.isSameRoot(cn1));
    }

    @Test
    public void shouldCleanTheTree() {
        PathName cn = ROOT.append("alfa").append("beta").append("delta");
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
        PathName cn = ROOT.append("alfa");
        PathName beta = cn.append("beta");

        assertTrue(beta == cn.append("beta"));
    }

    @Test
    public void shouldReturnTheFirstName() {
        PathName cn = ROOT.append("alfa").append("beta").append("delta");
        assertEquals("alfa", cn.getFirstName());
    }

    @Test
    public void shouldReturnTheFirstNameWithOnlyOneName() {
        PathName cn = ROOT.append("alfa");
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
        PathName cn = ROOT.append("alfa", "beta", "gamma");
        assertEquals("alfa", cn.get(0));
        assertEquals("beta", cn.get(1));
        assertEquals("gamma", cn.get(2));
    }

    @Test
    public void shouldIterate() {
        Iterator<String> it = PathName.ROOT.append("one", "two", "three")
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
        PathName tn = PathName.createRoot().append("one", "two", "three");
        String[] array = tn.toArray();
        assertEquals(3, array.length);
        assertEquals("one", array[0]);
        assertEquals("two", array[1]);
        assertEquals("three", array[2]);
    }

    @Test
    public void shouldCompare() {
        PathName root = PathName.createRoot();
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
        PathName name = PathName.createRoot().append("one", "two", "three");

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
        PathName name = PathName.createRoot().append("one");

        Iterator<String> it = name.reverseIterator();

        assertTrue(it.hasNext());
        assertEquals("one", it.next());

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldReverseIterateWithRoot() {
        PathName name = PathName.createRoot();

        Iterator<String> it = name.reverseIterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void shouldDetectIsRoot() {
        PathName root = PathName.createRoot();

        assertTrue(root.isRoot());
    }

    @Test
    public void shouldDetectIsNotRoot() {
        PathName name = PathName.createRoot().append("one", "two", "three");

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
        List<PathName> items = ROOT.append("one", "two", "three").getAllPartialTNames();
        assertEquals("one", items.get(0).getLastName());
        assertEquals("two", items.get(1).getLastName());
        assertEquals("three", items.get(2).getLastName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldTheReturnedgetAllPartialTNamesBeImmutable() {
        List<PathName> items = ROOT.append("one", "two", "three").getAllPartialTNames();
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
        PathName tn = ROOT.append("one", "two", "three");
        String[] a = tn.toArray();
        String[] b = tn.toArray();
        assertTrue(a != b);

        a[0] = "XXX";
        assertEquals("one", b[0]);
    }

    @Test
    public void shouldRootBeEmpty() {
        assertTrue(ROOT.isEmpty());
    }

    @Test
    public void shouldNonRootBeNotEmpty() {
        PathName tn = ROOT.append("one", "two", "three");
        assertFalse(tn.isEmpty());
    }

    @Test
    public void shouldANullParamReturnRoot() {
        PathName a = ROOT.append((String)null);
        assertEquals(a, ROOT);

    }

    @Test
    public void shouldANullParamReturnTheSameTName() {
        PathName a = ROOT.append("one", "two", "three");
        PathName b = a.append((String)null);
        assertEquals(a, b);
    }

    @Test
    public void shouldGetTNameAt() {
        PathName tn = ROOT.append("one", "two", "three");

        assertEquals("one", tn.getTNameAt(0).getLastName());
        assertEquals("two", tn.getTNameAt(1).getLastName());
        assertEquals("three", tn.getTNameAt(2).getLastName());
    }

    @Test
    public void shouldCreateANewRootWithASeparator() {
        PathName root = PathName.createRootWithSeparator(" > ");
        assertEquals(" > ", root.getSeparator());
    }

    @Test
    public void shouldUseTheSeparatorGivenToRoot() {
        PathName root = PathName.createRootWithSeparator(" > ");

        assertEquals("one > two > three",
                root.append("one").append("two").append("three").toString());
    }
}
