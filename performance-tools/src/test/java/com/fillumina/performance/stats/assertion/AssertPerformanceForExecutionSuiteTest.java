package com.fillumina.performance.stats.assertion;

import com.fillumina.performance.stats.FakePerformanceCreator;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertPerformanceForExecutionSuiteTest {

    @Test
    public void shouldCheckTheAssertions() {
        final AssertPerformanceForExecutionSuite assertion =
                new AssertPerformanceForExecutionSuite();

        assertion.forExecution("First Object")
                .assertPercentageFor("First").sameAs(10F);

        assertion.forExecution("Second Object")
                .assertPercentageFor("First").sameAs(20F);

        assertion.consume("First Object", FakePerformanceCreator.createStats(10,
                new Object[][] {
                    {"First", 10},
                    {"Full", 100}
                }));

        assertion.consume("Second Object", FakePerformanceCreator.createStats(100,
                new Object[][] {
                    {"First", 20},
                    {"Full", 100}
                }));
    }

    @Test
    public void shouldRiseAnAssertionErrorIfNotMatching() {
        final AssertPerformanceForExecutionSuite assertion =
                new AssertPerformanceForExecutionSuite();

        assertion.forExecution("First Object")
                .assertPercentageFor("First").sameAs(10F);

        assertion.forExecution("Second Object")
                .assertPercentageFor("First").sameAs(20F);

        assertion.consume("First Object", FakePerformanceCreator.createStats(10,
                new Object[][] {
                    {"First", 10},
                    {"Full", 100}
                }));

        try {
            assertion.consume("Second Object", FakePerformanceCreator.createStats(100,
                new Object[][] {
                    {"First", 30},
                    {"Full", 100}
                }));
            fail();
        } catch (AssertionError e) {
            assertEquals("Second Object 'First' expected equals to 20.00 %, " +
                    "found 30.00000 ± 0.00000 % (confidence 99.9000 %) " +
                    "with a tolerance of 7.0 %",
                    e.getMessage());
        }
    }
}
