package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.stats.Significance;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CollectedMeasuresTest {

    @Test
    public void shouldCalculateTheMeasureOfSingleTest() {
        TNameMap<SampleValueAccumulator> accumulators = new TNameMap<>();
        accumulators.add(createAccumulator("one", 1, 2, 3, 4, 5));

        CollectedMeasures<SampleValueAccumulator> collector =
                new CollectedMeasures<>(
                        accumulators,
                        IntervalUnit.MILLISECONDS,
                        ListFilter.<Double>identity());

        assertEquals(3,
                collector.getAccumulators().get("one").getMeasure().getMean(),
                0);
    }

    @Test
    public void shouldCalculateTheMeasureOfTwoTest() {
        TNameMap<SampleValueAccumulator> accumulators = new TNameMap<>();
        accumulators.add(createAccumulator("one", 1, 2, 3, 4, 5));
        accumulators.add(createAccumulator("two", 5, 6, 7));

        CollectedMeasures<SampleValueAccumulator> collector =
                new CollectedMeasures<>(
                        accumulators,
                        IntervalUnit.MILLISECONDS,
                        ListFilter.<Double>identity());

        assertEquals(3,
                collector.getAccumulators().get("one").getMeasure().getMean(),
                0);
        assertEquals(6,
                collector.getAccumulators().get("two").getMeasure().getMean(),
                0);
    }

    @Test
    public void shouldGetMeasures() {
        TNameMap<SampleValueAccumulator> accumulators = new TNameMap<>();
        accumulators.add(createAccumulator("one", 1, 2, 3, 4, 5));
        accumulators.add(createAccumulator("two", 5, 6, 7));

        CollectedMeasures<SampleValueAccumulator> collector =
                new CollectedMeasures<>(
                        accumulators,
                        IntervalUnit.MILLISECONDS,
                        ListFilter.<Double>identity());

        Map<TName, DimensionalMeasure> map = collector.getMeasures();

        assertEquals(3, map.get(TN.tname("one")).getMean(), 0);
        assertEquals(6, map.get(TN.tname("two")).getMean(), 0);
    }

    @Test
    public void shouldCalculateGlobalMeasure() {
        TNameMap<SampleValueAccumulator> accumulators = new TNameMap<>();
        accumulators.add(createAccumulator("one", 1, 2, 3, 4, 5));
        accumulators.add(createAccumulator("two", 6, 7, 8, 9));

        CollectedMeasures<SampleValueAccumulator> collector =
                new CollectedMeasures<>(
                        accumulators,
                        IntervalUnit.MILLISECONDS,
                        ListFilter.<Double>identity());

        assertEquals(5, collector.getGlobalMeasure().getMean(), 0);
    }

    @Test
    public void shouldGetSignificance() {
        TNameMap<SampleValueAccumulator> accumulators = new TNameMap<>();
        accumulators.add(createAccumulator("one", 1, 2, 3, 4, 5));
        accumulators.add(createAccumulator("two", 6, 7, 8, 9));

        CollectedMeasures<SampleValueAccumulator> collector =
                new CollectedMeasures<>(
                        accumulators,
                        IntervalUnit.MILLISECONDS,
                        ListFilter.<Double>identity());

        Significance significance = collector.getSignificance();
        assertNotNull(significance);
    }

    private SampleValueAccumulator createAccumulator(String name,
            double... values) {
        SampleValueAccumulator acc = new SampleValueAccumulator();
        acc.setName(TN.tname(name));
        for (double v : values) {
            acc.addValue(v);
        }
        return acc;
    }

}
