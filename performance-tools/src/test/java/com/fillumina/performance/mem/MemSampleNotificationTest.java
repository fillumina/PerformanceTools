package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractAssertableConsumer;
import com.fillumina.performance.infrastructure.SafeSink;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemSample;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleNotificationTest {

    private static class MemSampleConsumerImpl
            extends AbstractAssertableConsumer<MemSample> {
        private boolean called;

        public MemSampleConsumerImpl() {
            super(MemSample.class);
        }

        @Override
        public void consume(MemSample memSample) {
            called = true;
        }
    }

    private static class MemStatsConsumerImpl
            extends AbstractAssertableConsumer<MemStats> {

        private boolean called;

        public MemStatsConsumerImpl() {
            super(MemStats.class);
        }

        @Override
        public void consume(MemStats memStats) {
            called = true;
        }
    }

    @Test
    public void shouldNotifySamples() {
        final MemSampleConsumerImpl sampleConsumer =
                new MemSampleConsumerImpl();
        final MemStatsConsumerImpl statsConsumer = new MemStatsConsumerImpl();

        MemConsumptionExecutor executor = UsedMemConsumptionExecutor.INSTANCE;
        executor.addConsumer(sampleConsumer);

        MemStatsProducer analyzer = new MemStatsProducer(executor);
        analyzer.addTest("test", (Runnable) () -> {
            SafeSink.drain(new Object());
        });
        analyzer.addConsumer(statsConsumer);
        analyzer.execute();

        assertTrue(sampleConsumer.called);
        assertTrue(statsConsumer.called);
    }
}
