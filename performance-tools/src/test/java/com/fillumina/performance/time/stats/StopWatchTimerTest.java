package com.fillumina.performance.time.stats;

import com.fillumina.performance.Telemetry;
import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import com.fillumina.performance.util.stats.Ratio;
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

    private Appendable printout;

    public static void main(final String[] args) {
        final StopWatchTimerTest  tt =
                new StopWatchTimerTest();
        tt.printout = System.out;
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
        PerformanceTimeHelper.sleepMicroseconds(200);
    }

    void stepTwo() {
        PerformanceTimeHelper.sleepMicroseconds(100);
    }

    void stepRepeating() {
        PerformanceTimeHelper.sleepMicroseconds(100);
    }

    void stepThree() {
        PerformanceTimeHelper.sleepMicroseconds(1_000);
    }

    @Test
    public void shouldReturnValidResults() {
        for (int i=0; i<ITERATIONS; i++) {
            process();
        }
        timer.getPerformances()
                .getStats(AverageTimeStats.class)
                .appendTo(printout)
                .check(Assertions.withTolerance(Ratio.percentage(5))
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
        Map<TName, SingleTimeStats> map = Telemetry.stopAndGetStats()
                .getStats(AverageTimeStats.class)
                .check(Assertions.withTolerance(Ratio.percentage(8))
                    .assertPercentage(START).sameAs(0)
                    .assertPercentage(TWO).sameAs(10)
                    .assertPercentage(THREE).sameAs(100))
                .getAssertable()
                .getSingleStatsMap();

        assertNull(map.get(TN.tname(ONE)));
        assertNull(map.get(TN.tname(REPEATING)));
    }
}
