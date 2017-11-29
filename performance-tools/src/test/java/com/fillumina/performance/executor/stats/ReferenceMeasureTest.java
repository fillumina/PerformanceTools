package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReferenceMeasureTest {

    @Test
    public void shouldGetReferenceTestMeasure() {
        Map<TName,DimensionalMeasure> map = new LinkedHashMap<>();
        addMeasure(map, "one", 1);
        addMeasure(map, "two", 7);
        addMeasure(map, "three", 2.3);
        addMeasure(map, "four", 2);

        BiggerMeasure ref = new BiggerMeasure(map);
        assertEquals(1, ref.getIndex());
        assertEquals(7.0, ref.getMeasure().getMean(), 0);
        assertEquals(TN.tname("two"), ref.getName());
    }

    private void addMeasure(Map<TName,DimensionalMeasure> map,
            String name, double value) {
        map.put(TN.tname(name),
                new DimensionalOnlineMeasure(IntervalUnit.MILLISECONDS, value));
    }
}
