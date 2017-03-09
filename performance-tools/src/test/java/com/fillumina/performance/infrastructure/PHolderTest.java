package com.fillumina.performance.infrastructure;

import com.fillumina.performance.infrastructure.PHolder.LeafVisitor;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.mock.ConsumerMock;
import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.StaticPath;
import java.util.ArrayList;
import java.util.List;
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
        PHolder<SpeedSample> holder = new PHolder<>((StaticPath)null);

        assertTrue(holder.isNull());
    }

    @Test
    public void shouldEmptyBeEmpty() {
        PHolder<SpeedSample> holder = PHolder.empty();

        assertTrue(holder.isNull());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldEmptyCannotAddChild() {
        PHolder<SpeedSample> empty = PHolder.empty();
        empty.addChild(PHolder.empty());
    }

    @Test
    public void shouldReportThePresenceOfAPerformance() {
        SpeedSample sample = MockPerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PHolder<SpeedSample> holder = PHolder.createWithValue(sample);

        assertFalse(holder.isNull());
    }

    @Test
    public void shouldUseAPerformance() {
        SpeedSample sample = MockPerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PHolder<SpeedSample> holder = PHolder.createWithValue(sample);

        PerformanceConsumerExecutionChecker<SpeedSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertEquals(sample, consumer.getReceivedPerformance());
    }

    @Test
    public void shouldReturnRoot() {
        final AssertableMock root = new AssertableMock("leaf");
        final StaticPath rootName = CName.EMPTY.append("root");
        PHolder<AssertableMock> holder = new PHolder<>(rootName, root);
        assertEquals(rootName, holder.getName());
        assertEquals(root, holder.getStats());
        assertFalse(holder.isNull());
    }

    @Test
    public void shouldTestEquals() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = createPHolder("L", leaf);
        PHolder<AssertableMock> holder2 = createPHolder("L", leaf);

        assertEquals(holder1, holder2);
    }

    @Test
    public void shouldNotTestEquals() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = createPHolder("L", leaf);
        PHolder<AssertableMock> holder2 = createPHolder("S", leaf);

        assertNotEquals(holder1, holder2);
    }

    @Test
    public void shouldHaveSameHashCode() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = createPHolder("L", leaf);
        PHolder<AssertableMock> holder2 = createPHolder("L", leaf);

        assertEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldNotHaveSameHashCode() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = createPHolder("L", leaf);
        PHolder<AssertableMock> holder2 = createPHolder("S", leaf);

        assertNotEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldAddAChild() {
        PHolder<PHolder<AssertableMock>> root = new PHolder<>((StaticPath)null);

        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> childHolder = createPHolder("L", leaf);

        root.addChild(childHolder);

        assertFalse(root.isNull());

        PHolder<AssertableMock> resultChildHolder = root.iterator().next();

        assertEquals(childHolder, resultChildHolder);
    }

    @Test
    public void shouldTraverseChildren() {
        PHolder<PHolder<AssertableMock>> root = new PHolder<>((StaticPath)null);

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        root.addChild(createPHolder("one", leaf1));
        root.addChild(createPHolder("two", leaf2));
        root.addChild(createPHolder("three", leaf3));

        final List<AssertableMock> list = new ArrayList<>();

        root.traverseLeaves(new LeafVisitor<AssertableMock>() {
            @Override
            public void visitLeaf(StaticPath name, AssertableMock stats) {
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
        PHolder<PHolder<PHolder<AssertableMock>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableMock>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        subroot.addChild(createPHolder("one", leaf1));
        subroot.addChild(createPHolder("two", leaf2));
        subroot.addChild(createPHolder("three", leaf3));

        final List<AssertableMock> list = new ArrayList<>();

        root.traverseLeaves(new LeafVisitor<AssertableMock>() {
            @Override
            public void visitLeaf(StaticPath name, AssertableMock stats) {
                list.add(stats);
            }
        });

        assertEquals(3, list.size(), 0);
        assertTrue(list.contains(leaf1));
        assertTrue(list.contains(leaf2));
        assertTrue(list.contains(leaf3));
    }

    @Test
    public void shouldCreateTreeWithBuilder() {
        // builder
        PHolder<PHolder<AssertableMock>> builtRoot =
                PHolder.build("root")
                    .branch("subroot")
                        .leaf("one", new AssertableMock("1"))
                        .leaf("two", new AssertableMock("2"))
                        .leaf("three", new AssertableMock("3"))
                    .<PHolder<AssertableMock>>getRoot();

        // normal instantiation
        StaticPath rootCName = CName.EMPTY.append("root");
        PHolder<PHolder<PHolder<AssertableMock>>> root =
                new PHolder<>(rootCName);
        StaticPath subrootCName = rootCName.append("subroot");
        PHolder<PHolder<AssertableMock>> subroot =
                new PHolder<>(subrootCName);
        root.addChild(subroot);
        AssertableMock leaf1 = new AssertableMock("1");
        AssertableMock leaf2 = new AssertableMock("2");
        AssertableMock leaf3 = new AssertableMock("3");
        subroot.addChild(new PHolder<>(subrootCName.append("one"), leaf1));
        subroot.addChild(new PHolder<>(subrootCName.append("two"), leaf2));
        subroot.addChild(new PHolder<>(subrootCName.append("three"), leaf3));

        assertEquals(builtRoot, root);
    }

    @Test
    public void shouldIterateThroughtChildrenSecondOrder() {
        PHolder<PHolder<PHolder<AssertableMock>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableMock>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        subroot.addChild(createPHolder("one", leaf1));
        subroot.addChild(createPHolder("two", leaf2));
        subroot.addChild(createPHolder("three", leaf3));

        boolean flag = false;
        for (PHolder<PHolder<AssertableMock>> p : root) {
            assertEquals(p, subroot);
            flag = true;
        }

        assertTrue(flag);
    }

    @Test
    public void shouldIterateThroughSubTrees() {
        PHolder<PHolder<AssertableMock>> root = new PHolder<>((StaticPath)null);

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        root.addChild(createPHolder("one", leaf1));
        root.addChild(createPHolder("two", leaf2));
        root.addChild(createPHolder("three", leaf3));

        final List<AssertableMock> list = new ArrayList<>();

        for (PHolder<AssertableMock> p : root) {
            list.add(p.getStats());
        }

        assertEquals(3, list.size(), 0);
        assertTrue(list.contains(leaf1));
        assertTrue(list.contains(leaf2));
        assertTrue(list.contains(leaf3));
    }

    @Test
    public void shouldBeEmptyIfNoChildren() {
        final AssertableMock leaf1 = new AssertableMock("1");
        PHolder<AssertableMock> holder = createPHolder("one", leaf1);

        assertTrue(holder.isChildless());
    }

    @Test
    public void shouldReturnTheLeafByName() {
        PHolder<PHolder<PHolder<AssertableMock>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableMock>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        final PHolder<AssertableMock> holder1 = createPHolder("one", leaf1);
        final PHolder<AssertableMock> holder2 = createPHolder("two", leaf2);
        final PHolder<AssertableMock> holder3 = createPHolder("three", leaf3);

        subroot.addChild(holder1);
        subroot.addChild(holder2);
        subroot.addChild(holder3);

        final PHolder<PHolder<PHolder<AssertableMock>>> result1 =
                root.getLeaf(CName.EMPTY.append("one"));
        final PHolder<PHolder<PHolder<AssertableMock>>> result2 =
                root.getLeaf(CName.EMPTY.append("two"));
        final PHolder<PHolder<PHolder<AssertableMock>>> result3 =
                root.getLeaf(CName.EMPTY.append("three"));

        assertEquals(holder1, result1);
        assertEquals(holder2, result2);
        assertEquals(holder3, result3);
    }

    private PHolder<AssertableMock> createPHolder(String name, AssertableMock leaf) {
        return new PHolder<>(CName.EMPTY.append(name), leaf);
    }

    @Test
    public void shouldUseAConsumerOnSecondOrder() {
        PHolder<PHolder<PHolder<AssertableMock>>> root =
                new PHolder<>(CName.EMPTY.append("root"));

        PHolder<PHolder<AssertableMock>> subroot =
                new PHolder<>(CName.EMPTY.append("subroot"));

        root.addChild(subroot);

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        final PHolder<AssertableMock> holder1 = createPHolder("one", leaf1);
        final PHolder<AssertableMock> holder2 = createPHolder("two", leaf2);
        final PHolder<AssertableMock> holder3 = createPHolder("three", leaf3);

        subroot.addChild(holder1);
        subroot.addChild(holder2);
        subroot.addChild(holder3);

        ConsumerMock<PHolder<PHolder<AssertableMock>>> consumer =
                new ConsumerMock<>();
        root.use(consumer);

        assertEquals(3, consumer.getList().size(), 0);
        assertTrue(consumer.getList().contains("1"));
        assertTrue(consumer.getList().contains("2"));
        assertTrue(consumer.getList().contains("3"));
    }

    @Test
    public void shouldUseAConsumerOnFirstOrder() {
        PHolder<PHolder<AssertableMock>> root =
                new PHolder<>(CName.EMPTY.append("subroot"));

        final AssertableMock leaf1 = new AssertableMock("1");
        final AssertableMock leaf2 = new AssertableMock("2");
        final AssertableMock leaf3 = new AssertableMock("3");

        final PHolder<AssertableMock> holder1 = createPHolder("one", leaf1);
        final PHolder<AssertableMock> holder2 = createPHolder("two", leaf2);
        final PHolder<AssertableMock> holder3 = createPHolder("three", leaf3);

        root.addChild(holder1);
        root.addChild(holder2);
        root.addChild(holder3);

        ConsumerMock<PHolder<AssertableMock>> consumer =
                new ConsumerMock<>();
        root.use(consumer);

        assertEquals(3, consumer.getList().size(), 0);
        assertTrue(consumer.getList().contains("1"));
        assertTrue(consumer.getList().contains("2"));
        assertTrue(consumer.getList().contains("3"));
    }

    @Test
    public void shouldUseAConsumerOnSimpleStats() {
        final AssertableMock leaf = new AssertableMock("1");
        final PHolder<AssertableMock> holder = createPHolder("one", leaf);

        ConsumerMock<AssertableMock> consumer = new ConsumerMock<>();
        holder.use(consumer);

        assertEquals(1, consumer.getList().size(), 0);
        assertTrue(consumer.getList().contains("1"));
    }

    @Test
    public void shouldUseAssertionOnSimpleStats() {
        final AssertableMock leaf = new AssertableMock("1");
        final PHolder<AssertableMock> holder = createPHolder("one", leaf);

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();
        holder.check(assertion);

        assertEquals(1, assertion.getList().size(), 0);
        assertTrue(assertion.getList().contains("1"));

        StringBuilder buf = new StringBuilder();
        holder.checkAndPrint(buf, assertion);

        assertEquals("1", buf.toString().trim());
    }
}
