package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.Assertable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractAssertableConsumerNotifier
            <I extends AbstractAssertableConsumerNotifier<I>>
        implements AssertableConsumerNotifier {

    private final List<AssertableConsumer<?>> consumers =
            new CopyOnWriteArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public I addConsumerIf(boolean condition, AssertableConsumer<?> consumer) {
        if (condition) {
            addConsumer(consumer);
        }
        return (I) this;
    }

    /**
     * {@link AssertableConsumer}s added here will be notified any time a
     * statistics is elaborated even if it is not the final one
     * (which will be finally reported).
     * A {@code null} argument and {@code null} array elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I addConsumer(AssertableConsumer<?> consumer) {
        if (consumer != null) {
            consumers.add(consumer);
        }
        return (I) this;
    }

    /**
     * A {@code null} argument and {@code null} array's elements are ignored.
     */
    @Override
    @SuppressWarnings("unchecked")
    public I removeConsumer(final AssertableConsumer<?> consumer) {
        if (consumer != null) {
            consumers.remove(consumer);
        }
        return (I) this;
    }

    /**
     * Passes the {@link PerformanceSample} to all
     * {@link PerformanceSampleConsumer}s
     * in the same order they were added.
     */
    @SuppressWarnings("unchecked")
    protected void dispatchToConsumers(Assertable assertable) {
        Class<? extends Assertable> required = assertable.getClass();
        for (AssertableConsumer<?> c: consumers) {
            Class<?> accepted = c.getAcceptedAssertableClass();
            if (accepted.isAssignableFrom(required)) {
                ((AssertableConsumer<Assertable>) c).consume(assertable);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public I clearConsumers() {
        consumers.clear();
        return (I) this;
    }

}
