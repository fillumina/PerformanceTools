package com.fillumina.performance.util.junit;

import com.fillumina.performance.templates.SuitePerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitSuitePerformanceTemplate<T>
        extends SuitePerformanceTemplate<T> {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
