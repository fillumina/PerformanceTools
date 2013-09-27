package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.ParametrizedSequencePerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitSequenceSuitePerformanceTemplate<P,S>
        extends ParametrizedSequencePerformanceTemplate<P,S> {

    @Test
    public void executeTestSuite() {
        super.testWithoutOutput();
    }
}
