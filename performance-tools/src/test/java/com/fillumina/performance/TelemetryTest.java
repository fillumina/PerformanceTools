package com.fillumina.performance;

import com.fillumina.performance.stats.assertion.AssertPerformance;
import com.fillumina.performance.util.PerformanceTimeHelper;
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

        stepThree();
        Telemetry.section(THREE);
    }

    void stepOne() {
        PerformanceTimeHelper.sleepMicroseconds(20);
    }

    void stepTwo() {
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
                .use(AssertPerformance.withTolerance(5)
                    .assertPercentageFor(START).sameAs(0)
                    .assertPercentageFor(ONE).sameAs(20)
                    .assertPercentageFor(TWO).sameAs(10)
                    .assertPercentageFor(THREE).sameAs(100));
    }

    @Test
    public void shouldNotWorkAtAllIfNotInitialized() {
        //Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        assertTrue(Telemetry.stop().isEmpty());
    }
}