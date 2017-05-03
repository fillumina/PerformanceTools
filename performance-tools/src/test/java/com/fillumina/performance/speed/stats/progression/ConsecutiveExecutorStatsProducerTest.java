package com.fillumina.performance.speed.stats.progression;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedTree;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsecutiveExecutorStatsProducerTest {

    private static class InnerRunnable implements Runnable {
        @Override
        public void run() {
            // do nothing
        }
    }

    @Test
    public void shouldExecuteTestsConsecutively() {
        StatsProducerMock<SpeedStats> producer =
                new StatsProducerMock<SpeedStats>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected SpeedStats createStats() {
                SpeedStatsMock.Builder builder = SpeedStatsMock.builder();
                for (TName name : getTests().keySet()) {
                    builder.addTest(name.toString())
                            .samples(100)
                            .stdev(5.0)
                            .timeNs(100)
                            .endTest();
                }
                return builder.buildWithCoincidentalValues();
            }

        };
        ConsecutiveExecutorStatsProducer consecutiveProducer =
                new ConsecutiveExecutorStatsProducer(true);
        consecutiveProducer.instrument(producer);

        consecutiveProducer.addTest("first", new InnerRunnable());
        consecutiveProducer.addTest("second", new InnerRunnable());
        consecutiveProducer.addTest("third", new InnerRunnable());

        SpeedStats stats = consecutiveProducer.execute().getStats();
        assertTrue(stats.getTestNames().contains(TN.n("first")));
        assertTrue(stats.getTestNames().contains(TN.n("second")));
        assertTrue(stats.getTestNames().contains(TN.n("third")));

        // mock returns a branch for each call of "execute"
        LinkedTree<String,Runnable> tree = producer.getExecutedTests();
        assertEquals("first", tree.getTree("first").getTreeAtIndex(0).getKey());
        assertEquals("second", tree.getTree("second").getTreeAtIndex(0).getKey());
        assertEquals("third", tree.getTree("third").getTreeAtIndex(0).getKey());
    }

}
