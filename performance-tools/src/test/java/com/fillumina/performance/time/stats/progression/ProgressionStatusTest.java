package com.fillumina.performance.time.stats.progression;

import com.fillumina.performance.mock.SpeedStatsMock;
import com.fillumina.performance.time.sample.IterationTimeAccumulator;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.time.stats.AverageTimeStats;
import com.fillumina.performance.time.stats.TimeSampleCollector;
import com.fillumina.performance.time.stats.TimeStats;
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
        TimeSampleCollector<AverageTimeStats> collector = 
                TimeSampleCollector.createSpeedCollector();


        TimeSample speedSample = new TimeSample(
                LinkedMap.create("first",
                        new IterationTimeAccumulator().add(123, 100),
                        "second",
                        new IterationTimeAccumulator().add(246, 100)));

        TimeStats lastStats = SpeedStatsMock
                .builder()
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
                timeSpentCoolingCpuMs,
                collector);

        assertEquals(rejectionMessage, ps.getRejectionMessage());
        assertEquals(sample, ps.getSample());
        assertEquals(totalSamples, ps.getTotalSamples());
        assertEquals(repetitions, ps.getRepetitions());
        assertArrayEquals(iterations, ps.getIterations());
        assertEquals(speedSample, ps.getSpeedSample());
        assertEquals(lastStats, ps.getLastStats());
        assertEquals(timeSpentCoolingCpuMs, ps.getTimeSpentCoolingCpuMs());
        assertEquals(collector, ps.getSpeedSampleCollector());
    }

}
