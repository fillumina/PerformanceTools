package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemSample;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.speed.sample.AbstractTestable;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleNotificationTest {

    private static class MemSampleConsumerImpl
            implements PerformanceConsumer<MemSample> {

        private boolean called;

        @Override
        public void consume(PHolder<MemSample> holder) {
            called = true;
        }
    }

    private static class MemStatsConsumerImpl
            implements PerformanceConsumer<MemStats> {

        private boolean called;

        @Override
        public void consume(PHolder<MemStats> holder) {
            called = true;
        }
    }

    @Test
    public void shouldNotifySamples() {
        final MemSampleConsumerImpl sampleConsumer =
                new MemSampleConsumerImpl();
        final MemStatsConsumerImpl statsConsumer = new MemStatsConsumerImpl();

        MemConsumptionExecutor executor = UsedMemConsumptionExecutor.INSTANCE;
        executor.addPerformanceConsumer(sampleConsumer);

        MemAnalyzer analyzer = new MemAnalyzer(executor);
        analyzer.addTest("test", new AbstractTestable() {
            @Override
            public Object test() {
                return new Object();
            }
        });
        analyzer.addPerformanceConsumer(statsConsumer);
        analyzer.execute();

        assertTrue(sampleConsumer.called);
        assertTrue(statsConsumer.called);
    }
}
