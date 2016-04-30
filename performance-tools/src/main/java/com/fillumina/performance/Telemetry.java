package com.fillumina.performance;

import com.fillumina.performance.sample.PerformanceSample;
import com.fillumina.performance.stats.PerformanceDataCollector;
import com.fillumina.performance.stats.PerformanceStatsConsumer;
import com.fillumina.performance.stats.viewer.StringTableStatsViewer;

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
      assert Telemetry.section("calculation");
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
                Telemetry.startIteration();
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
 * </pre>
 * @author Francesco Illuminati
 */
//TODO extends ThreadLocal directly??
@Deprecated // FIXIT not working now
public class Telemetry {

    private static final ThreadLocal<Telemetry> THREAD_LOCAL_TELEMETRY =
            new ThreadLocal<>();

    private final PerformanceSample sample;
    private long last;

    public Telemetry() {
        sample = new PerformanceSample();
        last = System.nanoTime();
    }

    /**
     * Initialize the test. If it is not called all the other calls will
     * be ignored so to be able to run a code normally if it is not under
     * Telemetry.
     *
     * @return always true so that it can be put on an assert
     */
    public static boolean init() {
        THREAD_LOCAL_TELEMETRY.set(new Telemetry());
        return true;
    }

    /** It determines the start of a new iteration. */
    @Deprecated // not used anymore
    public static boolean startIteration() {
//        final Telemetry telemetry = getTelemetry();
//        telemetry.runningPerf.setIterations(++telemetry.iterations);
        return true;
    };

    /**
     * Defines a section by name. It records the time elapsed since the
     * last call to itself or to {@link #startIteration()}.
     *
     * @return always true so it can be put on an assert.
     */
    public static boolean section(final String name) {
        final Telemetry telemetry = THREAD_LOCAL_TELEMETRY.get();
        if (telemetry != null) {
            telemetry.localSegment(name);
        }
        return true;
    }

    /** @return the performances. */
    public static PerformanceSample getLoopPerformances() {
        final Telemetry telemetry = getTelemetry();
        final PerformanceSample performances = telemetry.sample;
//        performances.setIterations(telemetry.iterations);
        return performances;
    }

    /** Makes the <i>consumer</i> consumes the performances. */
    public static void use(final PerformanceStatsConsumer consumer) {
        //consumer.consume("Telemetry", getLoopPerformances());
    }

    /** Prints out the performances in a human readable form. */
    public static void print() {
        System.out.println(THREAD_LOCAL_TELEMETRY.get().toString());
    }

    private void localSegment(final String name) {
        final long nano = getSegmentTime();
        sample.add(name, nano, 1);
    }

    private long getSegmentTime() {
        final long nano = System.nanoTime();
        final long segment = nano - last;
        last = nano;
        return segment;
    }

    /** @return the {@link Telemetry} relative to the current thread. */
    public static Telemetry getTelemetry() {
        final Telemetry telemetry = THREAD_LOCAL_TELEMETRY.get();
        if (telemetry == null) {
            throw new IllegalStateException(
                    "No Telemetry available for this thread");
        }
        return telemetry;
    }

    @Override
    public String toString() {
        PerformanceDataCollector collector = new PerformanceDataCollector(0.95);
        collector.add(sample);
        return StringTableStatsViewer.INSTANCE
                .toStringOutput(collector.createPerformanceStats(true))
                .toString();
    }
}
