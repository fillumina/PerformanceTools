package com.fillumina.performance.util;

import com.fillumina.performance.mock.NotifiableConsumerMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerNotifierTest {

    @Test
    public void shouldNotifyNobody() {
        ConsumerNotifier<?,String> notifier = new ConsumerNotifier<>();
        notifier.dispatchToConsumers("Hello");
    }

    @Test
    public void shouldNotifyAConsumer() {
        NotifiableConsumerMock<String> consumer =
                new NotifiableConsumerMock<>();

        ConsumerNotifier<?,String> notifier = new ConsumerNotifier<>();

        notifier.addConsumer(consumer);

        notifier.dispatchToConsumers("Hello");

        assertTrue(consumer.isNotified());
        assertEquals("Hello", consumer.getReceivedMessage());
    }

    @Test
    public void shouldNotifyConsumers() {
        NotifiableConsumerMock<String> consumer1 =
                new NotifiableConsumerMock<>();

        NotifiableConsumerMock<String> consumer2 =
                new NotifiableConsumerMock<>();

        ConsumerNotifier<?,String> notifier = new ConsumerNotifier<>();

        notifier.addConsumer(consumer1);
        notifier.addConsumer(consumer2);

        notifier.dispatchToConsumers("Hello");

        assertTrue(consumer1.isNotified());
        assertEquals("Hello", consumer1.getReceivedMessage());

        assertTrue(consumer2.isNotified());
        assertEquals("Hello", consumer2.getReceivedMessage());
    }

}
