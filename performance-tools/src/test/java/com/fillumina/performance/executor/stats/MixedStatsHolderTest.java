package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Magnitude;
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
        Stats stats = createTypedStatsMock("1");
        StatsHolder holder = StatsHolder.builder(stats.getStatsType(), "root")
                        .test("one", stats)
                        .build();

        MixedStatsHolder mixedHolder = new MixedStatsHolder(holder);

        assertEquals(holder, mixedHolder.getFirstStatsHolder());
    }

    @Test
    public void shouldReturnTheGivenHoldersAccordingToType() {
        Stats stats1 = createTypedStatsMock("1");
        StatsHolder h1 = StatsHolder.builder(stats1.getStatsType(), "root")
                        .test("one", stats1)
                        .build();

        Stats stats2 = createTypedStatsMock("2");
        StatsHolder h2 = StatsHolder.builder(stats2.getStatsType(), "root")
                        .test("one", stats2)
                        .build();

        MixedStatsHolder mixedHolder = new MixedStatsHolder(h1, h2);

        assertEquals(h1, mixedHolder.getStatsHolder(stats1.getStatsType()));
        assertEquals(h2, mixedHolder.getStatsHolder(stats2.getStatsType()));
    }

    @Test
    public void shouldBuildFromStats() {
        Stats stats1 = createTypedStatsMock("1");
        Stats stats2 = createTypedStatsMock("2");
        MixedStatsHolder mixedHolder = MixedStatsHolder.builder()
                .addStats(TN.tname("one"), stats1)
                .addStats(TN.tname("two"), stats2)
                .build();

        assertEquals(stats1,
                mixedHolder.getStatsHolder(stats1.getStatsType()).getStats());
        assertEquals(stats2,
                mixedHolder.getStatsHolder(stats2.getStatsType()).getStats());
    }

    @Test
    public void shouldReturnTheRegisteredTypes() {
        Stats stats1 = createTypedStatsMock("1");
        Stats stats2 = createTypedStatsMock("2");
        MixedStatsHolder mixedHolder = MixedStatsHolder.builder()
                .addStats(TN.tname("one"), stats1)
                .addStats(TN.tname("two"), stats2)
                .build();

        Set<StatsType> set = mixedHolder.getTypes();
        assertTrue(set.containsAll(Arrays.asList(
                stats1.getStatsType(), stats2.getStatsType()
        )));
    }

    @Test
    public void shouldReturnTheStatsMap() {
        Stats stats1 = createTypedStatsMock("1");
        Stats stats2 = createTypedStatsMock("2");
        MixedStatsHolder mixedHolder = MixedStatsHolder.builder()
                .addStats(TN.tname("one"), stats1)
                .addStats(TN.tname("two"), stats2)
                .build();

        Map<StatsType, StatsHolder> map = mixedHolder.getStatsMap();

        assertTrue(map.keySet().containsAll(Arrays.asList(
                stats1.getStatsType(), stats2.getStatsType()
        )));

        assertTrue(map.values().stream().map((t) -> t.getStats())
                .collect(Collectors.toCollection(ArrayList::new))
                .containsAll(Arrays.asList(stats1, stats2)) );
    }

    @Test
    public void shouldJoinOneMixedStatsHolders() {
        Stats stats1 = createTypedStatsMock("1");
        MixedStatsHolder one = create("one_", stats1);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .join();

        assertEquals(stats1,
                getAssertable(root, stats1.getStatsType(),
                        createName("one_", 0)));
    }

    @Test
    public void shouldJoinTwoMixedStatsHolders() {
        Stats stats1 = createTypedStatsMock("1");
        Stats stats2 = createTypedStatsMock("2");
        MixedStatsHolder one = create("one_", stats1);
        MixedStatsHolder two = create("two_", stats2);

        TName rootName = TN.tname("root");

        MixedStatsHolder mixedStatsHolder = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .addSubExperiment(two)
                .join();

        assertEquals(stats1,
                getAssertable(mixedStatsHolder, stats1.getStatsType(),
                        createName("one_", 0)));

        assertEquals(stats2,
                getAssertable(mixedStatsHolder, stats2.getStatsType(),
                        createName("two_", 0)));
    }

    @Test
    public void shouldJoinTwoMixedStatsHoldersWithSameStats() {
        Stats a1 = createTypedStatsMock("a1");
        Stats a2 = createTypedStatsMock("a2");
        MixedStatsHolder one = create("one_", a1, a2);

        Stats b1 = createTypedStatsMock("b1");
        Stats b2 = createTypedStatsMock("b2");
        Stats b3 = createTypedStatsMock("b3");
        MixedStatsHolder two = create("two_", b1, b2, b3);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .addSubExperiment(two)
                .join();

        assertEquals(a1,
                getAssertable(root, a1.getStatsType(),
                        createName("one_", 0)));

        assertEquals(a2,
                getAssertable(root, a2.getStatsType(),
                        createName("one_", 1)));

        assertEquals(b1,
                getAssertable(root, b1.getStatsType(),
                        createName("two_", 0)));

        assertEquals(b2,
                getAssertable(root, b2.getStatsType(),
                        createName("two_", 1)));

        assertEquals(b3,
                getAssertable(root, b3.getStatsType(),
                        createName("two_", 2)));
    }

    @Test
    public void shouldSetTheGivenRootName() {
        Stats s = createTypedStatsMock("2");
        MixedStatsHolder one = create("one_", s);

        TName rootName = TN.tname("root");

        MixedStatsHolder root = MixedStatsHolder.joiner(rootName)
                .addSubExperiment(one)
                .join();

        assertEquals(rootName, root.getStatsHolder(s.getStatsType()).getName());
    }

    private MixedStatsHolder create(String prefix, Stats... array) {
        MixedStatsHolder.Builder builder = MixedStatsHolder.builder();
        int index = 0;
        for (Stats a : array) {
            builder.addStats(createName(prefix, index), a);
            index++;
        }
        return builder.build();
    }

    private static TName createName(String prefix, int index) {
        return TN.tname(prefix + Integer.toString(index));
    }

    private AssertableExperiment getAssertable(MixedStatsHolder mixedHolder,
            StatsType type, TName name) {
        return mixedHolder
                .getStatsHolder(type)
                .getTree()
                .getTreeAtPath(name)
                .getValue();
    }
}
