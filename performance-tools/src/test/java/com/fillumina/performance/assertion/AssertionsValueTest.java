package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.NormalDistributionMeasureBuilder;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionsValueTest {

    @Test
    public void shouldConfirmTheExpectedPercentages() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
            .assertValue("First").equalsTo(33)
            .assertValue("Second").equalsTo(66);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        assertion.check(assertable);
    }

    @Test
    public void shouldNotBeGreater() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.ZERO)
            .assertValue("First").greaterThan(50);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeLesser() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.percentage(1))
            .assertValue("First").lessThan(10F);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    @Test
    public void shouldNotBeEquals() {
        final Assertions assertion =
                Assertions.withTolerance(Ratio.percentage(1))
            .assertValue("First").equalsTo(10F);

        final AssertableMock assertable =
                AssertableMock.create(
                        "First", 33,
                        "Second", 66,
                        "Top", 100);

        try {
            assertion.check(assertable);
        } catch (ValueAssertionError e) {
            assertEquals("First", e.getTestName().toString());
            assertEquals(33, e.getActualValue().getMean(), 1E-3);
            assertEquals(1.0, e.getTolerance().getPercentage(), 0);
            return;
        }
        fail();
    }

    public static void main(final String[] args) {
        Measure value = new NormalDistributionMeasureBuilder(10.0, 3.5, 0.1, 33)
                .build();

        for (double confidence = 0; confidence < 1; confidence += .1) {
            System.out.println("" + confidence + " -> " +
                    value.toStringForConfidence(Ratio.decimal(confidence)));
        }
    }
}
