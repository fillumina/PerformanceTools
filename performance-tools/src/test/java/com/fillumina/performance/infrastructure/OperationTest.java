package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceBuilder;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
@Deprecated
public class OperationTest {

    @Ignore @Test
    public void shouldNotExecuteNullTest() {
        PerformanceBuilder
                .config()
                    .tests()
                        .addTest(new LfsrRunnable())
                    .end()
                .end()
                .executeWithFullOutput();
    }
}
