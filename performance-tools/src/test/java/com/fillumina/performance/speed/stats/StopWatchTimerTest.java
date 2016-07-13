package com.fillumina.performance.speed.stats;

import com.fillumina.performance.Telemetry;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import java.util.Map;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimerTest {
    private static final int ITERATIONS = 100;
    private static final String START = "START";
    private static final String ONE = "ONE";
    private static final String TWO = "TWO";
    private static final String REPEATING = "REPEATING";
    private static final String THREE = "THREE";

    private boolean printout = false;

    public static void main(final String[] args) {
        final StopWatchTimerTest  tt =
                new StopWatchTimerTest();
        tt.printout = true;
        tt.shouldReturnValidResults();
    }

    private StopWatchTimer timer = new StopWatchTimer();

    void process() {
        timer.start();

        timer.section(START);

        stepOne();
        timer.section(ONE);

        stepTwo();
        timer.section(TWO);

        for (int i=0; i<10; i++) {
            stepRepeating();
        }
        timer.section(REPEATING, 10);

        stepThree();
        timer.section(THREE);
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
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        timer.getPerformance()
                .printIf(printout)
                .check(AssertSpeed.withTolerance(5)
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(ONE).sameAs(20)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(REPEATING).sameAs(10)
                    .assertPercentage(THREE).sameAs(100));
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
                .getPerformance()
                .getPerformances();

        assertNull(map.get(ONE));
        assertNull(map.get(REPEATING));
    }
}
