package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.ConsumerMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TNameMatcher;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.stats.Ratio;
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
    public void shouldBeEmptyIfNoAssertableIsGiven() {
        PHolder<TimeSample> holder = new PHolder<>((TName) null);

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldBeNotEmptyIfAssertableIsGiven() {
        TimeSample sample = SpeedSampleMock
                .builder()
                .addTest("one").nansecondsPerOp(1).endTest()
                .addTest("two").nansecondsPerOp(2).endTest()
                .createSample();

        PHolder<TimeSample> holder = new PHolder<>(sample);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldReturnGivenNameAndAssertable() {
        final AssertableMock assertable = new AssertableMock("leaf");
        final TName name = TN.tname("root");
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
                tree.getTree(TN.tname("L"));
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
                root.getTree(TN.tname("root", "subroot"));

        assertEquals(3, subroot.size());
        assertEquals("subroot", subroot.getKey().getLastName());

        assertEquals("1",
                subroot.get(TN.tname("root", "subroot", "one")).getName());
        assertEquals("2",
                subroot.get(TN.tname("root", "subroot", "two")).getName());
        assertEquals("3",
                subroot.get(TN.tname("root", "subroot", "three")).getName());
    }

    @Test
    public void shouldUseAnAssertable() {
        TimeSample sample = SpeedSampleMock.builder()
                .addTest("one").nansecondsPerOp(1).endTest()
                .addTest("two").nansecondsPerOp(2).endTest()
                .createSample();

        PHolder<TimeSample> holder = new PHolder<>(sample);

        PerformanceConsumerExecutionChecker<TimeSample> consumer =
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

        final List<AssertableMock> consumedAssertableList =
                consumer.getConsumedAssertableList();

        assertEquals(1, consumedAssertableList.size(), 0);
        assertEquals("A", consumedAssertableList.get(0).getName());
    }

    @Test
    public void shouldPrintATree() {
        AssertableMock assertable1 = new AssertableMock("1");
        AssertableMock assertable2 = new AssertableMock("2");
        AssertableMock assertable3 = new AssertableMock("3");

        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>experiment("root")
                        .subExperiment("subroot")
                        .test("one", assertable1)
                        .test("two", assertable2)
                        .test("three", assertable3)
                        .build();

        final String CR = System.lineSeparator();

        assertEquals(
                CR +
                "root" + CR +
                "====" + CR +
                CR +
                "root : subroot" + CR +
                "==============" + CR +
                CR +
                "root : subroot : one" + CR +
                "--------------------" + CR +
                assertable1.toString() + CR +
                CR +
                "root : subroot : two" + CR +
                "--------------------" + CR +
                assertable2.toString() + CR +
                CR +
                "root : subroot : three" + CR +
                "----------------------" + CR +
                assertable3.toString() + CR,
                holder.toString());
    }

    @Test
    public void shouldAddComplexAssertions() {
        TName one = TN.tname("root", "subroot", "one");
        TName two = TN.tname("root", "subroot", "two");
        TName three = TN.tname("root", "subroot", "three");

        PHolder<AssertableMock> holder =
                PHolder.<AssertableMock>experiment("root")
                        .subExperiment("subroot")
                            .test("one", AssertableMock.createWithTName(one,
                                    "first", 10.0, "second", 20.0 ))
                            .test("two", AssertableMock.createWithTName(two,
                                    "first", 10.0, "second", 20.0 ))
                            .test("three", AssertableMock.createWithTName(three,
                                    "first", 10.0, "second", 20.0 ))
                        .build();

        // using long matcher setter
        holder.check()
                .order(TNameMatcher.builder().all().string("first").build())
                .lessThan(TNameMatcher.builder().all().string("second").build());

        // using fluid interface
        holder.check().value()
                .all().string("second").end()
                .equalsTo(20.0).end();

        holder.check().percentage()
                .string("root", "subroot", "one", "first").end()
                .equalsTo(Ratio.percentage(50)).end();

        StringBuilder buf = new StringBuilder();
        holder.checkAndAppendTo(buf).percentage()
                .string("root", "subroot", "one", "first").end()
                .equalsTo(Ratio.percentage(50)).end();

        assertEquals(
                "'root : subroot : one : first' (50.00 +/- 0.00 %)  " +
                "is equals to 50.000 % with a tolerance of 10.000 %" +
                System.lineSeparator(),
                buf.toString());
    }
}
