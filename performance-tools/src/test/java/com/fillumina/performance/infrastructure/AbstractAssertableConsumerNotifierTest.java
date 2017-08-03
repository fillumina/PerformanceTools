package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableConsumerMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.TimeSample;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractAssertableConsumerNotifierTest {

    private static final TimeSample EMPTY_SAMPLE =
            SpeedSampleMock.builder().createSample();

    private static class PerformanceConsumerNotifierTestImpl
            extends AbstractAssertableConsumerNotifier
                <PerformanceConsumerNotifierTestImpl> {
    }

    private final PerformanceConsumerNotifierTestImpl notifier =
            new PerformanceConsumerNotifierTestImpl();


    @Test
    public void shouldNotAddPerformanceConsumerIterableIfFalse() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(TimeSample.class),
                        new AssertableConsumerMock<>(TimeSample.class));
        notifier.addConsumerIf(false, new AssertableConsumerAggregator(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertFalse(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(TimeSample.class),
                        new AssertableConsumerMock<>(TimeSample.class));
        notifier.addConsumerIf(true,
                new AssertableConsumerAggregator(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertTrue(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterable() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(TimeSample.class),
                        new AssertableConsumerMock<>(TimeSample.class));
        notifier.addConsumer(new AssertableConsumerAggregator(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertTrue(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        AssertableConsumerMock<TimeSample> checker =
                new AssertableConsumerMock<>(TimeSample.class);
        notifier.addConsumerIf(true, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        AssertableConsumerMock<TimeSample> checker =
                new AssertableConsumerMock<>(TimeSample.class);
        notifier.addConsumerIf(false, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(checker.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        AssertableConsumerMock<TimeSample> checker =
                new AssertableConsumerMock<>(TimeSample.class);
        notifier.addConsumer(checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void testClearConsumers() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(TimeSample.class),
                        new AssertableConsumerMock<>(TimeSample.class));

        notifier.addConsumer(new AssertableConsumerAggregator(list));

        notifier.clearConsumers();

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertFalse(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        final AssertableConsumerMock<TimeSample> one =
                new AssertableConsumerMock<>(TimeSample.class);
        final AssertableConsumerMock<TimeSample> two =
                new AssertableConsumerMock<>(TimeSample.class);
        notifier.addConsumer(one);
        notifier.addConsumer(two);

        notifier.removeConsumer(one);

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
