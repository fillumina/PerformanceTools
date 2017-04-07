package com.fillumina.performance.speed.stats;

import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.stats.Ratio;
import java.io.Serializable;

/**
 * The ratio between two tests.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedRatio implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String testName1;
    private final String testName2;
    private final Measure measure1;
    private final Measure measure2;
    private final double tukeyHSD;

    public SpeedRatio(
            String testName1, Measure measure1,
            String testName2, Measure measure2,
            double tukeyHSD) {
        if (measure1.getMean() > measure2.getMean()) {
            this.testName1 = testName1;
            this.testName2 = testName2;
            this.measure1 = measure1;
            this.measure2 = measure2;
        } else {
            this.testName1 = testName2;
            this.testName2 = testName1;
            this.measure1 = measure2;
            this.measure2 = measure1;
        }
        this.tukeyHSD = tukeyHSD;
    }

    public String getTestName1() {
        return testName1;
    }

    public String getTestName2() {
        return testName2;
    }

    public MeasureRatio getRatio(Ratio confidence) {
        return new MeasureRatio(measure2, measure1, confidence);
    }

    public MeasureRatio getInverseRatio(Ratio confidence) {
        return new MeasureRatio(measure1, measure2, confidence);
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

    public boolean isSingleTest() {
        return testName1.equals(testName2);
    }
}
