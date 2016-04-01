package com.fillumina.performance.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AnovaTest {

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

        Anova anova = new Anova(0.99, tot, north, south, east, owest);

        assertTrue(anova.isStatisticallyRelevant());
        assertEquals(6.47, anova.getMeanSquareAmong(), 1E-2);
        assertEquals(1.32, anova.getMeanSquareWithin(), 1E-2);
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

        Anova anova = new Anova(0.95, tot, x1, x2, x3);

        assertTrue(anova.isStatisticallyRelevant());
        assertEquals(15.04, anova.getMeanSquareAmong(), 1E-2);
        assertEquals(4.18, anova.getMeanSquareWithin(), 1E-2);
    }
}
