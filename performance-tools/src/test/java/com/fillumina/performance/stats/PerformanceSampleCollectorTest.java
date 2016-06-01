package com.fillumina.performance.stats;

import com.fillumina.performance.FakePerformanceCreator;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceSampleCollectorTest {

    @Test
    public void shouldAddSamplesAndGetStastitics() {
        PerformanceSampleCollector collector = new PerformanceSampleCollector();
        for (int i=0; i<100; i++) {
            collector.add(FakePerformanceCreator
                    .createSample(1_000,  new Object[][]{
                                {"one", 950 + i},
                                {"two", 1950 + i}} ));
        }
        PerformanceStats stats = collector.createPerformanceStats(null, false);
        final Map<String, TestPerformance> tp = stats.getTestPerformances();
        assertEquals(2, tp.size());
        assertEquals(1.0,
                tp.get("one").getElapsedNanosecondsPerCycle().getMean(), 1E-3);
        assertEquals(2.0,
                tp.get("two").getElapsedNanosecondsPerCycle().getMean(), 1E-3);
    }

}
