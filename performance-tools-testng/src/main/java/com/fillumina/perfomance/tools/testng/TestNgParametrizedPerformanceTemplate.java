package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.template.ParametrizedPerformanceTemplate;
import org.testng.annotations.Test;

/**
 * It works just like the
 * {@link com.fillumina.performance.template.AutoProgressionPerformanceTemplate}
 * but it allows to add a parameter to each test. This means that you can run
 * the same code against different objects and so automatically
 * creating different tests. (i.e. you can test the relative speed of different
 * type of {@code List}s by using the same test code and passing different
 * type of list to it).
 * <p>
 * Differently from
 * {@link com.fillumina.performance.template.AutoProgressionPerformanceTemplate}
 * each test is executed on the spot (where it is created) and the results
 * returned so there isn't a global {@code execute()} code for all tests.
 * To discriminate between different tests each has a name that can
 * be used in {@code assertion.forExecution(TEST_NAME)} and each parameter
 * is named too:
 * <pre>
 * assertion.forExecution(<b>TEST_NAME</b>).
 *       .assertPercentageFor(<b>PARAMETER_NAME</b>).sameAs(<b>PERCENTAGE</b>);
 * </pre>
 *
 * @author Francesco Illuminati
 */
public abstract class TestNgParametrizedPerformanceTemplate<T>
        extends ParametrizedPerformanceTemplate<T> {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
