package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiMeasureTest {

    private static final double[] SOUTH = {
        7.56,
        6.57,
        5.71,
        5.42,
        5.38,
        4.96,
        4.89,
        4.83,
        4.32,
        4.11,
        3.92,
        3.78,
        3.58,
        3.27,
        2.95,
        2.23
    };

    private static final double[] OWEST = {
        6.92,
        6.9,
        6.73,
        6.68,
        6.54,
        6.53,
        6.52,
        5.82,
        5.31,
        5.2,
        4.33,
        4.01,
        3.86
    };

    private static final double[] NORTH = {
        5.56,
        5.42,
        5.1,
        4.72,
        4.71,
        3.87,
        3.67,
        3.23,
        2.99
    };

    private static final double[] EAST = {
        6.36,
        5.27,
        4.65,
        4.35,
        4.26,
        4.16,
        4.13,
        3.98,
        3.94,
        3.78,
        2.98,
        2.69
    };

    /**
     * @see <a href='http://www.dmi.units.it/corsi/biomed/anova/anova.html'>
     * Il test ANOVA (Italian)</a>
     */
    @Test
    public void shouldDoBiomedAnova() {
        OnlineMeasure north = new OnlineMeasure(NORTH);
        OnlineMeasure south = new OnlineMeasure(SOUTH);
        OnlineMeasure east = new OnlineMeasure(EAST);
        OnlineMeasure owest = new OnlineMeasure(OWEST);

        OnlineMeasure tot = new OnlineMeasure();
        tot.addAll(NORTH);
        tot.addAll(SOUTH);
        tot.addAll(EAST);
        tot.addAll(OWEST);

        MultiMeasure anova = new MultiMeasure(tot, north, south, east, owest);

        assertTrue(anova.isStatisticallyRelevantWithConfidence(0.95));
        assertEquals(6.47, anova.getAnovaMeanSquareBetween(), 1E-2);
        assertEquals(1.32, anova.getAnovaMeanSquareWithin(), 1E-2);
    }

    public static final double[] X1 = {
        7, 4, 6, 8, 6, 6, 2, 9
    };

    public static final double[] X2 = {
        5, 5, 3, 4, 4, 7, 2, 2
    };

    public static final double[] X3 = {
        2, 4, 7, 1, 2, 1, 5, 5
    };

    /**
     * @see <a href='https://web.mst.edu/~psyworld/anovaexample.htm'>
     *  ANOVA example</a>
     */
    @Test
    public void shouldDoSoundAnova() {
        OnlineMeasure x1 = new OnlineMeasure(X1);
        OnlineMeasure x2 = new OnlineMeasure(X2);
        OnlineMeasure x3 = new OnlineMeasure(X3);
        OnlineMeasure tot = new OnlineMeasure()
                .addAll(X1)
                .addAll(X2)
                .addAll(X3);

        MultiMeasure anova = new MultiMeasure(tot, x1, x2, x3);

        assertTrue(anova.isStatisticallyRelevantWithConfidence(0.95));
        assertEquals(15.04, anova.getAnovaMeanSquareBetween(), 1E-2);
        assertEquals(4.18, anova.getAnovaMeanSquareWithin(), 1E-2);
    }

    /*
        see http://statistica.mooo.com/OneWay_Anova_with_TukeyHSD
    */

    private static final OnlineMeasure A = new OnlineMeasure(
            0.28551035,
            0.338524035,
            0.088313218,
            0.205930807,
            0.363240102);

    @Test
    public void shouldCheckStatsA() {
        assertEquals(5, A.getCount(), 1E-3);
        assertEquals(1.2815, A.getSum(), 1E-3);
        assertEquals(0.2563, A.getMean(), 1E-3);
        assertEquals(0.0125, A.getUnbiasedVariance(), 1E-3);
        assertEquals(0.1116, A.getUnbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0499, A.getStandardError(), 1E-3);
    }

    private static final OnlineMeasure B = new OnlineMeasure(
            0.52173913,
            0.763358779,
            0.32546786,
            0.425305688,
            0.378071834);

    @Test
    public void shouldCheckStatsB() {
        assertEquals(5, B.getCount(), 1E-3);
        assertEquals(2.4139, B.getSum(), 1E-3);
        assertEquals(0.4828, B.getMean(), 1E-3);
        assertEquals(0.0298, B.getUnbiasedVariance(), 1E-3);
        assertEquals(0.1727, B.getUnbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0772, B.getStandardError(), 1E-3);
    }

    private static final OnlineMeasure C = new OnlineMeasure(
            0.989119683,
            1.192718142,
            0.788288288,
            0.549176236,
            0.544588155);

    @Test
    public void shouldCheckStatsC() {
        assertEquals(5, C.getCount(), 1E-3);
        assertEquals(4.0639, C.getSum(), 1E-3);
        assertEquals(0.8128, C.getMean(), 1E-3);
        assertEquals(0.0794, C.getUnbiasedVariance(), 1E-3);
        assertEquals(0.2817, C.getUnbiasedStandardDeviation(), 1E-3);
        assertEquals(0.1260, C.getStandardError(), 1E-3);
    }

    private static final OnlineMeasure D = new OnlineMeasure(
            1.26705653,
            1.625320787,
            1.266108976,
            1.154187629,
            1.268498943,
            1.069518717);

    @Test
    public void shouldCheckStatsD() {
        assertEquals(6, D.getCount(), 1E-3);
        assertEquals(7.6507, D.getSum(), 1E-3);
        assertEquals(1.2751, D.getMean(), 1E-3);
        assertEquals(0.0359, D.getUnbiasedVariance(), 1E-3);
        assertEquals(0.1896, D.getUnbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0774, D.getStandardError(), 1E-3);
    }

    private static final OnlineMeasure G = new OnlineMeasure(
            0.28551035,
            0.338524035,
            0.088313218,
            0.205930807,
            0.363240102,
            0.52173913,
            0.763358779,
            0.32546786,
            0.425305688,
            0.378071834,
            0.989119683,
            1.192718142,
            0.788288288,
            0.549176236,
            0.544588155,
            1.26705653,
            1.625320787,
            1.266108976,
            1.154187629,
            1.268498943,
            1.069518717);

    @Test
    public void shouldCheckStatsG() {
        assertEquals(21, G.getCount(), 1E-3);
        assertEquals(15.4100, G.getSum(), 1E-3);
        assertEquals(0.7338, G.getMean(), 1E-3);
        assertEquals(0.1955, G.getUnbiasedVariance(), 1E-3);
        assertEquals(0.4422, G.getUnbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0965, G.getStandardError(), 1E-3);
    }

    private static final MultiMeasure MM =
            new MultiMeasure(G, A, B, C, D);

    @Test
    public void shouldApplyAnovaToABCD() {
        assertEquals(27.5943, MM.getAnovaF(), 1E-3);
        assertEquals(9.2580E-7, 1.0 - MM.anovaPValue(), 1E-10);
    }

    @Test
    public void shouldGiveStudentizedRangeQ() {
        assertEquals(5.1403, Qsturng.qStudentRange(0.99, 4, 17), 1E-3);
        assertEquals(4.0204, Qsturng.qStudentRange(0.95, 4, 17), 1E-3);
    }

    @Test
    public void shouldMatchAB() {
        assertEquals(2.5582, MM.tukeyKramerHsdQStat(0, 1), 1E-3);
        assertEquals(0.3033978, 1.0 - MM.tukeyKramerHsdPValue(0, 1), 1E-3);
        assertFalse(MM.areSignificanltyDifferentAccordingToTukeyKramer(0.99, 0, 1));
    }

    @Test
    public void shouldMatchAC() {
        assertEquals(6.2854, MM.tukeyKramerHsdQStat(0, 2), 1E-3);
        assertEquals(0.0018321, 1.0 - MM.tukeyKramerHsdPValue(0, 2), 1E-3);
        assertTrue(MM.areSignificanltyDifferentAccordingToTukeyKramer(0.99, 0, 2));
    }

    @Test
    public void shouldMatchAD() {
        assertEquals(12.0193, MM.tukeyKramerHsdQStat(0, 3), 1E-3);
        assertEquals(0.0010053, 1.0 - MM.tukeyKramerHsdPValue(0, 3), 1E-3);
        assertTrue(MM.areSignificanltyDifferentAccordingToTukeyKramer(0.99, 0, 3));
    }

    @Test
    public void shouldMatchBC() {
        assertEquals(3.7273, MM.tukeyKramerHsdQStat(1, 2), 1E-3);
        assertEquals(0.0745409, 1.0 - MM.tukeyKramerHsdPValue(1, 2), 1E-3);
        assertFalse(MM.areSignificanltyDifferentAccordingToTukeyKramer(0.99, 1, 2));
    }

    @Test
    public void shouldMatchBD() {
        assertEquals(9.3473, MM.tukeyKramerHsdQStat(1, 3), 1E-3);
        assertEquals(0.0010053, 1.0 - MM.tukeyKramerHsdPValue(1, 3), 1E-3);
        assertTrue(MM.areSignificanltyDifferentAccordingToTukeyKramer(0.99, 1, 3));
    }

    @Test
    public void shouldMatchCD() {
        assertEquals(5.4544, MM.tukeyKramerHsdQStat(2, 3), 1E-3);
        assertEquals(0.0062832, 1.0 - MM.tukeyKramerHsdPValue(2, 3), 1E-3);
        assertTrue(MM.areSignificanltyDifferentAccordingToTukeyKramer(0.99, 2, 3));
    }

    /** Games-Howell test is a bit more restrictive than Tukey - Kramer's. */
    @Test
    public void shouldComplyToGamesHowellTest() {
        assertFalse(MM.areSignificanltyDifferentAccordingToGamesHowell(0.95, 0, 1));
        assertTrue(MM.areSignificanltyDifferentAccordingToGamesHowell(0.95, 0, 2));
        assertTrue(MM.areSignificanltyDifferentAccordingToGamesHowell(0.95, 0, 3));
        assertFalse(MM.areSignificanltyDifferentAccordingToGamesHowell(0.95, 1, 2));
        assertTrue(MM.areSignificanltyDifferentAccordingToGamesHowell(0.95, 1, 3));
        assertTrue(MM.areSignificanltyDifferentAccordingToGamesHowell(0.95, 2, 3));
    }

    /**
     * @see <a href='http://www.excel-easy.com/examples/anova.html'>
     *  Excel Easy: Anova</a>
     */
    @Test
    public void shouldValidateAnova() {
        OnlineMeasure economics = new OnlineMeasure(42, 53, 49, 53, 43, 44, 45, 52, 54);
        OnlineMeasure medicine = new OnlineMeasure( 69, 54, 58, 64, 64, 55, 56);
        OnlineMeasure history = new OnlineMeasure(  35, 40, 53, 42, 50, 39, 55, 39, 40);

        OnlineMeasure global = new OnlineMeasure(
                42, 53, 49, 53, 43, 44, 45, 52, 54,
                69, 54, 58, 64, 64, 55, 56,
                35, 40, 53, 42, 50, 39, 55, 39, 40
        );

        assertMeasures(economics, medicine, history);

        assertAnova(new MultiMeasure(global, economics, medicine, history));
    }

    private void assertMeasures(OnlineMeasure economics, OnlineMeasure medicine,
            OnlineMeasure history) {
        assertEquals(9, economics.getCount());
        assertEquals(7, medicine.getCount());
        assertEquals(9, history.getCount());

        assertEquals(435, economics.getSum(), 1E-4);
        assertEquals(420, medicine.getSum(), 1E-4);
        assertEquals(393, history.getSum(), 1E-4);

        assertEquals(48.3334, economics.getMean(), 1E-4);
        assertEquals(60, medicine.getMean(), 1E-4);
        assertEquals(43.6667, history.getMean(), 1E-4);

        assertEquals(23.5, economics.getUnbiasedVariance(), 1E-4);
        assertEquals(32.3334, medicine.getUnbiasedVariance(), 1E-4);
        assertEquals(50.5, history.getUnbiasedVariance(), 1E-4);
    }

    private void assertAnova(MultiMeasure mm) {
        assertEquals(35.72727, mm.getAnovaMeanSquareWithin(), 1E-2);
        assertEquals(542.92, mm.getAnovaMeanSquareBetween(), 1E-2);
        assertEquals(15.19623, mm.getAnovaF(), 1E-4);
        assertEquals(7.16E-5, 1 - mm.anovaPValue(), 1E-4);
        assertTrue(mm.isStatisticallyRelevantWithConfidence(0.99));
    }

}
