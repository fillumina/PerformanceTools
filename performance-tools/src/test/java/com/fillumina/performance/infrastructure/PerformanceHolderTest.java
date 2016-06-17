package com.fillumina.performance.infrastructure;

import com.fillumina.performance.FakePerformanceCreator;
import com.fillumina.performance.speed.sample.PerformanceSample;
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
        PerformanceHolder<PerformanceSample> holder =
                new PerformanceHolder<>(null);

        assertTrue(holder.isEmpty());
    }

    @Test
    public void shouldReportThePresenceOfAPerformance() {
        PerformanceSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PerformanceHolder<PerformanceSample> holder =
                new PerformanceHolder<>(sample);

        assertFalse(holder.isEmpty());
    }

    @Test
    public void shouldUseAPerformance() {
        PerformanceSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PerformanceHolder<PerformanceSample> holder =
                new PerformanceHolder<>(sample);

        PerformanceConsumerExecutionChecker<PerformanceSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertEquals(sample, consumer.getReceivedPerformance());
    }

    @Test
    public void shouldNotUseAPerformanceIfWheneverReceiveFalse() {
        PerformanceSample sample = FakePerformanceCreator.createSample(10,
                new Object[][]{{"one", 1}, {"two", 2}});

        PerformanceHolder<PerformanceSample> holder =
                new PerformanceHolder<>(sample);

        holder.whenever(false);

        PerformanceConsumerExecutionChecker<PerformanceSample> consumer =
                new PerformanceConsumerExecutionChecker<>();

        holder.use(consumer);

        assertFalse(consumer.isNotified());
    }
}
