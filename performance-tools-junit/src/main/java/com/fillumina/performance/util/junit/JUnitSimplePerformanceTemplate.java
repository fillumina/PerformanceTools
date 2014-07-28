package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.SimplePerformanceTemplate;
import org.junit.Test;

/**
 * Has some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class JUnitSimplePerformanceTemplate
        extends SimplePerformanceTemplate {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
