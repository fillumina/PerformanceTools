package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.DoubleLfsrRunnable;
import com.fillumina.performance.infrastructure.LfsrRunnable;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PerformanceBuilderTest {

    public static void main(final String[] args) {
        new PerformanceBuilderTest().testExec();
    }

    @Test
    public void testExec() {
        PerformanceBuilder.config()
                .speedTestOnly().end()
                .tests()
                    .addTest("single", new LfsrRunnable())
                    .addTest("double", new DoubleLfsrRunnable())
                    .end()
            .end()
            .exec();
    }

}
