package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.EqCondition;
import com.fillumina.performance.util.stats.MeasureMock;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class AssertionsOrderTest {

    @Test
    public void shouldConfirmTheExpectedOrder() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        assertion.check(assertable);
    }

    @Test
    public void shouldExceptionGiveInfo() {
        final Assertions speedAssertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("Second").lessThan("First");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            speedAssertion.check(assertable);
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.LESS, e.getCondition());
            assertEquals("Second", e.getFirstTestName().toString());
            assertEquals("First", e.getSecondTestName().toString());
            assertEquals(33, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(66, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 1E-3);
            return;
        }
        fail();
    }

    @Test
    public void shouldBeFasterWithTolerance10() {
        final Assertions highToleranceAssertion =
                Assertions.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 109,
                        "Second", 100);

        highToleranceAssertion.check(assertable);
    }

    @Test
    public void shouldNotBeFasterWithLowTolerance() {
        final Assertions lowToleranceAssertion =
                Assertions.withTolerance(Ratio.percentage(10))
                    .assertOrder("First").lessThan("Second");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 110,
                        "Second", 100);

        try {
            lowToleranceAssertion.check(assertable);
            fail();
        } catch (AssertionError e) {

        }
    }

    @Test
    public void shouldNotBeSlower() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("First").greaterThan("Second");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.GREATER, e.getCondition());
            assertEquals("First", e.getFirstTestName().toString());
            assertEquals("Second", e.getSecondTestName().toString());
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 1E-3);

            Map<EqCondition,Ratio> whatIfMap = e.getWhatIfToleranceMap();
            assertEquals(1.01, whatIfMap.get(EqCondition.GREATER).getDecimal(), 0);
            assertEquals(1.01, whatIfMap.get(EqCondition.EQUALS).getDecimal(), 0);
            assertNull(whatIfMap.get(EqCondition.LESS));
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("Second");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
            fail();
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.EQUALS, e.getCondition());
            assertEquals("Second", e.getSecondTestName().toString());
            assertEquals("First", e.getFirstTestName().toString());
            assertEquals(66, e.getSecondMeasure().getMean(), 1E-3);
            assertEquals(33, e.getFirstMeasure().getMean(), 1E-3);
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("First").sameAs("NonExistent");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
            fail();
        } catch (TestNotFoundException e) {
            assertEquals("test 'NonExistent' not found, " +
                    "valid tests are: [First, Second, Top]",
                    e.getMessage());
        }
    }

    @Test
    public void shouldCheckTwoTestsSimultaneously() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("Top");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldFailSecondTest() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                    .assertOrder("First").lessThan("Second")
                    .assertOrder("Second").lessThan("First");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
            fail("second test should fail");
        } catch (OrderAssertionError e) {
            assertEquals(EqCondition.LESS, e.getCondition());
            assertEquals("Second", e.getFirstTestName().toString());
            assertEquals("First", e.getSecondTestName().toString());
            assertEquals(66, e.getFirstMeasure().getMean(), 1E-3);
            assertEquals(33, e.getSecondMeasure().getMean(), 1E-3);
        }
    }

    private static class MeasureImpl extends MeasureMock {
        private final double standardError;

        MeasureImpl(double mean, double standardError) {
            this.mean = mean;
            this.standardError = standardError;
        }

        @Override
        public double getStandardError() {
            return standardError;
        }
    }

    public static void main(final String[] args) {
        Measure firstMeasure =
                new MeasureImpl(5.34653740395317, 0.03210949319450669);

        System.out.println("0.95: " + firstMeasure.getConfidenceInterval(Ratio.P_95));
        System.out.println("0.05: " + firstMeasure.getConfidenceInterval(Ratio.decimal(0.05)));
        System.out.println("0.00: " + firstMeasure.getConfidenceInterval(Ratio.ZERO));
    }
}
