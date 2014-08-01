package com.fillumina.perfomance.tools.testng;

import com.fillumina.performance.template.ParametrizedSequencePerformanceTemplate;
import org.testng.annotations.Test;

/**
 * This template adds to each test a parameter and an item of a sequence.
 * <p>
 * The tests are created from the parameters (each new test will have a
 * different parameter and adopt the parameter's name) and there will be many
 * rounds each one named after the name of the test combined with the
 * string representation of the sequence item.
 * <p>
 * The performances returned are the average of the performances over all the
 * items of the sequence while intermediate performances are calculated on the
 * actual sequence item.
 * <p>
 * By this way it is possible to test different {@code Map}s (parameters)
 * with different sizes (sequence).
 * <p>
 * To create the name of the test use the static method
 * {@link #testName(String, Object) }.
 *
 * @author Francesco Illuminati
 */
public abstract class TestNgParametrizedSequencePerformanceTemplate<P,S>
        extends ParametrizedSequencePerformanceTemplate<P,S> {

    @Test
    public void executeTestSuite() {
        super.testWithoutOutput();
    }
}
