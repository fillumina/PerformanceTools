package com.fillumina.performance.consumer;

import com.fillumina.performance.producer.LoopPerformances;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TeePerformanceConsumerTest {

    @Test
    public void shouldNotifyBothPassedConsumers() {
        final AtomicInteger first = new AtomicInteger();
        final AtomicInteger second = new AtomicInteger();

        final PerformanceConsumer pc = TeePerformanceConsumer.createFrom(
                new PerformanceConsumer() {

            @Override
            public void consume(final String message,
                    final LoopPerformances loopPerformances) {
                first.set(Integer.valueOf(message));
            }
        }, new PerformanceConsumer() {

            @Override
            public void consume(final String message,
                    final LoopPerformances loopPerformances) {
                second.set(Integer.valueOf(message));
            }
        });

        pc.consume("1234", null);

        assertEquals(1234, first.get());
        assertEquals(1234, second.get());
    }

}
