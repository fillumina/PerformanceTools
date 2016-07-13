package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.formatter.PerformanceTimeHelper;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ShouldNoConfigMeansAllTest
        extends AutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new ShouldNoConfigMeansAllTest().executeWithFullOutput();
    }

    @Test
    public void shouldExecuteTest() {
        new ShouldNoConfigMeansAllTest().executeWithoutOutput();
    }

    @Override
    public void addAssertions(ProgressionAssertion assertion) {
    }

    @Override
    public void config(TestConfiguration configuration) {
        // left empty
    }

    @Override
    public void addTests(TestContainer<Testable> tests) {
        tests.addTest("test", new AbstractTestable() {
            @Override
            public Object test() {
                PerformanceTimeHelper.sleepMicroseconds(1);
                return true;
            }
        });
    }

}
