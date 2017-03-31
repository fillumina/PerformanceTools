package com.fillumina.performance.speed.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
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
        DimensionalMeasure[] measureArray = new DimensionalMeasure[10];
        for (int i=0; i<measureArray.length; i++) {
            measureArray[i] = new DimensionalOnlineMeasure(1.0/(i + 1));
        }
        List<TestStats> list = new ArrayList<>();
        for (int i=0; i<10; i++) {
            list.add(new TestStats(null, measureArray[i], 1, 1, 1, 1));
        }

        Measure[] extracted = SpeedStatsBuilder.extractMeasureArray(list);

        assertArrayEquals(measureArray, extracted);
    }

}
