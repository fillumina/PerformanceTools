package com.fillumina.performance.infrastructure.type;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiStatsTest {

    private static class HeightStats implements AssertableMultiStats, Assertable {

        @Override
        public Measure getValue(String testName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MeasureRatio getRatioWithSlowestTest(String testName) {
            throw new UnsupportedOperationException();
        }
    }

    private static class StatsConsumer<A extends AssertableStats & Assertable>
            implements PerformanceConsumer<A> {

        @Override
        public void consume(PHolder<A> performances) {
        }
    }

    private AssertableStats getStats() {
        return new HeightStats();
    }

    private AssertableParameterizedStats getParameterizedStats() {
        return new HeightStats();
    }

    private class ParamPerformanceHolder
            extends PHolder<AssertableStats> {

        public ParamPerformanceHolder(AssertableStats stats) {
            super(stats);
        }

    }

    @Test
    public void testSomeMethod() {
        final HeightStats hs = new HeightStats();
        new StatsConsumer<>().consume(
                new ParamPerformanceHolder(getStats()));

//        new StatsConsumer<>().consume(
//                new PHolder<AssertableParameterizedStats>(
//                        getParameterizedStats()));
    }

}
