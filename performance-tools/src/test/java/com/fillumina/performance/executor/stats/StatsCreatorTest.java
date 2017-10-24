package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.mock.AbstractSampleMock;
import com.fillumina.performance.mock.SampleCreator;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.MultiMeasureSignificance;
import com.fillumina.performance.util.tname.TNameMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsCreatorTest {

    @Test
    public void shouldCreateMixedStats() {
        StatsCreator<Stats<SingleStats>,Sample> statsCreator =
                new StatsCreator<>(TN.tname("first"));

        statsCreator.addSample(createSample(1, 10));
        statsCreator.addSample(createSample(2, 20));
        statsCreator.addSample(createSample(3, 30));
        statsCreator.addSample(createSample(4, 40));
        statsCreator.addSample(createSample(5, 50));

        MixedAssertableHolder holder =
                statsCreator.getMixedAssertableHolder(ListFilter.identity());

        Stats<?> assertable = holder.getStats(Stats.class).getAssertable();

        assertEquals(3.0, assertable.getMeasure("one").getMean(), 0);
        assertEquals(30.0, assertable.getMeasure("two").getMean(), 0);
    }

    private static class AStats extends StatsMock {
        private static final long serialVersionUID = 1L;
        public AStats(MultiMeasureSignificance multiMeasure,
                TNameMap<SingleStats> singleStatsMap) {
            super(multiMeasure, singleStatsMap);
        }
    }

    private static class BStats extends StatsMock {
        private static final long serialVersionUID = 1L;
        public BStats(MultiMeasureSignificance multiMeasure,
                TNameMap<SingleStats> singleStatsMap) {
            super(multiMeasure, singleStatsMap);
        }
    }

    private static class ASample extends AbstractSampleMock<StatsMock> {
        private static final long serialVersionUID = 1L;
        public ASample(TNameMap<SampleValue> map) {
            super(map);
        }

        @Override
        protected AStats createStats(MultiMeasureSignificance s,
                TNameMap<SingleStats> map) {
            return new AStats(s, map);
        }
    }

    private static class BSample extends AbstractSampleMock<StatsMock> {
        private static final long serialVersionUID = 1L;
        public BSample(TNameMap<SampleValue> map) {
            super(map);
        }

        @Override
        protected BStats createStats(MultiMeasureSignificance s,
                TNameMap<SingleStats> map) {
            return new BStats(s, map);
        }
    }

    @Test
    public void shouldCreateMixedStatsWithTwoStats() {
        StatsCreator<StatsMock,AbstractSampleMock<StatsMock>> statsCreator =
                new StatsCreator<>(TN.tname("first"));

        statsCreator.addSample(createASample(1, 10));
        statsCreator.addSample(createASample(2, 20));
        statsCreator.addSample(createASample(3, 30));
        statsCreator.addSample(createASample(4, 40));
        statsCreator.addSample(createASample(5, 50));

        statsCreator.addSample(createBSample(100, 10_000));
        statsCreator.addSample(createBSample(200, 20_000));
        statsCreator.addSample(createBSample(300, 30_000));
        statsCreator.addSample(createBSample(400, 40_000));
        statsCreator.addSample(createBSample(500, 50_000));

        MixedAssertableHolder holder =
                statsCreator.getMixedAssertableHolder(ListFilter.identity());

        Stats<?> aStats = holder.getStats(AStats.class).getAssertable();
        assertEquals(3.0, aStats.getMeasure("one").getMean(), 0);
        assertEquals(30.0, aStats.getMeasure("two").getMean(), 0);

        Stats<?> bStats = holder.getStats(BStats.class).getAssertable();
        assertEquals(300.0, bStats.getMeasure("one").getMean(), 0);
        assertEquals(30_000.0, bStats.getMeasure("two").getMean(), 0);
    }

    private Sample createSample(double a, double b) {
        return new Sample(SampleCreator.createMap("one", a, "two", b));
    }

    private ASample createASample(double a, double b) {
        return new ASample(SampleCreator.createMap("one", a, "two", b));
    }

    private BSample createBSample(double a, double b) {
        return new BSample(SampleCreator.createMap("one", a, "two", b));
    }
}
