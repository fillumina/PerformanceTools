package com.fillumina.performance.time.stats;

import com.fillumina.performance.Telemetry;
import com.fillumina.performance.assertion.Assertions;
import com.fillumina.performance.executor.PN;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.AccurateSleeper;
import com.fillumina.performance.util.Looper;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import java.util.Map;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StopWatchTimerTest {
    private static final int ITERATIONS = 500;
    private static final String START = "start";
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String REPEATING = "repeating";
    private static final String THREE = "three";

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
        Looper.loop(ITERATIONS, () -> process() );

        timer.getPerformances()
                .appendTo(printout)
                .getStatsHolder(TimeStatsType.AVERAGE)
                .check(Assertions.withTolerance(Ratio.percentage(5))
                    //.assertPercentage(START).equalsTo(0)
                    .assertPercentage(ONE).equalsTo(20)
                    .assertPercentage(TWO).equalsTo(10)
                    .assertPercentage(REPEATING).equalsTo(10)
                    .assertPercentage(THREE).equalsTo(100));
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
        Map<PathName, DimensionalMeasure> map = Telemetry.stopAndGetStats()
                .getStatsHolder(TimeStatsType.AVERAGE)
                .check(Assertions.withTolerance(Ratio.percentage(10))
                    .assertPercentage(START).equalsTo(0)
                    .assertPercentage(TWO).equalsTo(10)
                    .assertPercentage(THREE).equalsTo(100))
                .getStats()
                .getMeasureMap();

        assertNull(map.get(PN.pname(ONE)));
        assertNull(map.get(PN.pname(REPEATING)));
    }
}
