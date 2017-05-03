package com.fillumina.performance.mem;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.Sink;
import com.fillumina.performance.mem.sample.MemConsumptionExecutor;
import com.fillumina.performance.mem.sample.MemSample;
import com.fillumina.performance.mem.sample.UsedMemConsumptionExecutor;
import com.fillumina.performance.util.TName;
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
        public void consume(TName tname, MemSample memSample) {
            called = true;
        }
    }

    private static class MemStatsConsumerImpl
            implements PerformanceConsumer<MemStats> {

        private boolean called;

        @Override
        public void consume(TName tname, MemStats memStats) {
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
        analyzer.addTest("test", new Runnable() {
            @Override
            public void run() {
                Sink.drain(new Object());
            }
        });
        analyzer.addPerformanceConsumer(statsConsumer);
        analyzer.execute();

        assertTrue(sampleConsumer.called);
        assertTrue(statsConsumer.called);
    }
}
