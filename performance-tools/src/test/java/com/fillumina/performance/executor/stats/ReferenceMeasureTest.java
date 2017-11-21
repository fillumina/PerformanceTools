package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReferenceMeasureTest {

    @Test
    public void shouldGetReferenceTestMeasure() {
        List<SingleStats> list = Arrays.asList(
                createSingleStats("one", 1),
                createSingleStats("two", 7),
                createSingleStats("three", 2.3),
                createSingleStats("four", 2)
        );

        ReferenceMeasure ref = new ReferenceMeasure(list);
        assertEquals(1, ref.getReferenceTestIndex());
        assertEquals(7.0, ref.getReferenceTestMeasure().getMean(), 0);
        assertEquals(TN.tname("two"), ref.getReferenceTestName());
    }

    private SingleStats createSingleStats(String name, double value) {
        return new SingleStats(TN.tname(name),
                new DimensionalOnlineMeasure(IntervalUnit.MILLISECONDS, value),
                0,0,0);
    }
}
