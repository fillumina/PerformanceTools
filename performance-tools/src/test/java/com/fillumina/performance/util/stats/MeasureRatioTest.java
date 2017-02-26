package com.fillumina.performance.util.stats;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @see <a href='http://www.graphpad.com/quickcalcs/errorProp1/?Format=SEM'>
 *  Harvey J. Motulsky: Calculate the CI of a quotient</a>
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MeasureRatioTest {
    private static final String NL = System.lineSeparator();

    @Test
    public void testConfidence90() {
        assertConfidenceIntervalOfRatio(
                12, 1.2, 27,
                8, 0.7, 30,
                0.90,
                1.436366754, 1.566697826);
    }

    @Test
    public void testConfidence95() {
        assertConfidenceIntervalOfRatio(
                12, 1.2, 27,
                8, 0.7, 30,
                0.95,
                1.424114218, 1.580284941);
    }

    @Test
    public void testConfidence99() {
        assertConfidenceIntervalOfRatio(
                12, 1.2, 27,
                8, 0.7, 30,
                0.99,
                1.399848567, 1.607958523);
    }

    private void assertConfidenceIntervalOfRatio(
            double meanA, double varianceA, int countA,
            double meanB, double varianceB, int countB,
            double confidence,
            double expectedLower, double expectedUpper) {

        MeasureRatio mrci = new MeasureRatio(
                        meanA, varianceA, countA,
                        meanB, varianceB, countB,
                        Ratio.value(confidence));

        double ratio = mrci.getRatio();
        double marginOfError = mrci.getMarginOfError();

        StringBuilder buf = new StringBuilder();
        buf.append(NL).append("CONFIDENCE =\t").append(confidence);
        buf.append(NL).append("MEAN A =\t").append(meanA);
        buf.append(NL).append("VARIANCE A =\t").append(varianceA);
        buf.append(NL).append("STD DEV A =\t").append(Math.sqrt(varianceA));
        buf.append(NL).append("SEM A =    \t").append(sem(varianceA, countA));
        buf.append(NL).append("MEAN B =\t").append(meanB);
        buf.append(NL).append("VARIANCE B =\t").append(varianceB);
        buf.append(NL).append("STD DEV B =\t").append(Math.sqrt(varianceB));
        buf.append(NL).append("SEM B =    \t").append(sem(varianceB, countB));
        buf.append(NL).append("Quotient =\t").append(mrci);
        buf.append(NL).append("StandardError =\t")
                .append(mrci.getStandardError());
        buf.append(NL).append("LOWER INTERVAL BOUND =\t").append(ratio - marginOfError);
        buf.append(NL).append("UPPER INTERVAL BOUND =\t").append(ratio + marginOfError);


        assertEquals(buf.toString(), expectedLower, ratio - marginOfError, 1E-5);
        assertEquals(buf.toString(), expectedUpper, ratio + marginOfError, 1E-5);
    }

    /**
     * Standard error of the mean.
     * @see <a href='http://www.sportsci.org/resource/stats/meansd.html'>
     *  Standard Error of the mean</a>
     */
    private double sem(double variance, int samples) {
        return Math.sqrt(variance / samples);
    }
}
