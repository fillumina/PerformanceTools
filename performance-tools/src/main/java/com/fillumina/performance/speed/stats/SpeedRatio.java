package com.fillumina.performance.speed.stats;

import com.fillumina.performance.util.stats.MeasureRatio;
import java.io.Serializable;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedRatio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String testName1, testName2;
    private final MeasureRatio ratio;
    private final MeasureRatio inverseRatio;
    private final double tukeyHSD;

    public SpeedRatio(String testName1, String testName2,
            MeasureRatio ratio,
            MeasureRatio inverseRatio,
            double tukeyHSD) {
        this.testName1 = testName1;
        this.testName2 = testName2;
        this.ratio = ratio;
        this.inverseRatio = inverseRatio;
        this.tukeyHSD = tukeyHSD;
    }

    public String getTestName1() {
        return testName1;
    }

    public String getTestName2() {
        return testName2;
    }

    public MeasureRatio getRatio() {
        return ratio;
    }

    public MeasureRatio getInverseRatio() {
        return inverseRatio;
    }

    /**
     * The Tukey-Kramer honest significant difference test finds means that are
     * significantly different from each other. Consequently it can find means
     * that are significantly equal or not comparable (without enough data
     * to be statistically significant).
     *
     * @see https://en.wikipedia.org/wiki/Tukey%27s_range_test
     * @return the Tukey-Kramer honest significant difference (HSD) test value
     *         between the two tests with given indexes.
     */
    public double getTukeyHSD() {
        return tukeyHSD;
    }

    public boolean compareTests(String test1, String test2) {
        return (this.testName1.equals(test1) && this.testName2.equals(test2)) ||
                (this.testName1.equals(test2) && this.testName2.equals(test1));
    }
}
