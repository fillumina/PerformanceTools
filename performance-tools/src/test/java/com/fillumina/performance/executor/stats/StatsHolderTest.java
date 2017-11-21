package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsHolderTest {

    private static class StatsMock extends Stats {
        private static final long serialVersionUID = 1L;
        private final String name;

        public StatsMock(String name) {
            super(new StatsMockBuilder()
                    .addTest("test").mean(10.0).stdev(2.0).endTest()
                    .buildWithCoincidentalValues().getOnlyHolder().getStats());
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    public void shouldBeEmptyIfNoAssertableIsGiven() {
        StatsHolder holder = new StatsHolder(MockStatsType.INSTANCE, null);

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldBeNotEmptyIfAssertableIsGiven() {
        final StatsMock assertable = new StatsMock("one");

        StatsHolder holder = new StatsHolder(
                MockStatsType.INSTANCE,
                assertable);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldReturnGivenNameAndAssertable() {
        final StatsMock assertable = new StatsMock("leaf");
        final TName name = TN.tname("root");
        StatsHolder holder = new StatsHolder(
                MockStatsType.INSTANCE, name, assertable);

        assertEquals(name, holder.getName());
        assertEquals(assertable, holder.getStats());
    }

    @Test
    public void shouldTwoHoldersContainingSameAssertableBeEqual() {
        final StatsMock assertable = new StatsMock("leaf");
        StatsHolder holder1 =
                new StatsHolder(MockStatsType.INSTANCE, "L", assertable);
        StatsHolder holder2 =
                new StatsHolder(MockStatsType.INSTANCE, "L", assertable);

        assertEquals(holder1, holder2);
    }

    @Test
    public void shouldTwoHoldersContainingDifferentAssertableBeNotEqual() {
        final StatsMock leaf = new StatsMock("leaf");
        StatsHolder holder1 =
                new StatsHolder(MockStatsType.INSTANCE, "L", leaf);
        StatsHolder holder2 =
                new StatsHolder(MockStatsType.INSTANCE, "S", leaf);

        assertNotEquals(holder1, holder2);
    }

    @Test
    public void shouldHaveSameHashCode() {
        final StatsMock leaf = new StatsMock("leaf");
        StatsHolder holder1 =
                new StatsHolder(MockStatsType.INSTANCE, "L", leaf);
        StatsHolder holder2 =
                new StatsHolder(MockStatsType.INSTANCE, "L", leaf);

        assertEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldNotHaveSameHashCode() {
        final StatsMock leaf = new StatsMock("leaf");
        StatsHolder holder1 =
                new StatsHolder(MockStatsType.INSTANCE, "L", leaf);
        StatsHolder holder2 =
                new StatsHolder(MockStatsType.INSTANCE, "S", leaf);

        assertNotEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldAddSubExperiment() {
        StatsMock subExperimentAssertable = new StatsMock("leaf");
        StatsHolder subExperiment =
                new StatsHolder(MockStatsType.INSTANCE, "L",
                        subExperimentAssertable);

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE)
                .addSubExperiment(subExperiment)
                .build();

        LinkedTree<TName, Stats> tree = holder.getTree();

        LinkedTree<TName, Stats> subTree =
                tree.getTree(TN.tname("L"));
        assertEquals("leaf", ((StatsMock)subTree.getValue()).getName());
    }

    @Test
    public void shouldGetTheAssertableAtPath() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot")
                            .test("one", new StatsMock("1"))
                            .test("two", new StatsMock("2"))
                            .test("three", new StatsMock("3"))
                        .build();

        Stats a = holder.getStatsAtPath("root", "subroot", "one");

        assertNotNull(a);
    }

    @Test
    public void shouldGetTheAssertableByPathOmittingRoot() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot")
                            .test("one", new StatsMock("1"))
                            .test("two", new StatsMock("2"))
                            .test("three", new StatsMock("3"))
                        .build();

        Stats a = holder.getStatsAtPath("subroot", "two");
        Stats b = holder.getStatsAtPath("root", "subroot", "two");
        assertNotNull(a);
        assertEquals(a, b);
    }

    @Test
    public void shouldCreateTreeWithBuilder() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot")
                            .test("one", new StatsMock("1"))
                            .test("two", new StatsMock("2"))
                            .test("three", new StatsMock("3"))
                        .build();

        LinkedTree<TName, Stats> root = holder.getTree();

        assertEquals(1, root.size());
        assertEquals("root", root.getKey().getLastName());

        LinkedTree<TName, Stats> subroot =
                root.getTree(TN.tname("root", "subroot"));

        assertEquals(3, subroot.size());
        assertEquals("subroot", subroot.getKey().getLastName());

        assertEquals("1",
                ((StatsMock)subroot.get(TN.tname("root", "subroot", "one"))).getName());
        assertEquals("2",
                ((StatsMock)subroot.get(TN.tname("root", "subroot", "two"))).getName());
        assertEquals("3",
                ((StatsMock)subroot.get(TN.tname("root", "subroot", "three"))).getName());
    }

    @Test
    public void shouldUseAnAssertable() {
        StatsMock assertable = new StatsMock("one");

        StatsHolder holder =
                new StatsHolder(MockStatsType.INSTANCE, assertable);

        Holder<String> str = new Holder<>();
        holder.use(t -> str.setValue(((StatsMock)t).getName()) );

        assertEquals("one", str.getValue());
    }

    @Test
    public void shouldPrintATree() {
        StatsMock assertable1 = new StatsMock("1");
        StatsMock assertable2 = new StatsMock("2");
        StatsMock assertable3 = new StatsMock("3");

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
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

        Stats.Type type = MockStatsType.INSTANCE;

        StatsHolder holder =
                StatsHolder.builder(type, "root")
                        .subExperiment("subroot")
                            .test("one", StatsMockBuilder.create(type,
                                    one, "first", 10.0, "second", 20.0 ))
                            .test("two", StatsMockBuilder.create(type,
                                    two, "first", 10.0, "second", 20.0 ))
                            .test("three", StatsMockBuilder.create(type,
                                    three, "first", 10.0, "second", 20.0 ))
                        .build();

        //System.out.println(holder.toString());

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

        holder.check().order()
                .string("root", "subroot", "one", "first").end()
                .equalsTo()
                .string("root", "subroot", "two", "first").end();

        StringBuilder buf = new StringBuilder();
        holder.checkAndAppendTo(buf).percentage()
                .string("root", "subroot", "one", "first").end()
                .equalsTo(Ratio.percentage(50)).end();

        //System.out.println(buf.toString());

        assertTrue(buf.length() > 10);
    }

    @Test
    public void shouldReturnAssertableType() {
        StatsHolder holder =
                new StatsHolder(MockStatsType.INSTANCE, null);

        assertEquals(MockStatsType.INSTANCE, holder.getStatsType());
    }

    @Test
    public void shouldGetFlattenedMap() {
        StatsMock assertable1 = new StatsMock("1");
        StatsMock assertable2 = new StatsMock("2");
        StatsMock assertable3 = new StatsMock("3");
        StatsMock assertable4 = new StatsMock("4");
        StatsMock assertable5 = new StatsMock("5");
        StatsMock assertable6 = new StatsMock("6");

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot1")
                            .test("one", assertable1)
                            .test("two", assertable2)
                            .test("three", assertable3)
                        .endSubExperiment()
                        .subExperiment("subroot2")
                            .test("one", assertable4)
                            .test("two", assertable5)
                            .subExperiment("subroot21")
                                .test("one", assertable6)
                    .build();

        Map<TName, Stats> map = holder.getFlattenedAssertableMap();

        assertEquals(6, map.size());

        TName subRoot1 = TN.tname("root", "subroot1");
        TName subRoot2 = TN.tname("root", "subroot2");

        assertEquals(assertable1, map.get(subRoot1.append("one")));
        assertEquals(assertable2, map.get(subRoot1.append("two")));
        assertEquals(assertable3, map.get(subRoot1.append("three")));

        assertEquals(assertable4, map.get(subRoot2.append("one")));
        assertEquals(assertable5, map.get(subRoot2.append("two")));

        assertEquals(assertable6, map.get(subRoot2.append("subroot21", "one")));
    }

    @Test
    public void shouldCheckAssertion() {
        StatsMock assertable1 = new StatsMock("1");
        StatsMock assertable2 = new StatsMock("2");
        StatsMock assertable3 = new StatsMock("3");
        StatsMock assertable4 = new StatsMock("4");
        StatsMock assertable5 = new StatsMock("5");
        StatsMock assertable6 = new StatsMock("6");

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot1")
                            .test("one", assertable1)
                            .test("two", assertable2)
                            .test("three", assertable3)
                        .endSubExperiment()
                        .subExperiment("subroot2")
                            .test("one", assertable4)
                            .test("two", assertable5)
                            .subExperiment("subroot21")
                                .test("one", assertable6)
                    .build();

        AssertionMock assertion = new AssertionMock();

        holder.check(assertion);

        List<Assertable> list = assertion.getConsumedAssertableList();

        assertEquals(6, list.size());

        assertTrue( list.containsAll(Arrays.asList(
                assertable1, assertable2, assertable3,
                assertable4, assertable5, assertable6)) );
    }

    @Test
    public void shouldCheckAndAppendAssertion() {
        StatsMock assertable1 = new StatsMock("1");
        StatsMock assertable2 = new StatsMock("2");
        StatsMock assertable3 = new StatsMock("3");
        StatsMock assertable4 = new StatsMock("4");
        StatsMock assertable5 = new StatsMock("5");
        StatsMock assertable6 = new StatsMock("6");

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot1")
                            .test("one", assertable1)
                            .test("two", assertable2)
                            .test("three", assertable3)
                        .endSubExperiment()
                        .subExperiment("subroot2")
                            .test("one", assertable4)
                            .test("two", assertable5)
                            .subExperiment("subroot21")
                                .test("one", assertable6)
                    .build();

        AssertionMock assertion = new AssertionMock();

        StringBuilder buf = new StringBuilder();
        holder.checkAndAppendTo(buf, assertion);

        Assertable[] array = new Assertable[] {
            assertable1, assertable2, assertable3,
            assertable4, assertable5, assertable6
        };

        StringBuilder req = new StringBuilder();
        for (Assertable a : array) {
            req.append(a).append(System.lineSeparator());
        }

        assertEquals(req.toString(), buf.toString());
    }
}
