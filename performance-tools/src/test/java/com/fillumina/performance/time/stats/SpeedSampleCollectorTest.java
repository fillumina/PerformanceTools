package com.fillumina.performance.time.stats;

import com.fillumina.performance.time.stats.TimeStats;
import com.fillumina.performance.time.stats.SpeedSampleCollector;
import com.fillumina.performance.time.stats.SingleSpeedStats;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.IterationTime;
import com.fillumina.performance.time.sample.SpeedSample;
import com.fillumina.performance.util.TName;
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
            collector.add(SpeedSampleMock
                    .builder()
                        .addTest("one").nansecondsPerOp(950 + i).endTest()
                        .addTest("two").nansecondsPerOp(1950 + i).endTest()
                    .createSample());
        }

        TimeStats stats = collector.createPerformanceStatsAndFilterIf(false);

        final Map<TName, SingleSpeedStats> tp = stats.getSingleStatsMap();

        assertEquals(2, tp.size());
        assertEquals(1000,
                tp.get(TN.tname("one")).getElapsedNanosecondsPerCycle().getMean(),
                10);
        assertEquals(2000,
                tp.get(TN.tname("two")).getElapsedNanosecondsPerCycle().getMean(),
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
            collector.add(SpeedSampleMock
                    .builder()
                        .addTest("one").nansecondsPerOp(i).endTest()
                        .addTest("two").nansecondsPerOp(1000 + i).endTest()
                    .createSample());
        }

        collector.createPerformanceStatsAndFilterIf(true);

        assertTrue(filtered.get());
    }

    @Test
    public void shouldAddSamplesConsecutively() {
        SpeedSampleCollector collector = new SpeedSampleCollector();

        addSample(collector, "first", 100, 100);
        addSample(collector, "first", 150, 150);
        addSample(collector, "first", 120, 120);

        addSample(collector, "second", 200, 400);
        addSample(collector, "second", 250, 500);
        addSample(collector, "second", 220, 440);
        addSample(collector, "second", 100, 200);

        TimeStats stats = collector.createPerformanceStatsAndFilterIf(false);

        assertEquals(3, stats.getMeasure("first").getCount());
        assertEquals(4, stats.getMeasure("second").getCount());
    }

    private void addSample(SpeedSampleCollector collector,
            String testName,
            int iterations,
            int timePerOp) {
        SpeedSample first1 = SpeedSampleMock.builder()
                .addTest(testName)
                .iterations(iterations)
                .nansecondsPerOp(timePerOp)
                .endTest()
                .createSample();
        collector.add(first1);
    }
}
