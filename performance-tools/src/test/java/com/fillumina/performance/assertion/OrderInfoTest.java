package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.RelativeOrder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureMock;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map;
import java.util.NoSuchElementException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class OrderInfoTest {

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
        final Assertions assertion =
                Assertions.withTolerance(Ratio.P_10)
                       .assertOrder("Second").lessThan("First");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ExperimentAssertionError ex) {
            AbstractAssertionErrorInfo<?> e =
                    (AbstractAssertionErrorInfo<?>) ex.getInfo();

            assertEquals(RelativeOrder.LESS, e.getRelativeOrder());

            checkValues(e, "Second", 66, "First", 33, Ratio.P_10);

            return;
        }
        fail();
    }

    @Test
    public void shouldExceptionGiveInfoWithNegateAssertion() {
        final Assertions speedAssertion =
                Assertions.withTolerance(Ratio.ZERO)
                       .assertOrder("Second").lessThanOrEquals("First");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            speedAssertion.check(assertable);

        } catch (ExperimentAssertionError ex) {
            AbstractAssertionErrorInfo<?> e =
                    (AbstractAssertionErrorInfo<?>) ex.getInfo();

            // !(a <= b) is the same as (a > b)
            assertEquals(RelativeOrder.GREATER, e.getRelativeOrder());

            checkValues(e, "Second", 66, "First", 33, e.getTolerance());
            return;
        }
        fail();
    }

    @Test
    public void shouldBeLessThanWithTolerance10() {
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
    public void shouldNotBeLessThanWithLowTolerance() {
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
    public void shouldProduceWhatIfMap() {
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
        } catch (ExperimentAssertionError ex) {
            AbstractAssertionErrorInfo<?> e =
                    (AbstractAssertionErrorInfo<?>) ex.getInfo();

            assertEquals(RelativeOrder.GREATER, e.getRelativeOrder());

            checkValues(e, "First", 33, "Second", 66, e.getTolerance());

            Map<RelativeOrder,Ratio> whatIfMap = e.getWhatIfToleranceMap();
            assertEquals(1.01, whatIfMap.get(RelativeOrder.GREATER).getDecimal(), 0);
            assertEquals(1.01, whatIfMap.get(RelativeOrder.EQUALS).getDecimal(), 0);
            assertNull(whatIfMap.get(RelativeOrder.LESS));
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                        .assertOrder("First").equalsTo("Second");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
            fail();
        } catch (ExperimentAssertionError ex) {
            AbstractAssertionErrorInfo<?> e =
                    (AbstractAssertionErrorInfo<?>) ex.getInfo();

            assertEquals(RelativeOrder.EQUALS, e.getRelativeOrder());

            checkValues(e, "First", 33, "Second", 66, e.getTolerance());
        }
    }

    @Test
    public void shouldReportNonExistentTest() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
                        .assertOrder("First").equalsTo("NonExistent");

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
            fail();
        } catch (MeasureNotFoundException e) {
            assertEquals("measure 'NonExistent' not found, " +
                    "valid names are: [First, Second, Top]",
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
        } catch (ExperimentAssertionError ex) {
            AbstractAssertionErrorInfo<?> e =
                    (AbstractAssertionErrorInfo<?>) ex.getInfo();

            assertEquals(RelativeOrder.LESS, e.getRelativeOrder());

            checkValues(e, "Second", 66, "First", 33, e.getTolerance());
        }
    }

    private void checkValues(AbstractAssertionErrorInfo<?> e,
            String firstName, double firstMean,
            String secondName, double secondMean,
            final Ratio tolerance)
            throws NoSuchElementException {
        assertEquals(firstName, e.getFirstTestName().toString());

        final String secondTestName = e.getAssertionValue().toString();
        assertEquals(secondName, secondTestName);

        final AssertableExperiment a = e.getAssertable();
        assertEquals(firstMean, a.getMeasure(e.getFirstTestName()).getMean(), 1E-3);
        assertEquals(secondMean, a.getMeasure(secondTestName).getMean(), 1E-3);
        assertEquals(tolerance, e.getTolerance());
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
