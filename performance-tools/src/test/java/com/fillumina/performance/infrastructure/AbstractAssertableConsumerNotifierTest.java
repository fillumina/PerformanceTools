package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.AssertableConsumerMock;
import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.AverageTimeSample;
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

    private static final AverageTimeSample EMPTY_SAMPLE =
            SpeedSampleMock.builder().createSample();

    private static class PerformanceConsumerNotifierTestImpl
            extends ConsumerNotifierImpl
                <PerformanceConsumerNotifierTestImpl> {
    }

    private final PerformanceConsumerNotifierTestImpl notifier =
            new PerformanceConsumerNotifierTestImpl();


    @Test
    public void shouldNotAddPerformanceConsumerIterableIfFalse() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(AverageTimeSample.class),
                        new AssertableConsumerMock<>(AverageTimeSample.class));
        notifier.addConsumerIf(false, new AssertableConsumerAggregator(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertFalse(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(AverageTimeSample.class),
                        new AssertableConsumerMock<>(AverageTimeSample.class));
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
                Arrays.asList(new AssertableConsumerMock<>(AverageTimeSample.class),
                        new AssertableConsumerMock<>(AverageTimeSample.class));
        notifier.addConsumer(new AssertableConsumerAggregator(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertTrue(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        AssertableConsumerMock<AverageTimeSample> checker =
                new AssertableConsumerMock<>(AverageTimeSample.class);
        notifier.addConsumerIf(true, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        AssertableConsumerMock<AverageTimeSample> checker =
                new AssertableConsumerMock<>(AverageTimeSample.class);
        notifier.addConsumerIf(false, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(checker.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        AssertableConsumerMock<AverageTimeSample> checker =
                new AssertableConsumerMock<>(AverageTimeSample.class);
        notifier.addConsumer(checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void testClearConsumers() {
        List<AssertableConsumer<?>> list =
                Arrays.asList(new AssertableConsumerMock<>(AverageTimeSample.class),
                        new AssertableConsumerMock<>(AverageTimeSample.class));

        notifier.addConsumer(new AssertableConsumerAggregator(list));

        notifier.clearConsumers();

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertFalse(((AssertableConsumerMock)checker).isNotified());
        }
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        final AssertableConsumerMock<AverageTimeSample> one =
                new AssertableConsumerMock<>(AverageTimeSample.class);
        final AssertableConsumerMock<AverageTimeSample> two =
                new AssertableConsumerMock<>(AverageTimeSample.class);
        notifier.addConsumer(one);
        notifier.addConsumer(two);

        notifier.removeConsumer(one);

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
