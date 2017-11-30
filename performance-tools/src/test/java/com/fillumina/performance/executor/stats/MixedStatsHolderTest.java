package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedStatsHolderTest {

    private static class AssertableMock_1 extends StatsMock {
        public static final Stats.Type TYPE = new MockStatsType("1");
        @Override public Type getType() { return TYPE; }
    }
    private static class AssertableMock_2 extends StatsMock {
        public static final Stats.Type TYPE = new MockStatsType("2");
        @Override public Type getType() { return TYPE; }
    }
    private static class AssertableMock_3 extends StatsMock {
        public static final Stats.Type TYPE = new MockStatsType("3");
        @Override public Type getType() { return TYPE; }
    }

    private static class StatsMock extends Stats {
        public static final Stats.Type TYPE = new MockStatsType("0");
        private static final long serialVersionUID = 1L;
        private final String name;

        public StatsMock() {
            this("ANONYMOUS");
        }

        public StatsMock(String name) {
            super(new StatsMockBuilder()
                    .addTest("test").mean(10.0).stdev(2.0).endTest()
                    .buildWithCoincidentalValues().getFirstStatsHolder().getStats());
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return TYPE;
        }
    }

    @Test
    public void shouldReturnTheExtendedType() {
        AssertableMock_1 a = new AssertableMock_1();
        assertEquals(AssertableMock_1.TYPE, a.getType());
    }

    @Test
    public void shouldEMPTYstaticFieldShouldBeEmpty() {
        MixedStatsHolder mixedHolder = MixedStatsHolder.EMPTY;
        assertTrue(mixedHolder.isEmpty());
    }

    @Test
    public void shouldBeEmptyIfNoMapIsSpecified() {
        MixedStatsHolder mixedHolder = new MixedStatsHolder();
        assertTrue(mixedHolder.isEmpty());
    }

    @Test
    public void shouldReturnTheGivenHolder() {
        StatsHolder holder =
                StatsHolder.builder(MockStatsType.INSTANCE, "root")
                        .test("one", new StatsMock("1"))
                        .build();

        MixedStatsHolder mixedHolder = new MixedStatsHolder(holder);

        assertEquals(holder, mixedHolder.getFirstStatsHolder());
    }

    @Test
    public void shouldReturnTheGivenHoldersAccordingToClass() {
        StatsHolder h1 = StatsHolder.builder(StatsMock.TYPE, "root")
                        .test("one", new StatsMock())
                        .build();

        StatsHolder h2 =
                StatsHolder.builder(AssertableMock_1.TYPE, "root")
                        .test("one", new AssertableMock_1())
                        .build();

        MixedStatsHolder mixedHolder = new MixedStatsHolder(h1, h2);

        assertEquals(h1, mixedHolder.getStatsHolder(StatsMock.TYPE));
        assertEquals(h2, mixedHolder.getStatsHolder(AssertableMock_1.TYPE));
    }

    @Test
    public void shouldBuildFromAssertables() {
        StatsMock a1 = new StatsMock();
        AssertableMock_1 a2 = new AssertableMock_1();
        MixedStatsHolder mixedHolder = MixedStatsHolder.builder()
                .addAssertable(StatsMock.TYPE,
                        TN.tname("one"), a1)
                .addAssertable(AssertableMock_1.TYPE,
                        TN.tname("two"), a2)
                .build();

        assertEquals(a1,
                mixedHolder.getStatsHolder(StatsMock.TYPE).getStats());
        assertEquals(a2,
                mixedHolder.getStatsHolder(AssertableMock_1.TYPE).getStats());
    }

    @Test
    public void shouldReturnTheRegisteredTypes() {
        StatsMock a1 = new StatsMock();
        AssertableMock_1 a2 = new AssertableMock_1();
        MixedStatsHolder mixedHolder = MixedStatsHolder.builder()
                .addAssertable(StatsMock.TYPE,
                        TN.tname("one"), a1)
                .addAssertable(AssertableMock_1.TYPE,
                        TN.tname("two"), a2)
                .build();

        Set<Stats.Type> set = mixedHolder.getTypes();
        assertTrue(set.containsAll(Arrays.asList(
                StatsMock.TYPE, AssertableMock_1.TYPE
        )));
    }

    @Test
    public void shouldReturnTheStatsMap() {
        StatsMock a1 = new StatsMock();
        AssertableMock_1 a2 = new AssertableMock_1();
        MixedStatsHolder mixedHolder = MixedStatsHolder.builder()
                .addAssertable(StatsMock.TYPE,
                        TN.tname("one"), a1)
                .addAssertable(AssertableMock_1.TYPE,
                        TN.tname("two"), a2)
                .build();

        Map<Stats.Type, StatsHolder> map = mixedHolder.getStatsMap();

        assertTrue(map.keySet().containsAll(Arrays.asList(
                StatsMock.TYPE, AssertableMock_1.TYPE
        )));

        assertTrue(map.values().stream().map((t) -> t.getStats())
                .collect(Collectors.toCollection(ArrayList::new))
                .containsAll(Arrays.asList(a1, a2)) );
    }

    @Test
    public void shouldJoinOneMixedAssertableHolders() {
        AssertableMock_1 a1 = new AssertableMock_1();
        MixedStatsHolder one = create("one_", a1);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .join();

        assertEquals(a1,
                getAssertable(root, AssertableMock_1.TYPE,
                        createName("one_", 0)));
    }

    @Test
    public void shouldJoinTwoMixedAssertableHolders() {
        AssertableMock_1 a1 = new AssertableMock_1();
        MixedStatsHolder one = create("one_", a1);
        AssertableMock_2 a2 = new AssertableMock_2();
        MixedStatsHolder two = create("two_", a2);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .addSubExperiment(two)
                .join();

        assertEquals(a1,
                getAssertable(root, AssertableMock_1.TYPE,
                        createName("one_", 0)));

        assertEquals(a2,
                getAssertable(root, AssertableMock_2.TYPE,
                        createName("two_", 0)));
    }

    @Test
    public void shouldJoinTwoMixedAssertableHoldersWithSameStats() {
        AssertableMock_1 a1 = new AssertableMock_1();
        AssertableMock_2 a2 = new AssertableMock_2();
        MixedStatsHolder one = create("one_", a1, a2);

        AssertableMock_1 b1 = new AssertableMock_1();
        AssertableMock_2 b2 = new AssertableMock_2();
        AssertableMock_3 b3 = new AssertableMock_3();
        MixedStatsHolder two = create("two_", b1, b2, b3);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .addSubExperiment(two)
                .join();

        assertEquals(a1,
                getAssertable(root, AssertableMock_1.TYPE,
                        createName("one_", 0)));

        assertEquals(a2,
                getAssertable(root, AssertableMock_2.TYPE,
                        createName("one_", 1)));

        assertEquals(b1,
                getAssertable(root, AssertableMock_1.TYPE,
                        createName("two_", 0)));

        assertEquals(b2,
                getAssertable(root, AssertableMock_2.TYPE,
                        createName("two_", 1)));

        assertEquals(b3,
                getAssertable(root, AssertableMock_3.TYPE,
                        createName("two_", 2)));
    }

    @Test
    public void shouldSetTheGivenRootName() {
        AssertableMock_1 a1 = new AssertableMock_1();
        MixedStatsHolder one = create("one_", a1);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .join();

        assertEquals(rootName, root.getStatsHolder(AssertableMock_1.TYPE).getName());
    }

    private MixedStatsHolder create(String prefix, StatsMock... array) {
        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        int index = 0;
        for (StatsMock a : array) {
            builder.addAssertable(a.getType(),
                    createName(prefix, index),
                    a);
            index++;
        }
        return builder.build();
    }

    private static TName createName(String prefix, int index) {
        return TN.tname(prefix + Integer.toString(index));
    }

    private Assertable getAssertable(MixedStatsHolder mixedHolder,
            Stats.Type type, TName name) {
        return mixedHolder
                .getStatsHolder(type)
                .getTree()
                .getTreeAtPath(name)
                .getValue();
    }
}
