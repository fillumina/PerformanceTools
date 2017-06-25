package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemSample;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.fillumina.performance.infrastructure.AssertableConsumer;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSampleNotificationTest {

    private static class MemSampleConsumerImpl
            implements AssertableConsumer<MemSample> {

        private boolean called;

        @Override
        public void consume(MemSample memSample) {
            called = true;
        }
    }

    private static class MemStatsConsumerImpl
            implements AssertableConsumer<MemStats> {

        private boolean called;

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

        MemAnalyzer analyzer = new MemAnalyzer(executor);
        analyzer.addTest("test", (Runnable) () -> {
            Sink.drain(new Object());
        });
        analyzer.addConsumer(statsConsumer);
        analyzer.execute();

        assertTrue(sampleConsumer.called);
        assertTrue(statsConsumer.called);
    }
}
