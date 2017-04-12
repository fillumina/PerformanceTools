package com.fillumina.performance.template.speed;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.infrastructure.Testable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.testable.TimeTestable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinglePerformanceTemplateTest
        extends PerformanceTemplate {


    public static void main(final String[] args) {
        new SinglePerformanceTemplateTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        executeWithoutOutput();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertions) {
    }

    @Override
    public void config(TestConfiguration config) {
        config.speedTestOnly()
                .setSamples(5);
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
//        tests.addTest("test", new LfsrTest());
        tests.addTest("test", new TimeTestable(5));
    }
}
