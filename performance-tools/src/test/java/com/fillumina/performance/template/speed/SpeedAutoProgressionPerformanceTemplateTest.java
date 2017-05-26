package com.fillumina.performance.template.speed;

import com.fillumina.performance.template.Configuration;
import com.fillumina.performance.template.MixedAssertion;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SpeedAutoProgressionPerformanceTemplateTest
        extends PerformanceTemplate {

    public static void main(final String[] args) {
        new SpeedAutoProgressionPerformanceTemplateTest()
                .executeWithMediumOutput();
    }

    @Test
    public void executeTest() {
        new SpeedAutoProgressionPerformanceTemplateTest()
                .executeWithoutOutput();
    }

    @Override
    public void config(Configuration configuration) {
        configuration
                .setName("AutoProgressionPerformanceTemplateTest")
                .speedTestOnly()
                    .setSamples(30);
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("half", new Runnable() {

            @Override
            public void run() {
                PerformanceTimeHelper.sleepMicroseconds(100);
            }
        });

        tests.addTest("full", new Runnable() {

            @Override
            public void run() {
                PerformanceTimeHelper.sleepMicroseconds(200);
            }
        });
    }

    @Override
    public void addAssertions(MixedAssertion assertion) {
        assertion.speed().tolerance(Ratio.percentage(10))
                .assertOrder("half").lessThan("full");
    }
}
