package com.fillumina.performance.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultipleMeasureTest {

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
        Measure north = new Measure(NORTH);
        Measure south = new Measure(SOUTH);
        Measure east = new Measure(EAST);
        Measure owest = new Measure(OWEST);
        RunningMeasure tot = new RunningMeasure();
        tot.addAll(NORTH);
        tot.addAll(SOUTH);
        tot.addAll(EAST);
        tot.addAll(OWEST);

        MultipleMeasure anova = new MultipleMeasure(tot, north, south, east, owest);

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
        Measure x1 = new Measure(X1);
        Measure x2 = new Measure(X2);
        Measure x3 = new Measure(X3);
        RunningMeasure tot = new RunningMeasure()
                .addAll(X1)
                .addAll(X2)
                .addAll(X3);

        MultipleMeasure anova = new MultipleMeasure(tot, x1, x2, x3);

        assertTrue(anova.isStatisticallyRelevantWithConfidence(0.95));
        assertEquals(15.04, anova.getAnovaMeanSquareBetween(), 1E-2);
        assertEquals(4.18, anova.getAnovaMeanSquareWithin(), 1E-2);
    }

    /*
        see http://statistica.mooo.com/OneWay_Anova_with_TukeyHSD_result
    */

    private static final Measure A = new Measure(
            0.28551035,
            0.338524035,
            0.088313218,
            0.205930807,
            0.363240102);

    @Test
    public void shouldCheckStatsA() {
        assertEquals(5, A.count(), 1E-3);
        assertEquals(1.2815, A.sum(), 1E-3);
        assertEquals(0.2563, A.mean(), 1E-3);
        assertEquals(0.0125, A.unbiasedVariance(), 1E-3);
        assertEquals(0.1116, A.unbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0499, A.standardError(), 1E-3);
    }

    private static final Measure B = new Measure(
            0.52173913,
            0.763358779,
            0.32546786,
            0.425305688,
            0.378071834);

    @Test
    public void shouldCheckStatsB() {
        assertEquals(5, B.count(), 1E-3);
        assertEquals(2.4139, B.sum(), 1E-3);
        assertEquals(0.4828, B.mean(), 1E-3);
        assertEquals(0.0298, B.unbiasedVariance(), 1E-3);
        assertEquals(0.1727, B.unbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0772, B.standardError(), 1E-3);
    }

    private static final Measure C = new Measure(
            0.989119683,
            1.192718142,
            0.788288288,
            0.549176236,
            0.544588155);

    @Test
    public void shouldCheckStatsC() {
        assertEquals(5, C.count(), 1E-3);
        assertEquals(4.0639, C.sum(), 1E-3);
        assertEquals(0.8128, C.mean(), 1E-3);
        assertEquals(0.0794, C.unbiasedVariance(), 1E-3);
        assertEquals(0.2817, C.unbiasedStandardDeviation(), 1E-3);
        assertEquals(0.1260, C.standardError(), 1E-3);
    }

    private static final Measure D = new Measure(
            1.26705653,
            1.625320787,
            1.266108976,
            1.154187629,
            1.268498943,
            1.069518717);

    @Test
    public void shouldCheckStatsD() {
        assertEquals(6, D.count(), 1E-3);
        assertEquals(7.6507, D.sum(), 1E-3);
        assertEquals(1.2751, D.mean(), 1E-3);
        assertEquals(0.0359, D.unbiasedVariance(), 1E-3);
        assertEquals(0.1896, D.unbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0774, D.standardError(), 1E-3);
    }

    private static final Measure G = new Measure(
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
        assertEquals(21, G.count(), 1E-3);
        assertEquals(15.4100, G.sum(), 1E-3);
        assertEquals(0.7338, G.mean(), 1E-3);
        assertEquals(0.1955, G.unbiasedVariance(), 1E-3);
        assertEquals(0.4422, G.unbiasedStandardDeviation(), 1E-3);
        assertEquals(0.0965, G.standardError(), 1E-3);
    }

    private static final MultipleMeasure MM =
            new MultipleMeasure(G, A, B, C, D);

    @Test
    public void shouldApplyAnovaToABCD() {
        assertEquals(27.5943, MM.getAnovaF(), 1E-3);
        assertEquals(9.2580E-7, MM.anovaPValue(), 1E-10);
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

}
