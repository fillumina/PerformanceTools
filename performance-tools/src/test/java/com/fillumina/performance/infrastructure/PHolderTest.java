package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.ConsumerMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.UnmodificableTNameMapWrapper;
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
    public void shouldBeEmptyIfNoAssertableIsGiven() {
        PHolder<SpeedSample> holder = new PHolder<>((TName) null);

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldBeNotEmptyIfAssertableIsGiven() {
        SpeedSample sample = SpeedSampleMock
                .builder()
                .addTest("one").nansecondsPerOp(1).endTest()
                .addTest("two").nansecondsPerOp(2).endTest()
                .createSample();

        PHolder<SpeedSample> holder = new PHolder<>(sample);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldReturnGivenNameAndAssertable() {
        final AssertableMock assertable = new AssertableMock("leaf");
        final TName name = TN.name("root");
        PHolder<AssertableMock> holder = new PHolder<>(name, assertable);

        assertEquals(name, holder.getName());
        assertEquals(assertable, holder.getAssertable());
    }

    @Test
    public void shouldTwoHoldersContainingSameAssertableBeEqual() {
        final AssertableMock assertable = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = new PHolder<>("L", assertable);
        PHolder<AssertableMock> holder2 = new PHolder<>("L", assertable);

        assertEquals(holder1, holder2);
    }

    @Test
    public void shouldTwoHoldersContainingDifferentAssertableBeNotEqual() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = new PHolder<>("L", leaf);
        PHolder<AssertableMock> holder2 = new PHolder<>("S", leaf);

        assertNotEquals(holder1, holder2);
    }

    @Test
    public void shouldHaveSameHashCode() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = new PHolder<>("L", leaf);
        PHolder<AssertableMock> holder2 = new PHolder<>("L", leaf);

        assertEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldNotHaveSameHashCode() {
        final AssertableMock leaf = new AssertableMock("leaf");
        PHolder<AssertableMock> holder1 = new PHolder<>("L", leaf);
        PHolder<AssertableMock> holder2 = new PHolder<>("S", leaf);

        assertNotEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldAddSubExperiment() {
        AssertableMock subExperimentAssertable = new AssertableMock("leaf");
        PHolder<AssertableMock> subExperiment =
                new PHolder<>("L", subExperimentAssertable);

        PHolder<AssertableMock> holder = PHolder.<AssertableMock>experiment()
                .addSubExperiment(subExperiment)
                .build();

        LinkedTree<TName, AssertableMock> tree = holder.getTree();

        LinkedTree<TName, AssertableMock> subTree =
                tree.getTree(TN.name("L"));
        assertEquals("leaf", subTree.getValue().getName());
    }

    @Test
    public void shouldCreateTreeWithBuilder() {
        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>experiment("root")
                        .subExperiment("subroot")
                            .test("one", new AssertableMock("1"))
                            .test("two", new AssertableMock("2"))
                            .test("three", new AssertableMock("3"))
                        .build();

        LinkedTree<TName, AssertableMock> root = holder.getTree();

        assertEquals(1, root.size());
        assertEquals("root", root.getKey().getLastName());

        LinkedTree<TName, AssertableMock> subroot =
                root.getTree(TN.name("root", "subroot"));

        assertEquals(3, subroot.size());
        assertEquals("subroot", subroot.getKey().getLastName());

        assertEquals("1",
                subroot.get(TN.name("root", "subroot", "one")).getName());
        assertEquals("2",
                subroot.get(TN.name("root", "subroot", "two")).getName());
        assertEquals("3",
                subroot.get(TN.name("root", "subroot", "three")).getName());
    }

    @Test
    public void shouldUseAnAssertable() {
        SpeedSample sample = SpeedSampleMock.builder()
                .addTest("one").nansecondsPerOp(1).endTest()
                .addTest("two").nansecondsPerOp(2).endTest()
                .createSample();

        PHolder<SpeedSample> holder = new PHolder<>(sample);

        PerformanceConsumerExecutionChecker<SpeedSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertEquals(sample, consumer.getReceivedAssertable());
    }

    @Test
    public void shouldUseAConsumerOnSimpleStats() {
        final AssertableMock assertable = new AssertableMock("A");
        final PHolder<AssertableMock> holder = new PHolder<>("test", assertable);

        ConsumerMock<AssertableMock> consumer = new ConsumerMock<>();
        holder.use(consumer);

        final UnmodificableTNameMapWrapper<AssertableMock> consumedAssertableMap =
                consumer.getConsumedAssertableMap();

        assertEquals(1, consumedAssertableMap.size(), 0);
        assertEquals("A", consumedAssertableMap.get("test").getName());
    }

    @Test
    public void shouldPrintATree() {
        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>experiment("root")
                        .subExperiment("subroot")
                        .test("one", new AssertableMock("1"))
                        .test("two", new AssertableMock("2"))
                        .test("three", new AssertableMock("3"))
                        .build();

        final String CR = System.lineSeparator();

        assertEquals(CR + "root" + CR +
                "====" + CR +
                "" + CR +
                "root : subroot" + CR +
                "==============" + CR +
                "" + CR +
                "root : subroot : one" + CR +
                "--------------------" + CR +
                "1" + CR +
                "" + CR +
                "root : subroot : two" + CR +
                "--------------------" + CR +
                "2" + CR +
                "" + CR +
                "root : subroot : three" + CR +
                "----------------------" + CR +
                "3" + CR,
                holder.toString());
    }
}
