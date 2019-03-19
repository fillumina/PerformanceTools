package com.fillumina.performance.util;

import com.fillumina.performance.mock.NotifiableConsumerMock;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ConsumerNotifierImplTest {

    private static class InnerConsumerNotifierImpl
            extends ConsumerNotifier<InnerConsumerNotifierImpl, String> {
    }

    private final InnerConsumerNotifierImpl notifier =
            new InnerConsumerNotifierImpl();


    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        NotifiableConsumerMock<String> one = new NotifiableConsumerMock<>();

        notifier.addConsumerIf(false, one);
        notifier.dispatchToConsumers("message");

        assertFalse(one.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        NotifiableConsumerMock<String> one = new NotifiableConsumerMock<>();

        notifier.addConsumerIf(true, one);
        notifier.dispatchToConsumers("message");

        assertTrue(one.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        NotifiableConsumerMock<String> one = new NotifiableConsumerMock<>();

        notifier.addConsumer(one);
        notifier.dispatchToConsumers("message");

        assertTrue(one.isNotified());
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        NotifiableConsumerMock<String> one = new NotifiableConsumerMock<>();

        notifier.addConsumer(one);
        notifier.removeConsumer(one);
        notifier.dispatchToConsumers("message");

        assertFalse(one.isNotified());
    }

    @Test
    public void shoudlClearConsumers() {
        NotifiableConsumerMock<String> one = new NotifiableConsumerMock<>();

        notifier.addConsumer(one);
        notifier.clearConsumers();
        notifier.dispatchToConsumers("message");

        assertFalse(one.isNotified());
    }
}
