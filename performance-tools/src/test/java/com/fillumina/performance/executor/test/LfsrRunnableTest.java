package com.fillumina.performance.executor.test;

import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.template.MixedAssertionBuilder;
import com.fillumina.performance.template.MixedConfigurationBuilder;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.MemUnit;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrRunnableTest extends PerformanceTemplate {

    public static void main(final String[] args) {
        new LfsrRunnableTest().executeWithFullOutput();
    }

    @Override
    public void addAssertions(MixedAssertionBuilder<?> assertions) {
        assertions.tolerance(Ratio.ZERO)
            .avgTime().order("lfsr").lessThan("counter").end()
            .allocatedMemory().value("lfsr").equalsTo(MemUnit.B.quantity(0)).end()
            .usedMemory().value("lfsr").equalsTo(MemUnit.B.quantity(0)).end();
    }

    @Override
    public void config(MixedConfigurationBuilder<?> config) {
    }

    @Override
    public void addTests(TestConfiguration<?> tests) {
        tests.addTest("lfsr", new LfsrRunnable());
        tests.addTest("counter", new Runnable() {
            private volatile int counter;
            @Override
            public void run() {
                Sink.drain(counter++);
            }
        });
    }
}
