package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceBuilder;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class OperationTest {

    @Test
    public void shouldNotExecuteNullTest() {
        PerformanceBuilder
                .config()
                    .tests()
                        .addTest(null)
                    .end()
                .end()
                .executeWithFullOutput();
    }
}
