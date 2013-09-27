package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.ParametrizedPerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitSuitePerformanceTemplate<T>
        extends ParametrizedPerformanceTemplate<T> {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
