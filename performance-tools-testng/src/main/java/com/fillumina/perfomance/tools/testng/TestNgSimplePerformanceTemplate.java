package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.template.SimplePerformanceTemplate;
import org.testng.annotations.Test;

/**
 * Has some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class TestNgSimplePerformanceTemplate
        extends SimplePerformanceTemplate {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
