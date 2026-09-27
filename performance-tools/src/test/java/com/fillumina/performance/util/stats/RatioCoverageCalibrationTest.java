package com.fillumina.performance.util.stats;

import java.util.Random;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/** Seeded probes, not a guarantee of coverage on real hardware. */
public class RatioCoverageCalibrationTest {
    @Test
    public void shouldCoverEqualAndUnequalMeansUnderIndependentNormalNoise() {
        assertTrue("equal independent workloads", misses(0, 10, 10) <= 15);
        assertTrue("unequal independent workloads", misses(0, 12, 10) <= 15);
    }

    @Test
    public void shouldExposeUndercoverageWhenSamplesAreAutocorrelated() {
        assertTrue("naive intervals substantially under-cover correlated workloads",
                misses(0.85, 10, 10) > 50);
    }

    private int misses(double correlation, double meanA, double meanB) {
        Random random = new Random(1234567L);
        int misses = 0;
        for (int run = 0; run < 400; run++) {
            OnlineMeasure a = new OnlineMeasure();
            OnlineMeasure b = new OnlineMeasure();
            double noiseA = 0;
            double noiseB = 0;
            for (int sample = 0; sample < 33; sample++) {
                double innovation = Math.sqrt(1 - correlation * correlation);
                noiseA = correlation * noiseA + innovation * random.nextGaussian();
                noiseB = correlation * noiseB + innovation * random.nextGaussian();
                a.addSample(meanA + noiseA);
                b.addSample(meanB + noiseB);
            }
            MeasureRatio ratio = new MeasureRatio(a, b, Ratio.P_99);
            double truth = meanA / meanB;
            if (!ratio.isValid() || truth < ratio.getLowerBound() ||
                    truth > ratio.getUpperBound()) {
                misses++;
            }
        }
        return misses;
    }
}
