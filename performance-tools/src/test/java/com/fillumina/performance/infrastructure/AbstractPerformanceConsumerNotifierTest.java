package com.fillumina.performance.infrastructure;

import com.fillumina.performance.sample.PerformanceSample;
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
        List<PerformanceConsumerExecutionChecker<PerformanceSample>> list =
                Arrays.asList(
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumerIf(false, list);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumerExecutionChecker<?> checker : list) {
            assertFalse(checker.isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<PerformanceConsumerExecutionChecker<PerformanceSample>> list =
                Arrays.asList(
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumerIf(true, list);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumerExecutionChecker<?> checker : list) {
            assertTrue(checker.isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterable() {
        List<PerformanceConsumerExecutionChecker<PerformanceSample>> list =
                Arrays.asList(
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumer(list);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumerExecutionChecker<?> checker : list) {
            assertTrue(checker.isNotified());
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
        List<PerformanceConsumerExecutionChecker<PerformanceSample>> list =
                Arrays.asList(
                        new PerformanceConsumerExecutionChecker<PerformanceSample>(),
                        new PerformanceConsumerExecutionChecker<PerformanceSample>());
        notifier.addPerformanceConsumer(list);

        notifier.clearConsumers();

        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        for (PerformanceConsumerExecutionChecker<?> checker : list) {
            assertFalse(checker.isNotified());
        }
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        final PerformanceConsumerExecutionChecker<PerformanceSample> one =
                new PerformanceConsumerExecutionChecker<>();
        final PerformanceConsumerExecutionChecker<PerformanceSample> two =
                new PerformanceConsumerExecutionChecker<>();
        List<PerformanceConsumerExecutionChecker<PerformanceSample>> list =
                Arrays.asList(one, two);
        notifier.addPerformanceConsumer(list);
        notifier.removePerformanceConsumer(one);
        notifier.dispatchToConsumers(ComposedName.EMPTY, null);
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
