package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LfsrTestableTest extends PerformanceTemplate {

    public static void main(final String[] args) {
        new LfsrTestableTest().executeWithFullOutput();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertions) {

    }

    @Override
    public void config(TestConfiguration config) {
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("lfsr", new LfsrTestable());
        tests.addTest("null", Testable.DO_NOTHING);
    }
}
