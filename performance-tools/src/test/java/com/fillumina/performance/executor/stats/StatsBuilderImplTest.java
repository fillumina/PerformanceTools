package com.fillumina.performance.executor.stats;

import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleCreator;
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
        StatsCreator statsBuilder = createStatsBuilder("one", 1, 2, 3);

        Stats stats = statsBuilder.createStats(ListFilter.identity());

        assertEquals(2, stats.getMeasure("one").getMean(), 0);
    }

    @Test
    public void shouldCreateSingleStatsFilteringData() {
        StatsCreator statsBuilder = createStatsBuilder("one", 1, 4, 4, 4);

        Stats stats = statsBuilder.createStats(MostUsedFilter.instance());

        assertEquals(4, stats.getMeasure("one").getMean(), 0);
    }

    private StatsCreator createStatsBuilder(String name, double... values) {
        StatsCreator statsBuilder = new StatsCreator(MockStatsType.INSTANCE);
        for (double v : values) {
            statsBuilder.addSample(SampleCreator.createSample(name, v));
        }
        return statsBuilder;
    }
}
