 package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.WrapperSpeedStatsTableStringGenerator;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceFluentTest {
    private static final String BINARY = "binary";
    private static final String MATH = "math";

    private PrintOut display = new PrintOut();

    public static void main(final String[] args) {
        final MultiplicationByTwoPerformanceFluentTest test =
                new MultiplicationByTwoPerformanceFluentTest();
        test.display = new PrintOut(true);
        test.executeTest();
    }

    @Test
    public void executeTest() {

        PerformanceTimerFactory.createSingleThreaded()
                .addPerformanceConsumerIf(display.isPrintOut(),
                        SampleLineStringGenerator.VIEWER)

                .instrumentedBy(AutoProgressionPerformanceInstrumenter.builder()
                            .setName("Multiplication By Two - fluent")
                            .setMaxPercentageMargin(10)
                            .build())

                .addTest(MATH, new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister(16);

                    @Override
                    public Object test() {
                        return lfsr.next() * 2;
                    }
                })

                .addTest(BINARY, new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister(16);

                    @Override
                    public Object test() {
                        return lfsr.next() << 1;
                    }
                })

                .addPerformanceConsumerIf(display.isPrintOut(),
                        WrapperSpeedStatsTableStringGenerator.VIEWER)

                .execute()

                .check(AssertSpeed.withTolerance(Ratio.percentage(10))
                    .assertOrder(BINARY).sameAs(MATH))

                .printIf(display.isPrintOut());
    }
}
