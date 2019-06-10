package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import com.fillumina.performance.mock.SettableAssertionMock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExperimentAssertionTest {

    @Test
    public void shouldCheckAnAssertable() {
        ExperimentAssertion assertion = new AssertionMock();
        AssertableExperiment assertable = new AssertableMock();
        assertion.check(assertable);
    }

    @Test
    public void shouldReportTheFailingAssertion() {
        ExperimentAssertion assertion = new SettableAssertionMock(a -> {
            throw new AssertionError();
        });

        final AssertableMock assertable = new AssertableMock("one");

        Map<AssertableExperiment, List<ExperimentAssertion>> failedAssertions =
                new LinkedHashMap<>();

        UnusedAssertionChecker unusedAssertion = new UnusedAssertionChecker();
        assertion.checkAndReport(assertable, failedAssertions, unusedAssertion);

        assertEquals(0, unusedAssertion.getUnusedAssertionList().size());
        assertEquals(1, failedAssertions.size());
        assertEquals(assertion, failedAssertions.get(assertable).get(0));
    }

    @Test
    public void shouldReportTheNotFoundAssertion() {
        ExperimentAssertion assertion = new SettableAssertionMock(a -> {
            throw new MeasureNotFoundException("not found");
        });
        final AssertableMock assertable = new AssertableMock("one");
        Map<AssertableExperiment, List<ExperimentAssertion>> failedAssertions =
                new LinkedHashMap<>();
        UnusedAssertionChecker unusedAssertion = new UnusedAssertionChecker();
        assertion.checkAndReport(assertable, failedAssertions, unusedAssertion);

        assertEquals(0, failedAssertions.size());

        assertEquals(1, unusedAssertion.getUnusedAssertionList().size());
        assertEquals(assertion, unusedAssertion.getUnusedAssertionList().get(0));
    }

}
