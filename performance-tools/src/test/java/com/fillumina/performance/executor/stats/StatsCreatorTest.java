package com.fillumina.performance.executor.stats;

import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.SampleCreator;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.OutlierEliminatorFilter;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreatorTest {

    @Test
    public void shouldCreateStatsFromNoSamples() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);

        Stats stats = creator.createStats();

        assertTrue(stats.isEmpty());
        assertEquals(MockStatsType.INSTANCE, stats.getStatsType());
    }

    @Test
    public void shouldCreateStats() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);

        creator.addSample(SampleCreator.createSample("one", 10.0, "two", 20.0));
        creator.addSample(SampleCreator.createSample("one", 10.1, "two", 19.8));
        creator.addSample(SampleCreator.createSample("one", 9.88, "two", 20.2));
        creator.addSample(SampleCreator.createSample("one", 9.79, "two", 19.9));
        creator.addSample(SampleCreator.createSample("one", 10.2, "two", 20.1));

        Stats stats = creator.createStats();

        assertEquals(MockStatsType.INSTANCE, stats.getStatsType());

        assertEquals(10.0,
                stats.getMeasure("one").in(Magnitude.UNIT).getMean(),
                1E-2);
        assertEquals(20.0,
                stats.getMeasure("two").in(Magnitude.UNIT).getMean(),
                1E-2);

        assertEquals(5, stats.getMeasure("one").getCount());
    }


    @Test
    public void shouldDiscardTheWholeRoundWhenOnlyOneVariantIsAnOutlier() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);
        for (int i = 0; i < 40; i++) {
            creator.addSample(SampleCreator.createSample("one", i == 25 ? 1000 : 1,
                    "two", 10));
        }
        Stats stats = creator.createStats(OutlierEliminatorFilter.INSTANCE);
        assertEquals(39, stats.getMeasure("one").getCount());
        assertEquals("the other variant must lose the same round", 39,
                stats.getMeasure("two").getCount());
        assertEquals(10, stats.getMeasure("two").getMean(), 0);
    }

    @Test
    public void shouldRemoveSharedDisturbancesAsOneRound() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);
        for (int i = 0; i < 40; i++) {
            creator.addSample(SampleCreator.createSample("one", i == 20 ? 100 : 1,
                    "two", i == 20 ? 200 : 2));
        }
        Stats stats = creator.createStats(OutlierEliminatorFilter.INSTANCE);
        assertEquals(39, stats.getMeasure("one").getCount());
        assertEquals(39, stats.getMeasure("two").getCount());
        assertEquals(2, stats.getMeasure("two").getMean(), 0);
    }

    @Test
    public void shouldRetainObservationsFromSparseRounds() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);
        creator.addSample(SampleCreator.createSample("one", 1));
        creator.addSample(SampleCreator.createSample("two", 2));
        Stats stats = creator.createStats(OutlierEliminatorFilter.INSTANCE);
        assertEquals(1, stats.getMeasure("one").getCount());
        assertEquals(1, stats.getMeasure("two").getCount());
    }

    @Test
    public void shouldCreateFilteredStats() {
        StatsCreator creator = new StatsCreator(MockStatsType.INSTANCE);

        creator.addSample(SampleCreator.createSample("one", 1, "two", 10));
        creator.addSample(SampleCreator.createSample("one", -1, "two", -10));

        // filters negative values out
        ListFilter<Double> accumulatorFilter = new ListFilter<Double>() {
            @Override
            public <T> List<T> filter(List<T> list,
                    Function<T, Double> extractor) {
                List<T> result = new ArrayList<>();
                for (T t : list) {
                    if (extractor.apply(t) > 0) {
                        result.add(t);
                    }
                }
                return result;
            }

        };

        Stats stats = creator.createStats(accumulatorFilter);

        assertEquals(MockStatsType.INSTANCE, stats.getStatsType());

        assertEquals(1.0,
                stats.getMeasure("one").in(Magnitude.UNIT).getMean(),
                1E-3);
        assertEquals(10.0,
                stats.getMeasure("two").in(Magnitude.UNIT).getMean(),
                1E-3);

        assertEquals(1, stats.getMeasure("one").getCount());
        assertEquals(1, stats.getMeasure("two").getCount());
    }
}
