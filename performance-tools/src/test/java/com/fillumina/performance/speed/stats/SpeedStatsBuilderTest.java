package com.fillumina.performance.speed.stats;

import com.fillumina.performance.speed.stats.SpeedStatsBuilder;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedStatsBuilderTest {


    @Test
    public void shouldExtractMeasureArray() {
        Measure[] measureArray = new Measure[10];
        for (int i=0; i<measureArray.length; i++) {
            measureArray[i] = new OnlineMeasure(1.0/(i + 1));
        }
        List<TestPerformance> list = new ArrayList<>();
        for (int i=0; i<10; i++) {
            list.add(new TestPerformance(null, measureArray[i], 1, 1, 1));
        }

        Measure[] extracted = SpeedStatsBuilder.extractMeasureArray(list);

        assertArrayEquals(measureArray, extracted);
    }

    @Test
    public void shouldGetTheSlowerIndex() {
        List<TestPerformance> list = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Measure mean = new OnlineMeasure(1.0/(i + 1));
            list.add(new TestPerformance(null, mean, 1, 1, 1));
        }
        int slowerIdx = SpeedStatsBuilder.getSlowerIndex(list);
        Measure slower = list.get(slowerIdx).getElapsedNanosecondsPerCycle();
        assertEquals(1.0, slower.getMean(), 0);
    }

}
