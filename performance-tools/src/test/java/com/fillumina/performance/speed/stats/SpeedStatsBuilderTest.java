package com.fillumina.performance.speed.stats;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.SingleSpeedStatsMock;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MultiMeasure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedStatsBuilderTest {

    @Test
    public void shouldCreateMultiMeasure() {
        OnlineMeasure global = new OnlineMeasure();
        LinkedHashMap<TName,SingleSpeedStats> map = new LinkedHashMap<>();

        for (int i=0; i<5; i++) {
            int timeNs = 10 * (i + 1);
            DimensionalMeasure timeMeasure =
                    new DimensionalOnlineMeasure(IntervalUnit.NANOSECONDS, timeNs);
            String name = "test_" + i;
            SingleSpeedStats single = SingleSpeedStatsMock
                    .builder()
                    .name(name)
                    .originalSamples(100)
                    .samples(100)
                    .timeNs(timeMeasure)
                    .totalIterations(100)
                    .totalTime(timeNs * 100)
                    .build();

            global.add(timeNs);
            map.put(TN.n(name), single);
        }

        MultiMeasure mm = SpeedStatsBuilder.createMultiMeasure(global, map);

        assertEquals((1 + 2 + 3 + 4 + 5) * 10 / 5, mm.getGlobal().getMean(), .1);
        assertEquals(global, mm.getGlobal());
        assertEquals(5, mm.getMeasureCount());
    }

    @Test
    public void shouldExtractMeasureArray() {
        DimensionalMeasure[] measureArray = new DimensionalMeasure[10];
        for (int i=0; i<measureArray.length; i++) {
            measureArray[i] = new DimensionalOnlineMeasure(1.0/(i + 1));
        }

        List<SingleSpeedStats> list = new ArrayList<>();
        for (int i=0; i<10; i++) {
            list.add(new SingleSpeedStats(null, measureArray[i], 1, 1, 1, 1));
        }

        Measure[] extracted = SpeedStatsBuilder.extractMeasureArray(list);

        assertArrayEquals(measureArray, extracted);
    }

}
