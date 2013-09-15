package com.fillumina.performance.util.junit;

import com.fillumina.performance.templates.AutoProgressionPerformanceTemplate;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public abstract class JUnitAutoProgressionPerformanceTemplate
        extends AutoProgressionPerformanceTemplate {

    @Test
    public void executeTest() {
        super.testWithOutput();
    }
}
