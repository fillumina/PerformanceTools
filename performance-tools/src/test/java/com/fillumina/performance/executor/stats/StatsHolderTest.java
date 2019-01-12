package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMatcher;
import com.fillumina.performance.util.unit.Absolute;
import com.fillumina.performance.util.unit.Magnitude;
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

    private Stats createTypedStatsMock(String typeName) {
        StatsType type = new StatsTypeImpl(typeName);
        return new StatsMockBuilder(type)
                    .addTest("test").mean(10.0).stdev(2.0).endTest()
                    .buildWithCoincidentalValues(Magnitude.UNIT)
                    .getFirstStatsHolder()
                    .getStats()
                    .as(Magnitude.UNIT);
    }

    @Test
    public void shouldBeEmptyIfNoStatsIsGiven() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, (String)null).build();

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldBeNotEmptyIfStatsIsGiven() {
        final Stats stats = createTypedStatsMock("one");

        StatsHolder holder = new StatsHolder(stats);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldReturnGivenNameAndStats() {
        final Stats stats = createTypedStatsMock("leaf");
        final TName name = TN.tname("root");
        StatsHolder holder = new StatsHolder(name, stats);

        assertEquals(name, holder.getName());
        assertEquals(stats, holder.getStats());
    }

    @Test
    public void shouldTwoHoldersContainingSameStatsBeEqual() {
        final Stats stats = createTypedStatsMock("leaf");
        StatsHolder holder1 = new StatsHolder(stats);
        StatsHolder holder2 = new StatsHolder(stats);

        assertEquals(holder1, holder2);
    }

    @Test
    public void shouldTwoHoldersContainingDifferentStatsTypeBeNotEqual() {
        final Stats s1 = createTypedStatsMock("1");
        final Stats s2 = createTypedStatsMock("2");
        StatsHolder holder1 = new StatsHolder(s1);
        StatsHolder holder2 = new StatsHolder(s2);

        assertNotEquals(holder1, holder2);
    }

    @Test
    public void shouldHaveSameHashCode() {
        final Stats stats = createTypedStatsMock("leaf");
        StatsHolder holder1 = new StatsHolder(stats);
        StatsHolder holder2 = new StatsHolder(stats);

        assertEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldNotHaveSameHashCode() {
        final Stats s1 = createTypedStatsMock("1");
        final Stats s2 = createTypedStatsMock("2");
        StatsHolder holder1 = new StatsHolder(s1);
        StatsHolder holder2 = new StatsHolder(s2);

        assertNotEquals(holder1.hashCode(), holder2.hashCode(), 0);
    }

    @Test
    public void shouldAddSubExperiment() {
        TName name = TN.tname("L");
        Stats stats = createTypedStatsMock("leaf");
        StatsHolder subExperiment = new StatsHolder(name, stats);

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE)
                .addSubExperiment(subExperiment)
                .build();

        LinkedTree<TName, Stats> tree = holder.getTree();

        LinkedTree<TName, Stats> subTree = tree.getTree(name);
        assertEquals(stats, subTree.getValue());
    }

    @Test
    public void shouldGetTheStatsAtPath() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot")
                            .test("one", createTypedStatsMock("1"))
                            .test("two", createTypedStatsMock("2"))
                            .test("three", createTypedStatsMock("3"))
                        .build();

        Stats a = holder.getStatsAtPath("root", "subroot", "one");

        assertNotNull(a);
    }

    @Test
    public void shouldGetTheStatsByPathOmittingRoot() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot")
                            .test("one", createTypedStatsMock("1"))
                            .test("two", createTypedStatsMock("2"))
                            .test("three", createTypedStatsMock("3"))
                        .build();

        Stats a = holder.getStatsAtPath("subroot", "two");
        Stats b = holder.getStatsAtPath("root", "subroot", "two");
        assertNotNull(a);
        assertEquals(a, b);
    }

    @Test
    public void shouldCreateTreeWithBuilder() {
        final Stats s1 = createTypedStatsMock("1");
        final Stats s2 = createTypedStatsMock("2");
        final Stats s3 = createTypedStatsMock("3");

        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .subExperiment("subroot")
                            .test("one", s1)
                            .test("two", s2)
                            .test("three", s3)
                        .build();

        LinkedTree<TName, Stats> root = holder.getTree();

        assertEquals(1, root.size());
        assertEquals("root", root.getKey().getLastName());

        LinkedTree<TName, Stats> subroot =
                root.getTree(TN.tname("root", "subroot"));

        assertEquals(3, subroot.size());
        assertEquals("subroot", subroot.getKey().getLastName());

        assertEquals(s1, subroot.get(TN.tname("root", "subroot", "one")));
        assertEquals(s2, subroot.get(TN.tname("root", "subroot", "two")));
        assertEquals(s3, subroot.get(TN.tname("root", "subroot", "three")));
    }

    @Test
    public void shouldUseAStats() {
        Stats stats = createTypedStatsMock("one");
        StatsHolder holder = new StatsHolder(stats);

        Holder<Stats> h = new Holder<>();

        holder.use(t -> h.setValue(t) );

        assertEquals(stats, h.getValue());
    }

    @Test
    public void shouldPrintATree() {
        Stats assertable1 = createTypedStatsMock("1");
        Stats assertable2 = createTypedStatsMock("2");
        Stats assertable3 = createTypedStatsMock("3");

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

        StatsType type = MockStatsType.INSTANCE;

        StatsHolder holder =
                StatsHolder.builder(type, "root")
                        .subExperiment("subroot")
                            .test("one", StatsMockBuilder.createWithTypes(type,
                                    one, Absolute.UNIT,
                                    "first", 10.0, "second", 20.0 ))
                            .test("two", StatsMockBuilder.createWithTypes(type,
                                    two, Absolute.UNIT,
                                     "first", 10.0, "second", 20.0 ))
                            .test("three", StatsMockBuilder.createWithTypes(type,
                                    three, Absolute.UNIT,
                                     "first", 10.0, "second", 20.0 ))
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
                StatsHolder.builder(MockStatsType.INSTANCE, "root").build();

        assertEquals(MockStatsType.INSTANCE, holder.getStatsType());
    }

    @Test
    public void shouldGetFlattenedMap() {
        Stats assertable1 = createTypedStatsMock("1");
        Stats assertable2 = createTypedStatsMock("2");
        Stats assertable3 = createTypedStatsMock("3");
        Stats assertable4 = createTypedStatsMock("4");
        Stats assertable5 = createTypedStatsMock("5");
        Stats assertable6 = createTypedStatsMock("6");

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
        Stats assertable1 = createTypedStatsMock("1");
        Stats assertable2 = createTypedStatsMock("2");
        Stats assertable3 = createTypedStatsMock("3");
        Stats assertable4 = createTypedStatsMock("4");
        Stats assertable5 = createTypedStatsMock("5");
        Stats assertable6 = createTypedStatsMock("6");

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

        List<AssertableExperiment> list = assertion.getConsumedAssertableList();

        assertEquals(6, list.size());

        assertTrue( list.containsAll(Arrays.asList(
                assertable1, assertable2, assertable3,
                assertable4, assertable5, assertable6)) );
    }

    @Test
    public void shouldCheckAndAppendAssertion() {
        Stats assertable1 = createTypedStatsMock("1");
        Stats assertable2 = createTypedStatsMock("2");
        Stats assertable3 = createTypedStatsMock("3");
        Stats assertable4 = createTypedStatsMock("4");
        Stats assertable5 = createTypedStatsMock("5");
        Stats assertable6 = createTypedStatsMock("6");

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

        AssertableExperiment[] array = new AssertableExperiment[] {
            assertable1, assertable2, assertable3,
            assertable4, assertable5, assertable6
        };

        StringBuilder req = new StringBuilder();
        for (AssertableExperiment a : array) {
            req.append(a).append(System.lineSeparator());
        }

        assertEquals(req.toString(), buf.toString());
    }
}
