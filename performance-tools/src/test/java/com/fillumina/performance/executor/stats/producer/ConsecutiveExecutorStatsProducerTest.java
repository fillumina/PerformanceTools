package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.stats.AbstractStatsProducer;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.collection.LinkedMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsecutiveExecutorStatsProducerTest {

    public class StatsProducerImpl
            extends AbstractStatsProducer<StatsProducerImpl, StatsMock> {

        private final Map<String,Double> map;

        public StatsProducerImpl(Map<String, Double> map) {
            this.map = map;
        }

        @Override
        public MixedAssertableHolder get() {
            StatsMockBuilder builder = StatsMock.builder();

            // assert only one test is executed at each execution
            assertEquals(1, getTests().size());

            getTests().forEach( (name, test) -> {
                final String n = name.toString();
                builder.addTest(n)
                        .mean(map.get(n))
                        .samples(33)
                        .stdev(0)
                        .endTest();
            });
            return builder.buildWithCoincidentalValues();
        }
    }

    @Test
    public void shouldExecuteTestsCojnsecutively() {
        StatsProducerImpl statsProducer = new StatsProducerImpl(
                LinkedMap.<String,Double>builder()
                    .put("one", 1.0)
                    .put("two", 2.0)
                    .put("three", 3.0)
                .build());

        ConsecutiveExecutorStatsProducer consecutiveExecutor =
                new ConsecutiveExecutorStatsProducer(true);

        consecutiveExecutor.instrument(statsProducer);

        consecutiveExecutor
                .addTest("one", () -> {})
                .addTest("two", () -> {})
                .addTest("three", () -> {});

        MixedAssertableHolder mixedHolder = consecutiveExecutor.execute();

        StatsMock stats = mixedHolder.getStats(StatsMock.class).getAssertable();

        assertEquals(1.0, stats.getMeasure("one").getMean(), 0);
        assertEquals(2.0, stats.getMeasure("two").getMean(), 0);
        assertEquals(3.0, stats.getMeasure("three").getMean(), 0);
    }
}
