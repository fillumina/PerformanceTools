package com.fillumina.performance.infrastructure;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.speed.sample.SpeedSample;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceHolderTest {

    @Test
    public void shouldReportNullPermanceAvailable() {
        TreeHolder<SpeedSample, SpeedSample> holder =
                new TreeHolder<>(null);

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldReportThePresenceOfAPerformance() {
        SpeedSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        TreeHolder<SpeedSample, SpeedSample> holder =
                new TreeHolder<>(sample);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldUseAPerformance() {
        SpeedSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        TreeHolder<SpeedSample, SpeedSample> holder =
                new TreeHolder<>(sample);

        PerformanceConsumerExecutionChecker<SpeedSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertEquals(sample, consumer.getReceivedPerformance());
    }
}
