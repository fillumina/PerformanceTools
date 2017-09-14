package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.AbstractAssertableConsumer;
import com.fillumina.performance.infrastructure.SafeSink;
import com.fillumina.performance.mem.sample.UsedMemSample;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.fillumina.performance.mem.sample.MemSampleProducer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleNotificationTest {

    private static class MemSampleConsumerImpl
            extends AbstractAssertableConsumer<UsedMemSample> {
        private boolean called;

        public MemSampleConsumerImpl() {
            super(UsedMemSample.class);
        }

        @Override
        public void accept(UsedMemSample memSample) {
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
        public void accept(MemStats memStats) {
            called = true;
        }
    }

    @Test
    public void shouldNotifySamples() {
        final MemSampleConsumerImpl sampleConsumer =
                new MemSampleConsumerImpl();
        final MemStatsConsumerImpl statsConsumer = new MemStatsConsumerImpl();

        MemSampleProducer executor = UsedMemSampleProducer.INSTANCE;
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
