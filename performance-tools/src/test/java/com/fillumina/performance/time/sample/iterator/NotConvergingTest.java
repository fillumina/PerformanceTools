package com.fillumina.performance.time.sample.iterator;

import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.PerformanceBuilder;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class NotConvergingTest {

    public static void main(final String[] args) {

        /**
         * This is a normal class featuring no protection against concurrent
         * accesses.
         */
        class State {
            private double x = Math.PI;

            private double increment() {
                return x++;
            }
        }

        /**
         * That's what jmh's {@code @State(Scope.Benchmark)} is really doing:
         * the access to the method is serialized.
         */
        class GuardedState {
            private double x = Math.PI;

            private synchronized double increment() {
                return x++;
            }
        }

        final GuardedState safelyShared = new GuardedState();

        /**
         * This status is thread local (which is what JMH's
         * {@link org.openjdk.jmh.annotations.Scope#Thread} does.
         */
        final ThreadLocal<State> threadLocal =
                new ThreadLocal<State>() {
            @Override
            protected State initialValue() {
                return new State();
            }

        };

        // TODO fix this test doesn't end
        PerformanceBuilder
                .config()
                    .speedConfig()
                        .setMultiThreading(true)
                    .end()
                .tests()
                    .addTest("synchronized", () -> {
                            Sink.drain(safelyShared.increment());
                        })
                    .addTest("unsafe", new Runnable() {
                            /**
                             * This is NOT private state
                             * (all threads share the same field unsafely).
                             */
                            private final State shared = new State();
                            @Override
                            public void run() {
                                Sink.drain(shared.increment());
                            }
                        })
                    .addTest("thread local", () -> {
                            Sink.drain(threadLocal.get().increment());
                        })
                    .end()
                .end()
            .executeWithFullOutput();
    }

}
