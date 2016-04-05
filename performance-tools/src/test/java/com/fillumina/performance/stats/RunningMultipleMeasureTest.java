package com.fillumina.performance.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RunningMultipleMeasureTest {

    /**
     * @see <a href='http://www.excel-easy.com/examples/anova.html'>
     *  Excel Easy: Anova</a>
     */
    @Test
    public void shouldValidateTest() {
        RunningMultipleMeasure rmm = new RunningMultipleMeasure(3);
        rmm.add(42.0, 69.0, 35.0);
        rmm.add(53.0, 54.0, 40.0);
        rmm.add(49.0, 58.0, 53.0);
        rmm.add(53.0, 64.0, 42.0);
        rmm.add(43.0, 64.0, 50.0);
        rmm.add(44.0, 55.0, 39.0);
        rmm.add(45.0, 56.0, 55.0);
        rmm.add(52.0, null, 39.0);
        rmm.add(54.0, null, 40.0);

        assertMeasures(rmm.getMeasures().get(0),
                rmm.getMeasures().get(1),
                rmm.getMeasures().get(2));

        assertAnova(rmm.getMultipleMeasure());
    }


    /**
     * @see <a href='http://www.excel-easy.com/examples/anova.html'>
     *  Excel Easy: Anova</a>
     */
    @Test
    public void shouldValidateAnova() {
        Measure economics = new Measure(42, 53, 49, 53, 43, 44, 45, 52, 54);
        Measure medicine = new Measure( 69, 54, 58, 64, 64, 55, 56);
        Measure history = new Measure(  35, 40, 53, 42, 50, 39, 55, 39, 40);

        Measure global = new Measure(
                42, 53, 49, 53, 43, 44, 45, 52, 54,
                69, 54, 58, 64, 64, 55, 56,
                35, 40, 53, 42, 50, 39, 55, 39, 40
        );

        assertMeasures(economics, medicine, history);

        assertAnova(new MultipleMeasure(global, economics, medicine, history));
    }

    private void assertMeasures(Measure economics, Measure medicine,
            Measure history) {
        assertEquals(9, economics.count());
        assertEquals(7, medicine.count());
        assertEquals(9, history.count());

        assertEquals(435, economics.sum(), 1E-4);
        assertEquals(420, medicine.sum(), 1E-4);
        assertEquals(393, history.sum(), 1E-4);

        assertEquals(48.3334, economics.mean(), 1E-4);
        assertEquals(60, medicine.mean(), 1E-4);
        assertEquals(43.6667, history.mean(), 1E-4);

        assertEquals(23.5, economics.unbiasedVariance(), 1E-4);
        assertEquals(32.3334, medicine.unbiasedVariance(), 1E-4);
        assertEquals(50.5, history.unbiasedVariance(), 1E-4);
    }

    private void assertAnova(MultipleMeasure mm) {
        assertEquals(35.72727, mm.getAnovaMeanSquareWithin(), 1E-2);
        assertEquals(542.92, mm.getAnovaMeanSquareBetween(), 1E-2);
        assertEquals(15.19623, mm.getAnovaF(), 1E-4);
        assertEquals(7.16E-5, mm.anovaPValue(), 1E-4);
        assertTrue(mm.isStatisticallyRelevantWithConfidence(0.99));
    }
}
