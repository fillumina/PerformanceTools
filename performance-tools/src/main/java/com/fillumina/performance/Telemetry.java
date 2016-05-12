package com.fillumina.performance;

import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.stats.PerformanceDataCollector;
import com.fillumina.performance.stats.PerformanceStats;

/**
 * Evaluates the percentage of time employed by different parts of a code.
 * It allows to better understand which part of an execution takes the most.
 * Because it uses a {@link ThreadLocal} it can be
 * used in a multi-threaded environment (i.e. in a web server where it can
 * trace a single request against other requests being executed at the same
 * time).
 * <p>
 * The static methods return a {@code boolean}
 * so that they can be used within an assertion
 * which will not by default be executed by the JVM.
 * <pre>
 * assert Telemetry.section("calculation");
 * </pre>
 * By this way the performance code can be left in place without affecting
 * the speed of the final application.
 * The returned value is always {@code true} to make the assertion succeed.
 * <pre>
    public class TelemetryTest {
        private static final int ITERATIONS = 10;
        private static final String START = "START";
        private static final String ONE = "ONE";
        private static final String TWO = "TWO";
        private static final String THREE = "THREE";

        private boolean printout = false;

        public static void main(final String[] args) {
            final TelemetryTest tt = new TelemetryTest();
            tt.printout = true;
            tt.shouldReturnValidResults();
        }

        void process() {
            Telemetry.section(START);

            stepOne();
            Telemetry.section(ONE);

            stepTwo();
            Telemetry.section(TWO);

            stepThree();
            Telemetry.section(THREE);
        }

        void stepOne() {
            worksForMills(20);
        }

        void stepTwo() {
            worksForMills(10);
        }

        void stepThree() {
            worksForMills(100);
        }

        void worksForMills(int millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        &#64;Test
        public void shouldReturnValidResults() {
            Telemetry.init();
            for (int i=0; i&lt;ITERATIONS; i++) {
                Telemetry.start();
                process();
            }
            if (printout) {
                Telemetry.print();
            }
            Telemetry.use(AssertPerformance.withTolerance(5)
                    .assertPercentageFor(START).sameAs(0)
                    .assertPercentageFor(ONE).sameAs(20)
                    .assertPercentageFor(TWO).sameAs(10)
                    .assertPercentageFor(THREE).sameAs(100));
        }
    }
 </pre>
 *
 * @author Francesco Illuminati
 */
public class Telemetry {

    private static final ThreadLocal<InnerTelemetry> THREAD_LOCAL_TELEMETRY =
            new ThreadLocal<>();


    /**
     * Initialize the test. If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init() {
        THREAD_LOCAL_TELEMETRY.set(new InnerTelemetry());
        return true;
    }

    public static boolean start() {
        InnerTelemetry telemetry = THREAD_LOCAL_TELEMETRY.get();
        if (telemetry != null) {
            telemetry.start();
        }
        return true;
    }

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #start()}.
     *
     * @return always true so it can be put on an assert and the code
     *         be removed in production by the compiler.
     */
    public static boolean section(final String name) {
        InnerTelemetry telemetry = THREAD_LOCAL_TELEMETRY.get();
        if (telemetry != null) {
            telemetry.segment(name);
        }
        return true;
    }

    public static PerformanceHolder<PerformanceStats> stop() {
        InnerTelemetry telemetry = THREAD_LOCAL_TELEMETRY.get();
        THREAD_LOCAL_TELEMETRY.set(null);
        if (telemetry != null) {
            return telemetry.stop();
        }
        return PerformanceHolder.empty();
    }

    private static class InnerTelemetry {
        private final PerformanceDataCollector collector =
                new PerformanceDataCollector();
        private PerformanceSample sample;
        private long last;

        void start() {
            if (sample != null) {
                collector.add(sample);
            }
            sample = new PerformanceSample();
            last = System.nanoTime();
        }

        void segment(final String name) {
            final long segment = System.nanoTime() - last;
            sample.add(name, segment, 1);
            last = System.nanoTime();
        }

        PerformanceHolder<PerformanceStats> stop() {
            if (sample != null) {
                collector.add(sample);
                sample = null;
            }
            final PerformanceStats stats =
                    collector.createPerformanceStats(null,true);
            return new PerformanceHolder<>(stats);
        }
    }
}
