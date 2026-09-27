package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.executor.test.Sink;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.util.rnd.Lfsr;
import com.fillumina.performance.util.stats.Ratio;

/**
 * Smoke test for the TestNG template, which until now had no test at all.
 * <p>
 * It runs the smallest comparison that still exercises the whole template
 * path: configuration, test declaration, assertion and reporting. The sample
 * count is deliberately low so that the ordinary build stays quick, and the
 * tolerance is wide because a bare template test on a shared machine is not
 * a measurement of anything.
 *
 * @author Francesco Illuminati
 */
public class TestNgTemplateTest extends TestNgPerformanceTemplate {

    private volatile int x = 1;
    private final Lfsr lfsr = new Lfsr();

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
        config.speedConfig()
                .setFixedSamples(3)
                .end();
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("minimum", () -> Sink.drain(x));
        tests.addTest("lfsr", () -> Sink.drain(lfsr.next()));
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.avgTime()
                .tolerance(Ratio.percentage(50))
                .order("minimum").lessThan("lfsr").end();
    }
}
