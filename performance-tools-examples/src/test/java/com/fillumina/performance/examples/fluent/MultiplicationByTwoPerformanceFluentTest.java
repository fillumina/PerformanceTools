 package com.fillumina.performance.examples.fluent;

import com.fillumina.performance.PerformanceTimerFactory;
import com.fillumina.performance.examples.PrintOut;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.strgen.SampleLineStringGenerator;
import com.fillumina.performance.speed.stats.AssertSpeed;
import com.fillumina.performance.speed.stats.progression.AutoProgressionPerformanceInstrumenter;
import com.fillumina.performance.speed.stats.strgen.SpeedStatsTableStringGenerator;
import com.fillumina.performance.util.LinearFeedbackShiftRegister;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class MultiplicationByTwoPerformanceFluentTest {

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
                            .setTimeout(60, TimeUnit.SECONDS)
                            .setMinConfidence(0.01)
                            .setMaxPercentageMargin(10)
                            .build())

                .addTest("math", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister(16);

                    @Override
                    public Object test() {
                        return lfsr.next() * 2;
                    }
                })

                .addTest("binary", new AbstractTestable() {
                    final LinearFeedbackShiftRegister lfsr =
                            new LinearFeedbackShiftRegister(16);

                    @Override
                    public Object test() {
                        return lfsr.next() << 1;
                    }
                })

                .addPerformanceConsumerIf(display.isPrintOut(),
                        SpeedStatsTableStringGenerator.VIEWER)

                .execute()

                .use(AssertSpeed.withTolerance(10)
                    .assertOrder("binary").sameAs("math"))

                .printIf(display.isPrintOut());
    }
}
