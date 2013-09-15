package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.SimplePerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitSimplePerformanceTemplate
        extends SimplePerformanceTemplate {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
