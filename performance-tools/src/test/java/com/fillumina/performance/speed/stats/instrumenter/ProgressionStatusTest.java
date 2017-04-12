package com.fillumina.performance.speed.stats.instrumenter;

import com.fillumina.performance.mock.MockPerformanceCreator;
import com.fillumina.performance.speed.sample.IterationTimeAccumulator;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.speed.stats.SpeedStats;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ProgressionStatusTest {

    @Test
    public void shouldRecordInput() {
        String rejectionMessage = "xyz_message";
        int sample = 123;
        int totalSamples = 456;
        int repetitions = 789;
        int[] iterations = new int[]{ 10, 11, 12};
        int timeSpentCoolingCpuMs = 123456;

        SpeedSample speedSample = new SpeedSample(
                LinkedMap.create("first",
                        new IterationTimeAccumulator().add(123, 100),
                        "second",
                        new IterationTimeAccumulator().add(246, 100)));

        SpeedStats lastStats = MockPerformanceCreator
                .speedStatsBuilder()
                .confidence(Ratio.decimal(0.9))
                .iterationsPerSample(300)
                .addTest("first").timeNs(10).stdev(5).samples(200).endTest()
                .addTest("second").timeNs(20).stdev(7).samples(250).endTest()
                .buildWithNormalDistribution();


        SampleProgressionStatus ps = new SampleProgressionStatus(
                rejectionMessage,
                sample,
                totalSamples,
                repetitions,
                iterations,
                speedSample,
                lastStats,
                timeSpentCoolingCpuMs);

        assertEquals(rejectionMessage, ps.getRejectionMessage());
        assertEquals(sample, ps.getSample());
        assertEquals(totalSamples, ps.getTotalSamples());
        assertEquals(repetitions, ps.getRepetitions());
        assertArrayEquals(iterations, ps.getIterations());
        assertEquals(speedSample, ps.getSpeedSample());
        assertEquals(lastStats, ps.getLastStats());
        assertEquals(timeSpentCoolingCpuMs, ps.getTimeSpentCoolingCpuMs());
    }

}
