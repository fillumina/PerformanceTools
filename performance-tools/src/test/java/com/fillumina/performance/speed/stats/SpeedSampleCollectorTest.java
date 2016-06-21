package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.speed.stats.SpeedSampleCollector;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.FakePerformanceCreator;
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
        SpeedSampleCollector collector =
                new SpeedSampleCollector(0.95, null);
        for (int i=0; i<100; i++) {
            collector.add(FakePerformanceCreator
                    .createSample(1_000,  new Object[][]{
                                {"one", 950 + i},
                                {"two", 1950 + i}} ));
        }
        SpeedStats stats = collector.createPerformanceStats(false);
        final Map<String, TestPerformance> tp = stats.getPerformances();
        assertEquals(2, tp.size());
        assertEquals(1.0,
                tp.get("one").getElapsedNanosecondsPerCycle().getMean(), 1E-3);
        assertEquals(2.0,
                tp.get("two").getElapsedNanosecondsPerCycle().getMean(), 1E-3);
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

        SpeedSampleCollector collector =
                new SpeedSampleCollector(0.95, filter);
        for (int i=0; i<100; i++) {
            collector.add(FakePerformanceCreator
                    .createSample(1_000,  new Object[][]{
                                {"one", i},
                                {"two", 1000 + i}} ));
        }

        collector.createPerformanceStats(true);

        assertTrue(filtered.get());
    }
}
