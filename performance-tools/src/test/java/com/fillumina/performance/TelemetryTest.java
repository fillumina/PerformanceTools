package com.fillumina.performance;

import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.TestPerformance;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.Map;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class TelemetryTest {
    private static final int ITERATIONS = 100;
    private static final String START = "START";
    private static final String ONE = "ONE";
    private static final String TWO = "TWO";
    private static final String REPEATING = "REPEATING";
    private static final String THREE = "THREE";

    private boolean printout = false;

    public static void main(final String[] args) {
        final TelemetryTest tt = new TelemetryTest();
        tt.printout = true;
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
        PerformanceTimeHelper.sleepMicroseconds(20);
    }

    void stepTwo() {
        PerformanceTimeHelper.sleepMicroseconds(10);
    }

    void stepRepeating() {
        PerformanceTimeHelper.sleepMicroseconds(10);
    }

    void stepThree() {
        PerformanceTimeHelper.sleepMicroseconds(100);
    }

    @Test
    public void shouldReturnValidResults() {
        Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        Telemetry.stop()
                .printIf(printout)
                .check(AssertSpeed.withTolerance(5)
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(ONE).sameAs(20)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(REPEATING).sameAs(10)
                    .assertPercentage(THREE).sameAs(100));
    }

    @Test
    public void shouldNotWorkAtAllIfNotInitialized() {
        //Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        assertTrue(Telemetry.stop().isEmpty());
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
        Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
            alternateProcess();
        }
        Map<String, TestPerformance> map = Telemetry.stop()
                .check(AssertSpeed.withTolerance(5)
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(THREE).sameAs(100))
                .getTree()
                .getPerformances();

        assertNull(map.get(ONE));
        assertNull(map.get(REPEATING));
    }
}