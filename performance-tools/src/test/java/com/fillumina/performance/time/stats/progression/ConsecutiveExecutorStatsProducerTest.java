package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedTree;
import java.util.Collection;
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
        StatsProducerMock producer = new StatsProducerMock() {
            private static final long serialVersionUID = 1L;

            @Override
            protected TimeStats createStats() {
                SpeedStatsMock.Builder builder = SpeedStatsMock.builder();
                for (TName name : getTests().keySet()) {
                    builder.addTest(name.toString())
                            .samples(100)
                            .stdev(5.0)
                            .timeNs(100)
                            .endTest();
                }
                return builder.buildWithCoincidentalValues(
                        TimeSampleCollector::createAverageTimeCollector);
            }

        };
        ConsecutiveExecutorStatsProducer consecutiveProducer =
                new ConsecutiveExecutorStatsProducer(true);
        consecutiveProducer.instrument(producer);

        consecutiveProducer.addTest("first", new InnerRunnable());
        consecutiveProducer.addTest("second", new InnerRunnable());
        consecutiveProducer.addTest("third", new InnerRunnable());


        AverageTimeStats stats = consecutiveProducer.execute()
                .getStats(AverageTimeStats.class).getAssertable();

        final TName first = TN.tname("first");
        final TName second = TN.tname("second");
        final TName third = TN.tname("third");

        final Collection<TName> names = stats.getNames();
        assertTrue(names.contains(first));
        assertTrue(names.contains(second));
        assertTrue(names.contains(third));

        // mock returns a branch for each call of "get"
        LinkedTree<TName,Runnable> tree = producer.getExecutedTests();
        assertEquals(first.append("first"),
                tree.getTree(first).getTreeAtIndex(0).getKey());
        assertEquals(second.append("second"),
                tree.getTree(second).getTreeAtIndex(0).getKey());
        assertEquals(third.append("third"),
                tree.getTree(third).getTreeAtIndex(0).getKey());
    }

}
