package com.fillumina.performance;

import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.AccurateSleeper;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class TelemetryTest {
    private static final int WARMUP = 1;
    private static final int ITERATIONS = 10;
    private static final String START = "START";
    private static final String ONE = "ONE";
    private static final String TWO = "TWO";
    private static final String REPEATING = "REPEATING";
    private static final String THREE = "THREE";

    private Appendable printout = null;

    public static void main(final String[] args) {
        final TelemetryTest tt = new TelemetryTest();
        tt.printout = System.out;
        tt.shouldReturnValidResults();
    }

    void process() {
        Telemetry.start();

        Telemetry.section(START);

        stepOne();
        Telemetry.section(ONE);

        stepTwo();
        Telemetry.section(TWO);

        for (int i=0; i<10; i++) {
            stepRepeating();
        }
        Telemetry.section(REPEATING, 10);

        stepThree();
        Telemetry.section(THREE);
    }

    void stepOne() {
        AccurateSleeper.sleepMicroseconds(200);
    }

    void stepTwo() {
        AccurateSleeper.sleepMicroseconds(100);
    }

    void stepRepeating() {
        AccurateSleeper.sleepMicroseconds(100);
    }

    void stepThree() {
        AccurateSleeper.sleepMicroseconds(1_000);
    }

    @Test
    public void shouldReturnValidResults() {
        Telemetry.clear();
        Telemetry.init();
        // warmp up
        for (int i=0; i<WARMUP; i++) {
            process();
        }
        Telemetry.reset();
        // iterations
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        MixedStatsHolder result = Telemetry.stopAndGetStats();
        result.getStatsHolder(TimeStatsType.AVERAGE)
                .appendTo(printout)
                .check(Assertions.withTolerance(Ratio.percentage(5))
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(ONE).sameAs(20)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(REPEATING).sameAs(10)
                    .assertPercentage(THREE).sameAs(100));

        result.getStatsHolder(TimeStatsType.THROUGHPUT)
                .appendTo(printout);
    }

    @Test
    public void shouldNotWorkAtAllIfNotInitialized() {
        Telemetry.clear();
        //Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        assertTrue(Telemetry.stopAndGetStats() == MixedStatsHolder.EMPTY);
    }

    void alternateProcess() {
        Telemetry.start();

        Telemetry.section(START);

        stepTwo();
        Telemetry.section(TWO);

        stepThree();
        Telemetry.section(THREE);
    }

    @Test
    public void shouldNotAccountForAMissingTest() {
        Telemetry.clear();
        Telemetry.init();
        for (int i=0; i<WARMUP; i++) {
            alternateProcess();
        }
        Telemetry.reset();
        for (int i=0; i<ITERATIONS; i++) {
            alternateProcess();
        }
        MixedStatsHolder holder = Telemetry.stopAndGetStats();
        Map<TName, DimensionalMeasure> map = holder
                .getStatsHolder(TimeStatsType.AVERAGE)
                .check(Assertions.withTolerance(Ratio.percentage(5))
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(THREE).sameAs(100))
                .getStats()
                .getMeasureMap();

        assertNull(map.get(TN.tname(ONE)));
        assertNull(map.get(TN.tname(REPEATING)));
    }

    @Test
    public void shouldClearTheTelemetry() {
        Telemetry.clear();
        Telemetry.init();
        Telemetry.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        Telemetry.section("end");

        assertEquals(1, Telemetry.getStatsFromAllThreads().size());

        Telemetry.clear();

        assertEquals(0, Telemetry.getStatsFromAllThreads().size());
    }

    @Test
    public void shouldRecordStatsForConcurrentThreads() throws InterruptedException {
        Telemetry.clear();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable worker = () -> {
            Telemetry.init();
            Telemetry.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            Telemetry.section("end");
        };
        executor.execute(worker);
        executor.execute(worker);

        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        // Wait until all threads are finish

        executor.awaitTermination(130, TimeUnit.MILLISECONDS);

        Map<String,MixedStatsHolder> map = Telemetry.getStatsFromAllThreads();

        assertEquals(2, map.size());
    }
}