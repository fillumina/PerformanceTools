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
        List<AssertableConsumer<TimeSample>> list =
                Arrays.asList((AssertableConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());
        notifier.addConsumerIf(false,
                new AssertableConsumerChain<>(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<AssertableConsumer<TimeSample>> list =
                Arrays.asList((AssertableConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());
        notifier.addConsumerIf(true,
                new AssertableConsumerChain<>(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterable() {
        List<AssertableConsumer<TimeSample>> list =
                Arrays.asList((AssertableConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());
        notifier.addConsumer(new AssertableConsumerChain<>(list));
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        PerformanceConsumerExecutionChecker<TimeSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addConsumerIf(true, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        PerformanceConsumerExecutionChecker<TimeSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addConsumerIf(false, checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(checker.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        PerformanceConsumerExecutionChecker<TimeSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addConsumer(checker);
        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertTrue(checker.isNotified());
    }

    @Test
    public void testClearConsumers() {
        List<AssertableConsumer<TimeSample>> list =
                Arrays.asList((AssertableConsumer<TimeSample>)
                        new PerformanceConsumerExecutionChecker<TimeSample>(),
                        new PerformanceConsumerExecutionChecker<TimeSample>());

        notifier.addConsumer(new AssertableConsumerChain<>(list));

        notifier.clearConsumers();

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        for (AssertableConsumer<TimeSample> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        final PerformanceConsumerExecutionChecker<TimeSample> one =
                new PerformanceConsumerExecutionChecker<>();
        final PerformanceConsumerExecutionChecker<TimeSample> two =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addConsumer(one);
        notifier.addConsumer(two);

        notifier.removeConsumer(one);

        notifier.dispatchToConsumers(EMPTY_SAMPLE);
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
