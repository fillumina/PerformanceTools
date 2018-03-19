package com.fillumina.performance.util.stats;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ReciprocalOnlineMeasureSamplerTest {

    @Test
    public void shouldCreateStatsFromAscendingSequence() {
        ReciprocalOnlineMeasureSampler sampler =
                new ReciprocalOnlineMeasureSampler();

        sampler.addAll(1, 2, 3, 4, 5);

        assertEquals(3.0, sampler.getDirect().getMean(), 0);
        assertEquals(2.0, sampler.getDirect().getVariance(), 0);
        assertEquals(0.4567, sampler.getInverse().getMean(), 0.001);
        assertEquals(0.0841, sampler.getInverse().getVariance(), 0.001);
    }

    @Test
    public void shouldShouldCreateStatsFromSequenceOf1s() {
        ReciprocalOnlineMeasureSampler sampler =
                new ReciprocalOnlineMeasureSampler();

        sampler.addAll(1, 1, 1, 1, 1);

        assertEquals(1.0, sampler.getDirect().getMean(), 0);
        assertEquals(1.0, sampler.getInverse().getMean(), 0);
    }

    @Test
    public void shouldShouldCreateStatsFromSequenceOf2s() {
        ReciprocalOnlineMeasureSampler sampler =
                new ReciprocalOnlineMeasureSampler();

        sampler.addAll(2, 2, 2, 2, 2);

        assertEquals(2.0, sampler.getDirect().getMean(), 0);
        assertEquals(0.5, sampler.getInverse().getMean(), 0);
    }

    @Test
    public void shouldInverseUnits() {
        ReciprocalOnlineMeasureSampler sampler =
                new ReciprocalOnlineMeasureSampler();

        sampler.addAll(1_000, 2_000, 3_000, 4_000, 5_000);

        assertEquals(3_000.0, sampler.getDirect().getMean(), 0);
        assertEquals(1414.2135, sampler.getDirect().getStandardDeviation(), 0.001);
        assertEquals(4.567E-4, sampler.getInverse().getMean(), 1E-7);
        assertEquals(2.901E-4, sampler.getInverse().getStandardDeviation(), 1E-7);
    }

}
