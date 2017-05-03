package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.ConsumerMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedTree;
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
        PHolder<SpeedSample> holder = new PHolder<>((TName)null);

        assertTrue(holder.isNull());
    }

    @Test
    public void shouldEmptyBeEmpty() {
        PHolder<SpeedSample> holder = PHolder.empty();

        assertTrue(holder.isNull());
    }

    @Test
    public void shouldReportThePresenceOfAPerformance() {
        SpeedSample sample = SpeedSampleMock
                .builder()
                    .addTest("one").timePerOp(1).endTest()
                    .addTest("two").timePerOp(2).endTest()
                .createSample();

        PHolder<SpeedSample> holder = PHolder.createWithValue(sample);

        assertFalse(holder.isNull());
    }

    @Test
    public void shouldUseAPerformance() {
        SpeedSample sample = SpeedSampleMock
                .builder()
                    .addTest("one").timePerOp(1).endTest()
                    .addTest("two").timePerOp(2).endTest()
                .createSample();

        PHolder<SpeedSample> holder = PHolder.createWithValue(sample);

        PerformanceConsumerExecutionChecker<SpeedSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertEquals(sample, consumer.getReceivedPerformance());
    }

    @Test
    public void shouldReturnRoot() {
        final AssertableMock root = new AssertableMock("leaf");
        final TName rootName = TN.n("root");
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
        AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> childHolder = createPHolder("L", leaf);

        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>builder((String)null)
                .addChild(childHolder)
                .build();

        LinkedTree<TName,AssertableMock> tree = holder.getTree();

        LinkedTree<TName,AssertableMock> subTree =
                tree.getTree(TN.n("L"));
        assertEquals("leaf", subTree.getValue().getName());
    }

    @Test
    public void shouldCreateTreeWithBuilder() {
        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>builder("root")
                    .branch("subroot")
                        .leaf("one", new AssertableMock("1"))
                        .leaf("two", new AssertableMock("2"))
                        .leaf("three", new AssertableMock("3"))
                    .build();

        LinkedTree<TName, AssertableMock> root = holder.getTree();

        assertEquals(1, root.size());
        assertEquals("root", root.getKey().getLastName());

        LinkedTree<TName, AssertableMock> subroot =
                root.getTree(TN.n("root", "subroot"));

        assertEquals(3, subroot.size());
        assertEquals("subroot", subroot.getKey().getLastName());

        assertEquals("1",
                subroot.get(TN.n("root", "subroot", "one")).getName());
        assertEquals("2",
                subroot.get(TN.n("root", "subroot", "two")).getName());
        assertEquals("3",
                subroot.get(TN.n("root", "subroot", "three")).getName());
    }

    @Test
    public void shouldBeEmptyIfNoChildren() {
        final AssertableMock leaf1 = new AssertableMock("1");
        PHolder<AssertableMock> holder = createPHolder("one", leaf1);

        assertTrue(holder.isChildless());
    }

    private PHolder<AssertableMock> createPHolder(String name,
            AssertableMock leaf) {
        return new PHolder<>(TN.n(name), leaf);
    }

    @Test
    public void shouldUseAConsumerOnSimpleStats() {
        final AssertableMock leaf = new AssertableMock("1");
        final PHolder<AssertableMock> holder = createPHolder("one", leaf);

        ConsumerMock<AssertableMock> consumer = new ConsumerMock<>();
        holder.use(consumer);

        assertEquals(1, consumer.getConsumedAssertableMap().size(), 0);
        assertEquals("1", consumer.getConsumedAssertableMap().get("one").getName());
    }

    @Test
    public void shouldPrintATree() {
        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>builder("root")
                    .branch("subroot")
                        .leaf("one", new AssertableMock("1"))
                        .leaf("two", new AssertableMock("2"))
                        .leaf("three", new AssertableMock("3"))
                    .build();

        System.out.println(holder.toString());
    }
}
