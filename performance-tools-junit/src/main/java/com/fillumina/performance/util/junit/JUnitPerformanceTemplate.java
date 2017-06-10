package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.PerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public abstract class JUnitPerformanceTemplate
        extends PerformanceTemplate {

    @Test
    public void executeTest() {
        super.executeWithoutOutput();
    }
}
