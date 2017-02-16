package com.fillumina.performance;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.stats.MultipleMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
// TODO remove this test
public class PerformanceConsumerExtensionTest {

    private static class SpeedParametrizedStats extends SpeedStats {
        private static final long serialVersionUID = 1L;
        public SpeedParametrizedStats(OnlineMeasure global,
                MultipleMeasure multimeasure,
                Map<String, TestPerformance> testPerformance) {
            super(global, multimeasure, testPerformance);
        }
    }

    private static class ParametrizedConsumer
            implements PerformanceConsumer<SpeedParametrizedStats> {

        @Override
        public void consume(
                PHolder<SpeedParametrizedStats> performances) {
            // do nothing
        }

    }

    private static class StatsConsumer
            implements PerformanceConsumer<SpeedStats> {

        @Override
        public void consume(
                PHolder<SpeedStats> performances) {
            // do nothing
        }

    }

    @Test
    public void shouldWork() {
        final PHolder<SpeedParametrizedStats> paramStats =
                new PHolder<>(null);

        new ParametrizedConsumer().consume(paramStats);

        // it doesn't accept wrong leaf!
//        new StatsConsumer().consume(paramStats);
    }

}
