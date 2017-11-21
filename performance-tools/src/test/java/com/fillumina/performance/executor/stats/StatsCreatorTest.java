package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleCreator;
import com.fillumina.performance.util.filter.ListFilter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreatorTest {

    @Test
    public void shouldCreateMixedStats() {
        StatsCreator statsCreator = new StatsCreator(TN.tname("first"));

        statsCreator.addSample(createSample(1, 10));
        statsCreator.addSample(createSample(2, 20));
        statsCreator.addSample(createSample(3, 30));
        statsCreator.addSample(createSample(4, 40));
        statsCreator.addSample(createSample(5, 50));

        MixedStatsHolder holder =
                statsCreator.getMixedAssertableHolder(ListFilter.identity());

        Stats assertable = holder.getHolder(MockStatsType.INSTANCE).getStats();

        assertEquals(3.0, assertable.getMeasure("one").getMean(), 0);
        assertEquals(30.0, assertable.getMeasure("two").getMean(), 0);
    }

    private Sample createSample(double a, double b) {
        return new Sample(MockStatsType.INSTANCE,
                SampleCreator.createMap("one", a, "two", b));
    }
}
