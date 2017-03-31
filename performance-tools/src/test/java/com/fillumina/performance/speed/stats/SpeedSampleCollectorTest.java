package com.fillumina.performance.speed.stats;

import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.speed.sample.IterationTime;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.filter.ValueExtractor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedSampleCollectorTest {

    @Test
    public void shouldAddSamplesAndGetStastitics() {
        SpeedSampleCollector collector = new SpeedSampleCollector(null);
        for (int i=0; i<100; i++) {
            collector.add(MockPerformanceCreator
                    .speedSampleBuilder()
                        .addTest("one").timePerOp(950 + i).endTest()
                        .addTest("two").timePerOp(1950 + i).endTest()
                    .createSample());
        }

        SpeedStats stats = collector.createPerformanceStatsAndFilterIf(false);

        final Map<String, TestStats> tp = stats.getPerformanceMap();

        assertEquals(2, tp.size());
        assertEquals(1000,
                tp.get("one").getElapsedNanosecondsPerCycle().getMean(),
                10);
        assertEquals(2000,
                tp.get("two").getElapsedNanosecondsPerCycle().getMean(),
                20);
    }

    @Test
    public void shouldUseListFilter() {
        final AtomicBoolean filtered = new AtomicBoolean(false);
        ListFilter<IterationTime,Double> filter =
                new ListFilter<IterationTime,Double>() {
            @Override
            public List<IterationTime> filter(List<IterationTime> list,
                    ValueExtractor<IterationTime, Double> extractor) {
                filtered.set(true);
                return list;
            }
        };

        SpeedSampleCollector collector = new SpeedSampleCollector(filter);
        for (int i=0; i<100; i++) {
            collector.add(MockPerformanceCreator
                    .speedSampleBuilder()
                        .addTest("one").timePerOp(i).endTest()
                        .addTest("two").timePerOp(1000 + i).endTest()
                    .createSample());
        }

        collector.createPerformanceStatsAndFilterIf(true);

        assertTrue(filtered.get());
    }
}
