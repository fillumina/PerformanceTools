package com.fillumina.performance.infrastructure;

import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.StaticPath;
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
                <PerformanceConsumerNotifierTestImpl, SpeedSample> {
    }

    private PerformanceConsumerNotifierTestImpl notifier =
            new PerformanceConsumerNotifierTestImpl();


    @Test
    public void shouldSetComposedName() {
        StaticPath cn = CName.EMPTY.append("first").append("second");
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
        List<PerformanceConsumer<SpeedSample>> list =
                Arrays.asList((PerformanceConsumer<SpeedSample>)
                        new PerformanceConsumerExecutionChecker<SpeedSample>(),
                        new PerformanceConsumerExecutionChecker<SpeedSample>());
        notifier.addPerformanceConsumerIf(false,
                new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        for (PerformanceConsumer<?> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterableIfTrue() {
        List<PerformanceConsumer<SpeedSample>> list =
                Arrays.asList((PerformanceConsumer<SpeedSample>)
                        new PerformanceConsumerExecutionChecker<SpeedSample>(),
                        new PerformanceConsumerExecutionChecker<SpeedSample>());
        notifier.addPerformanceConsumerIf(true,
                new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        for (PerformanceConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIterable() {
        List<PerformanceConsumer<SpeedSample>> list =
                Arrays.asList((PerformanceConsumer<SpeedSample>)
                        new PerformanceConsumerExecutionChecker<SpeedSample>(),
                        new PerformanceConsumerExecutionChecker<SpeedSample>());
        notifier.addPerformanceConsumer(new PerformanceConsumerChain<>(list));
        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        for (PerformanceConsumer<?> checker : list) {
            assertTrue(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldAddPerformanceConsumerIfTrue() {
        PerformanceConsumerExecutionChecker<SpeedSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumerIf(true, checker);
        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        assertTrue(checker.isNotified());
    }

    @Test
    public void shouldNotAddPerformanceConsumerIfFalse() {
        PerformanceConsumerExecutionChecker<SpeedSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumerIf(false, checker);
        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        assertFalse(checker.isNotified());
    }

    @Test
    public void shouldAddPerformanceConsumer() {
        PerformanceConsumerExecutionChecker<SpeedSample> checker =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumer(checker);
        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        assertTrue(checker.isNotified());
    }

    @Test
    public void testClearConsumers() {
        List<PerformanceConsumer<SpeedSample>> list =
                Arrays.asList((PerformanceConsumer<SpeedSample>)
                        new PerformanceConsumerExecutionChecker<SpeedSample>(),
                        new PerformanceConsumerExecutionChecker<SpeedSample>());

        notifier.addPerformanceConsumer(new PerformanceConsumerChain<>(list));

        notifier.clearConsumers();

        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        for (PerformanceConsumer<SpeedSample> checker : list) {
            assertFalse(((PerformanceConsumerExecutionChecker)checker).isNotified());
        }
    }

    @Test
    public void shouldRemovePerformanceConsumer() {
        final PerformanceConsumerExecutionChecker<SpeedSample> one =
                new PerformanceConsumerExecutionChecker<>();
        final PerformanceConsumerExecutionChecker<SpeedSample> two =
                new PerformanceConsumerExecutionChecker<>();
        notifier.addPerformanceConsumer(one);
        notifier.addPerformanceConsumer(two);

        notifier.removePerformanceConsumer(one);

        notifier.dispatchToConsumers(PHolder.<SpeedSample>empty());
        assertFalse(one.isNotified());
        assertTrue(two.isNotified());
    }

}
