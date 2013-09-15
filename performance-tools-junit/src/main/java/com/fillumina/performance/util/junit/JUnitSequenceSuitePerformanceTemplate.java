package com.fillumina.performance.util.junit;

import com.fillumina.performance.templates.SequenceSuitePerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitSequenceSuitePerformanceTemplate<P,S>
        extends SequenceSuitePerformanceTemplate<P,S> {

    @Test
    public void executeTestSuite() {
        super.testWithoutOutput();
    }
}
