package com.fillumina.performance;

import com.fillumina.performance.consumer.assertion.AssertPerformance;
import org.junit.Test;

/**
 *
 * @author fra
 */
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
        Telemetry.segment(START);

        stepOne();
        Telemetry.segment(ONE);

        stepTwo();
        Telemetry.segment(TWO);

        stepThree();
        Telemetry.segment(THREE);
    }

    void stepOne() {
        worksForMills(10); // 50/10 = 20%
    }

    void stepTwo() {
        worksForMills(5); // 50/5 = 10%
    }

    void stepThree() {
        worksForMills(50); // 100%
    }

    void worksForMills(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    @Test
    public void shouldReturnValidResults() {
        Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
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