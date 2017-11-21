package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.SampleCreator;
import com.fillumina.performance.mock.MockStatsType;
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
        StatsBuilder statsBuilder = createStatsBuilder("one", 1, 2, 3);

        Stats stats = statsBuilder.createStats(ListFilter.identity());

        assertEquals(2, stats.getMeasure("one").getMean(), 0);
    }

    @Test
    public void shouldCreateSingleStatsFilteringData() {
        StatsBuilder statsBuilder = createStatsBuilder("one", 1, 4, 4, 4);

        Stats stats = statsBuilder.createStats(MostUsedFilter.instance());

        assertEquals(4, stats.getMeasure("one").getMean(), 0);
    }

    private StatsBuilder createStatsBuilder(
            String name, double... values) {
        StatsBuilder statsBuilder = new StatsBuilder(MockStatsType.INSTANCE);
        for (double v : values) {
            statsBuilder.addSample(SampleCreator.createSample(name, v));
        }
        return statsBuilder;
    }
}
