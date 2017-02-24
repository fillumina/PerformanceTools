package com.fillumina.performance.infrastructure;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.infrastructure.PHolder.LeafVisitor;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.ComposedName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PHolderTest {

    @Test
    public void shouldReportNullPermanceAvailable() {
        PHolder<SpeedSample> holder = new PHolder<>((ComposedName)null);

        assertTrue(holder.isNull());
    }

    @Test
    public void shouldEmptyBeEmpty() {
        PHolder<SpeedSample> holder = PHolder.empty();

        assertTrue(holder.isNull());
    }

    @Test
    public void shouldEmptyCannotAddChild() {
        PHolder<SpeedSample> empty = PHolder.empty();
        empty.addChild(PHolder.empty());

        assertTrue(empty.isNull());
    }

    @Test
    public void shouldReportThePresenceOfAPerformance() {
        SpeedSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PHolder<SpeedSample> holder = PHolder.createWithValue(sample);

        assertFalse(holder.isNull());
    }

    @Test
    public void shouldUseAPerformance() {
        SpeedSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PHolder<SpeedSample> holder = PHolder.createWithValue(sample);

        PerformanceConsumerExecutionChecker<SpeedSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertEquals(sample, consumer.getReceivedPerformance());
    }

    @Test
    public void shouldReturnRoot() {
        final AssertableImpl root = new AssertableImpl("leaf");
        final ComposedName rootName = CName.EMPTY.append("root");
        PHolder<AssertableImpl> holder = new PHolder<>(rootName, root);
        assertEquals(rootName, holder.getName());
        assertEquals(root, holder.getStats());
        assertFalse(holder.isNull());
    }

    @Test
    public void shouldTestEquals() {
        final AssertableImpl leaf = new AssertableImpl("leaf");
        PHolder<AssertableImpl> holder1 = createPHolder("L", leaf);
        PHolder<AssertableImpl> holder2 = createPHolder("L", leaf);

        assertEquals(holder1, holder2);
    }

    @Test
    public void shouldNotTestEquals() {
        final AssertableImpl leaf = new AssertableImpl("leaf");
        PHolder<AssertableImpl> holder1 = createPHolder("L", leaf);
        PHolder<AssertableImpl> holder2 = createPHolder("S", leaf);

        assertNotEquals(holder1, holder2);
    }

    @Test
    public void shouldHaveSameHashCode() {
        final AssertableImpl leaf = new AssertableImpl("leaf");
        PHolder<AssertableImpl> holder1 = createPHolder("L", leaf);
        PHolder<AssertableImpl> holder2 = createPHolder("L", leaf);

        assertEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldNotHaveSameHashCode() {
        final AssertableImpl leaf = new AssertableImpl("leaf");
        PHolder<AssertableImpl> holder1 = createPHolder("L", leaf);
        PHolder<AssertableImpl> holder2 = createPHolder("S", leaf);

        assertNotEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldAddAChild() {
        PHolder<PHolder<AssertableImpl>> root = new PHolder<>((ComposedName)null);

        final AssertableImpl leaf = new AssertableImpl("leaf");
        PHolder<AssertableImpl> childHolder = createPHolder("L", leaf);

        root.addChild(childHolder);

        assertFalse(root.isNull());

        PHolder<AssertableImpl> resultChildHolder = root.iterator().next();

        assertEquals(childHolder, resultChildHolder);
    }

    @Test
    public void shouldTraverseChildren() {
        PHolder<PHolder<AssertableImpl>> root = new PHolder<>((ComposedName)null);

        final AssertableImpl leaf1 = new AssertableImpl("1");
        final AssertableImpl leaf2 = new AssertableImpl("2");
        final AssertableImpl leaf3 = new AssertableImpl("3");

        root.addChild(createPHolder("one", leaf1));
        root.addChild(createPHolder("two", leaf2));
        root.addChild(createPHolder("three", leaf3));

        final List<AssertableImpl> list = new ArrayList<>();

        root.traverseLeaves(new LeafVisitor<AssertableImpl>() {
            @Override
            public void visitLeaf(ComposedName name, AssertableImpl stats) {
                list.add(stats);
            }
        });

        assertEquals(3, list.size(), 0);
        assertTrue(list.contains(leaf1));
        assertTrue(list.contains(leaf2));
        assertTrue(list.contains(leaf3));
    }

    @Test
    public void shouldTraverseChildrenSecondOrder() {
        PHolder<PHolder<PHolder<AssertableImpl>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableImpl>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableImpl leaf1 = new AssertableImpl("1");
        final AssertableImpl leaf2 = new AssertableImpl("2");
        final AssertableImpl leaf3 = new AssertableImpl("3");

        subroot.addChild(createPHolder("one", leaf1));
        subroot.addChild(createPHolder("two", leaf2));
        subroot.addChild(createPHolder("three", leaf3));

        final List<AssertableImpl> list = new ArrayList<>();

        root.traverseLeaves(new LeafVisitor<AssertableImpl>() {
            @Override
            public void visitLeaf(ComposedName name, AssertableImpl stats) {
                list.add(stats);
            }
        });

        assertEquals(3, list.size(), 0);
        assertTrue(list.contains(leaf1));
        assertTrue(list.contains(leaf2));
        assertTrue(list.contains(leaf3));
    }

    @Test
    public void shouldIterateThroughSubTrees() {
        PHolder<PHolder<AssertableImpl>> root = new PHolder<>((ComposedName)null);

        final AssertableImpl leaf1 = new AssertableImpl("1");
        final AssertableImpl leaf2 = new AssertableImpl("2");
        final AssertableImpl leaf3 = new AssertableImpl("3");

        root.addChild(createPHolder("one", leaf1));
        root.addChild(createPHolder("two", leaf2));
        root.addChild(createPHolder("three", leaf3));

        final List<AssertableImpl> list = new ArrayList<>();

        for (PHolder<AssertableImpl> p : root) {
            list.add(p.getStats());
        }

        assertEquals(3, list.size(), 0);
        assertTrue(list.contains(leaf1));
        assertTrue(list.contains(leaf2));
        assertTrue(list.contains(leaf3));
    }

    @Test
    public void shouldBeEmptyIfNoChildren() {
        final AssertableImpl leaf1 = new AssertableImpl("1");
        PHolder<AssertableImpl> holder = createPHolder("one", leaf1);

        assertTrue(holder.isChildless());
    }

    @Test
    public void shouldReturnTheLeafByName() {
        PHolder<PHolder<PHolder<AssertableImpl>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableImpl>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableImpl leaf1 = new AssertableImpl("1");
        final AssertableImpl leaf2 = new AssertableImpl("2");
        final AssertableImpl leaf3 = new AssertableImpl("3");

        final PHolder<AssertableImpl> holder1 = createPHolder("one", leaf1);
        final PHolder<AssertableImpl> holder2 = createPHolder("two", leaf2);
        final PHolder<AssertableImpl> holder3 = createPHolder("three", leaf3);

        subroot.addChild(holder1);
        subroot.addChild(holder2);
        subroot.addChild(holder3);

        final PHolder<PHolder<PHolder<AssertableImpl>>> result1 =
                root.getLeaf(CName.EMPTY.append("one"));
        final PHolder<PHolder<PHolder<AssertableImpl>>> result2 =
                root.getLeaf(CName.EMPTY.append("two"));
        final PHolder<PHolder<PHolder<AssertableImpl>>> result3 =
                root.getLeaf(CName.EMPTY.append("three"));

        assertEquals(holder1, result1);
        assertEquals(holder2, result2);
        assertEquals(holder3, result3);
    }

    private PHolder<AssertableImpl> createPHolder(String name, AssertableImpl leaf) {
        return new PHolder<>(CName.EMPTY.append(name), leaf);
    }

    private static class AssertableImpl implements Assertable {
        private final String name;

        public AssertableImpl(String name) {
            this.name = name;
        }

        @Override
        public Measure getValue(String testName) {
            return null;
        }

        @Override
        public MeasureRatio getRatioWithSlowestTest(String testName) {
            return null;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.name);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AssertableImpl other = (AssertableImpl) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "AssertableImpl{" + "name=" + name + '}';
        }
    }

    @Test
    public void shouldUseAConsumerOnSecondOrder() {
        PHolder<PHolder<PHolder<AssertableImpl>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableImpl>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableImpl leaf1 = new AssertableImpl("1");
        final AssertableImpl leaf2 = new AssertableImpl("2");
        final AssertableImpl leaf3 = new AssertableImpl("3");

        final PHolder<AssertableImpl> holder1 = createPHolder("one", leaf1);
        final PHolder<AssertableImpl> holder2 = createPHolder("two", leaf2);
        final PHolder<AssertableImpl> holder3 = createPHolder("three", leaf3);

        subroot.addChild(holder1);
        subroot.addChild(holder2);
        subroot.addChild(holder3);

        ConsumerImpl<PHolder<PHolder<AssertableImpl>>> consumer =
                new ConsumerImpl<>();
        root.use(consumer);

        assertEquals(3, consumer.list.size(), 0);
        assertTrue(consumer.list.contains("1"));
        assertTrue(consumer.list.contains("2"));
        assertTrue(consumer.list.contains("3"));
    }

    @Test
    public void shouldUseAConsumerOnFirstOrder() {
        PHolder<PHolder<AssertableImpl>> root =
                new PHolder<>(CName.EMPTY.append("subroot"));

        final AssertableImpl leaf1 = new AssertableImpl("1");
        final AssertableImpl leaf2 = new AssertableImpl("2");
        final AssertableImpl leaf3 = new AssertableImpl("3");

        final PHolder<AssertableImpl> holder1 = createPHolder("one", leaf1);
        final PHolder<AssertableImpl> holder2 = createPHolder("two", leaf2);
        final PHolder<AssertableImpl> holder3 = createPHolder("three", leaf3);

        root.addChild(holder1);
        root.addChild(holder2);
        root.addChild(holder3);

        ConsumerImpl<PHolder<AssertableImpl>> consumer =
                new ConsumerImpl<>();
        root.use(consumer);

        assertEquals(3, consumer.list.size(), 0);
        assertTrue(consumer.list.contains("1"));
        assertTrue(consumer.list.contains("2"));
        assertTrue(consumer.list.contains("3"));
    }

    @Test
    public void shouldUseAConsumerOnSimpleStats() {
        final AssertableImpl leaf = new AssertableImpl("1");
        final PHolder<AssertableImpl> holder = createPHolder("one", leaf);

        ConsumerImpl<AssertableImpl> consumer = new ConsumerImpl<>();
        holder.use(consumer);

        assertEquals(1, consumer.list.size(), 0);
        assertTrue(consumer.list.contains("1"));
    }

    @Test
    public void shouldUseAssertionOnSimpleStats() {
        final AssertableImpl leaf = new AssertableImpl("1");
        final PHolder<AssertableImpl> holder = createPHolder("one", leaf);

        AssertionImpl<AssertableImpl> assertion = new AssertionImpl<>();
        holder.check(assertion);

        assertEquals(1, assertion.list.size(), 0);
        assertTrue(assertion.list.contains("1"));

        StringBuilder buf = new StringBuilder();
        holder.checkAndPrint(buf, assertion);

        assertEquals("\n1\n", buf.toString());
    }

    // TODO this is an example of generic consumer which works on all the hierarchy
    private static class ConsumerImpl<T extends Assertable>
            implements PerformanceConsumer<T> {

        protected final List<String> list = new ArrayList<>();

        @Override
        public void consume(PHolder<T> performances) {
            performances.traverseLeaves(new LeafVisitor<AssertableImpl>() {
                @Override
                public void visitLeaf(ComposedName name, AssertableImpl stats) {
                    list.add(stats.name);
                }
            });
        }
    }

    // TODO this is an example of generic consumer which works on all the hierarchy
    private static class AssertionImpl<T extends Assertable>
            extends ConsumerImpl<T>
            implements Assertion<T> {

        @Override
        public void check(PHolder<T> performances) {
            consume(performances);
        }

        @Override
        public String toString(PHolder<T> performances) {
            final StringBuilder buf = new StringBuilder();
            performances.traverseLeaves(new LeafVisitor<AssertableImpl>() {
                @Override
                public void visitLeaf(ComposedName name, AssertableImpl stats) {
                    buf.append(stats.name);
                }
            });
            return buf.toString();
        }
    }
}
