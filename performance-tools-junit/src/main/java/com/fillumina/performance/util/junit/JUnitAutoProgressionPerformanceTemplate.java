package com.fillumina.performance.util.junit;

import com.fillumina.performance.template.AutoProgressionPerformanceTemplate;
import org.junit.Test;

/**
 * Configures an auto progression performance test that will iterate over
 * tests until a required stability of result is met.
 * <p>
 * The test is performed in <b>rounds</b>. For each round the given number of
 * <b>{@code samples}</b> are taken each consisting on a measure of the time
 * employed by the tests repeated for a number of <b>{@code iterations}</b>.
 * If the {@samples} measures have a standard deviation over the maximum allowed
 * than the iteration number is increased and a new round is executed.
 * At the first round the iteration number
 * is set to {@code baseIterations} and it is increased in the successive rounds
 * by a power of 10.
 * <p>
 * By this way the algorithm
 * will have at each round {@code sample} measurements to determine if the
 * variance between the {@code sample}s is less than the maximum standard
 * deviation allowed. If it is the test is stopped and the
 * <b>average results</b> of the last round is returned.
 * This means that the test guarantee to return the
 * performances within a specified stability value.
 * If the target stability is not met within a specified timeout the test
 * fails.
 * <p>
 * Keep the maximum allowed standard deviation high enough to prove your point
 * (especially in unit tests that must be executed fast) and lower it
 * when you need more precise results.
 *
 * @author Francesco Illuminati
 */
public abstract class JUnitAutoProgressionPerformanceTemplate
        extends AutoProgressionPerformanceTemplate {

    @Test
    public void executeTest() {
        super.testWithoutOutput();
    }
}
