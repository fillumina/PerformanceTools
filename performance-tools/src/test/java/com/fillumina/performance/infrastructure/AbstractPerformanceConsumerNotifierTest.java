package com.fillumina.performance.infrastructure;

import com.fillumina.performance.speed.sample.PerformanceSample;
import com.fillumina.performance.util.ComposedName;
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

    private static class PerformanceConsumerNotifierTestImpl
            extends AbstractPerformanceConsumerNotifier
                <PerformanceConsumerNotifierTestImpl, PerformanceSample> {
    }

    private PerformanceConsumerNotifierTestImpl notifier =
            new PerformanceConsumerNotifierTestImpl();

    @Test
    public void shouldSetComposedName() {
        ComposedName cn = ComposedName.create("first").add("second");
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
        List<PerformanceConsumer<PerformanceSample>> list =
                Arrays.asList((PerformanceConsumer<PerformanceSample>)
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumerIf(false,
                new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumer<?> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<PerformanceConsumer<PerformanceSample>> list =
                Arrays.asList((PerformanceConsumer<PerformanceSample>)
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumerIf(true,
                new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterable() {
        List<PerformanceConsumer<PerformanceSample>> list =
                Arrays.asList((PerformanceConsumer<PerformanceSample>)
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumer(new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        PerformanceConsumerExecutionChecker<PerformanceSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumerIf(true, checker);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        assertTrue(checker.isNotified());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        PerformanceConsumerExecutionChecker<PerformanceSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumerIf(false, checker);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        assertFalse(checker.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        PerformanceConsumerExecutionChecker<PerformanceSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumer(checker);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        assertTrue(checker.isNotified());
    }

    @Test
    public void testClearConsumers() {
        List<PerformanceConsumer<PerformanceSample>> list =
                Arrays.asList((PerformanceConsumer<PerformanceSample>)
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());

        notifier.addPerformanceConsumer(new PerformanceConsumerChain<>(list));

        notifier.clearConsumers();

        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumer<PerformanceSample> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRemovePerformanceConsumer() {
        final PerformanceConsumerExecutionChecker<PerformanceSample> one =
                new PerformanceConsumerExecutionChecker<>();
        final PerformanceConsumerExecutionChecker<PerformanceSample> two =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumer(one);
        notifier.addPerformanceConsumer(two);

        notifier.removePerformanceConsumer(one);

        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
