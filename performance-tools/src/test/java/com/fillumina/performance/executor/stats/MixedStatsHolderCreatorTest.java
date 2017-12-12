package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleCreator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.unit.IntervalUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedStatsHolderCreatorTest {

    @Test
    public void shouldCreateMixedStatsWithSingleType() {
        MixedStatsHolderCreator creator =
                new MixedStatsHolderCreator(TN.tname("first"));

        creator.addSample(createSample(1, 10));
        creator.addSample(createSample(2, 20));
        creator.addSample(createSample(3, 30));
        creator.addSample(createSample(4, 40));
        creator.addSample(createSample(5, 50));

        MixedStatsHolder holder =
                creator.getMixedAssertableHolder(ListFilter.identity());

        Stats stats = holder.getStatsHolder(MockStatsType.INSTANCE).getStats();

        assertEquals(3.0,
                stats.getMeasure("one").in(IntervalUnit.NANOSECONDS).getMean(),
                1E-3);
        assertEquals(30.0,
                stats.getMeasure("two").in(IntervalUnit.NANOSECONDS).getMean(),
                1E-3);
    }

    private Sample createSample(double a, double b) {
        return new Sample(MockStatsType.INSTANCE,
                SampleCreator.createMap("one", a, "two", b));
    }

    @Test
    public void shouldCreateMixedStatsWithMultipleTypes() {
        MixedStatsHolderCreator creator =
                new MixedStatsHolderCreator(TN.tname("first"));

        StatsType type1 = new StatsType(){};
        StatsType type2 = new StatsType(){};

        creator.addSample(
                SampleCreator.createSample(type1, "one", 10.0, "two", 20.0));
        creator.addSample(
                SampleCreator.createSample(type1, "one", 10.1, "two", 19.8));
        creator.addSample(
                SampleCreator.createSample(type1, "one", 9.88, "two", 20.2));
        creator.addSample(
                SampleCreator.createSample(type1, "one", 9.79, "two", 19.9));
        creator.addSample(
                SampleCreator.createSample(type1, "one", 10.2, "two", 20.1));

        creator.addSample(
                SampleCreator.createSample(type2, "one", 30.0, "two", 40.0));
        creator.addSample(
                SampleCreator.createSample(type2, "one", 30.1, "two", 39.8));
        creator.addSample(
                SampleCreator.createSample(type2, "one", 29.88, "two", 40.2));
        creator.addSample(
                SampleCreator.createSample(type2, "one", 29.79, "two", 39.9));
        creator.addSample(
                SampleCreator.createSample(type2, "one", 30.2, "two", 40.1));

        MixedStatsHolder holder =
                creator.getMixedAssertableHolder(ListFilter.identity());

        Stats stats1 = holder.getStatsHolder(type1).getStats();
        assertEquals(10.0,
                stats1.getMeasure("one").in(IntervalUnit.NANOSECONDS).getMean(),
                0.1);
        assertEquals(20.0,
                stats1.getMeasure("two").in(IntervalUnit.NANOSECONDS).getMean(),
                0.1);

        Stats stats2 = holder.getStatsHolder(type2).getStats();
        assertEquals(30.0,
                stats2.getMeasure("one").in(IntervalUnit.NANOSECONDS).getMean(),
                0.1);
        assertEquals(40.0,
                stats2.getMeasure("two").in(IntervalUnit.NANOSECONDS).getMean(),
                0.1);
    }
}
