package com.fillumina.performance.executor.sample;

import com.fillumina.performance.mock.SampleCreator;
import com.fillumina.performance.executor.stats.SingleStats;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.MostUsedFilter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsBuilderImplTest {

    @Test
    public void shouldCreateSingleStats() {
        StatsBuilder<Stats<SingleStats>, Sample> statsBuilder =
                createStatsBuilder("one", 1, 2, 3);

        Stats<SingleStats> stats = statsBuilder.createStats(ListFilter.identity());

        assertEquals(2, stats.getMeasure("one").getMean(), 0);
    }

    @Test
    public void shouldCreateSingleStatsFilteringData() {
        StatsBuilder<Stats<SingleStats>, Sample> statsBuilder =
                createStatsBuilder("one", 1, 4, 4, 4);

        Stats<SingleStats> stats = statsBuilder.createStats(
                MostUsedFilter.instance());

        assertEquals(4, stats.getMeasure("one").getMean(), 0);
    }

    private StatsBuilder<Stats<SingleStats>, Sample> createStatsBuilder(
            String name, double... values) {
        StatsBuilder<Stats<SingleStats>, Sample> statsBuilder =
                SampleCreator.createSample("bla", 1.0).getStatsBuilder();
        for (double v : values) {
            statsBuilder.addSample(SampleCreator.createSample(name, v));
        }
        return statsBuilder;
    }
}
