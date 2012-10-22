package com.fillumina.performance.consumer.assertion;

import com.fillumina.performance.producer.timer.LoopPerformancesCreator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
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

        assertion.consume("First Object", LoopPerformancesCreator.parse(10,
                new Object[][] {
                    {"First", 10},
                    {"Full", 100}
                }));

        assertion.consume("Second Object", LoopPerformancesCreator.parse(100,
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

        assertion.consume("First Object", LoopPerformancesCreator.parse(10,
                new Object[][] {
                    {"First", 10},
                    {"Full", 100}
                }));

        try {
            assertion.consume("Second Object", LoopPerformancesCreator.parse(100,
                new Object[][] {
                    {"First", 30},
                    {"Full", 100}
                }));
            fail();
        } catch (AssertionError e) {
            assertEquals("Second Object 'First' expected equals to 20.00 %, " +
                    "found 30.00 % with a tolerance of 7.0 %",
                    e.getMessage());
        }
    }
}
