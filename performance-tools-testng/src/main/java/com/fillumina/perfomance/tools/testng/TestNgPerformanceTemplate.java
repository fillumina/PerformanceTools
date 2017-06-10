package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.template.PerformanceTemplate;
import org.testng.annotations.Test;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class TestNgPerformanceTemplate
        extends PerformanceTemplate {

    @Test
    public void executeTest() {
        super.executeWithoutOutput();
    }
}
