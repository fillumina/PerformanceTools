package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.SpeedSampleMock;
import com.fillumina.performance.time.sample.TimeSample;
import com.fillumina.performance.util.TName;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractPerformanceConsumerNotifierTest {

    private static final TimeSample EMPTY_SAMPLE =
            SpeedSampleMock.builder().createSample();

    private static class PerformanceConsumerNotifierTestImpl
            extends AbstractPerformanceConsumerNotifier
                <PerformanceConsumerNotifierTestImpl, TimeSample> {
    }

    private PerformanceConsumerNotifierTestImpl notifier =
            new PerformanceConsumerNotifierTestImpl();


    @Test
    public void shouldSetComposedName() {
        TName cn = TN.EMPTY.append("first").append("second");
        notifier.setName(cn);
        assertEquals(cn, notifier.getName());
    }

    @Test
    public void shouldSetName() {
        notifier.setName("example");
        assertEquals("example", notifier.getName().toString());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIterableIfFalse() {
        List<PerformanceConsumer<TimeSample>> list =
                Arrays.asList((PerformanceConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());
        notifier.addPerformanceConsumerIf(false,
                new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (PerformanceConsumer<?> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<PerformanceConsumer<TimeSample>> list =
                Arrays.asList((PerformanceConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());
        notifier.addPerformanceConsumerIf(true,
                new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (PerformanceConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterable() {
        List<PerformanceConsumer<TimeSample>> list =
                Arrays.asList((PerformanceConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());
        notifier.addPerformanceConsumer(new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (PerformanceConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        PerformanceConsumerExecutionChecker<TimeSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumerIf(true, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        PerformanceConsumerExecutionChecker<TimeSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumerIf(false, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(checker.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        PerformanceConsumerExecutionChecker<TimeSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumer(checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void testClearConsumers() {
        List<PerformanceConsumer<TimeSample>> list =
                Arrays.asList((PerformanceConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());

        notifier.addPerformanceConsumer(new PerformanceConsumerChain<>(list));

        notifier.clearConsumers();

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (PerformanceConsumer<TimeSample> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        final PerformanceConsumerExecutionChecker<TimeSample> one =
                new PerformanceConsumerExecutionChecker<>();
        final PerformanceConsumerExecutionChecker<TimeSample> two =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumer(one);
        notifier.addPerformanceConsumer(two);

        notifier.removePerformanceConsumer(one);

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
