package com.fillumina.performance.mock;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.TestNotFoundException;
import com.fillumina.performance.assertion.UnusedAssertionChecker;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionTest {

    @Test
    public void shouldReportTheFailingAssertion() {
        Assertion assertion = new SettableAssertionMock(a -> {
            throw new AssertionError(); } );
        final AssertableMock assertable = new AssertableMock("one");
        Map<Assertable, List<Assertion>> failedAssertions = new LinkedHashMap<>();
        UnusedAssertionChecker unusedAssertion = new UnusedAssertionChecker();
        assertion.check(assertable, failedAssertions, unusedAssertion);

        assertEquals(0, unusedAssertion.getFailedAssertions().size());

        assertEquals(1, failedAssertions.size());
        assertEquals(assertion, failedAssertions.get(assertable).get(0));
    }

    @Test
    public void shouldReportTheNotFoundAssertion() {
        Assertion assertion = new SettableAssertionMock(a -> {
            throw new TestNotFoundException("not found"); } );
        final AssertableMock assertable = new AssertableMock("one");
        Map<Assertable, List<Assertion>> failedAssertions = new LinkedHashMap<>();
        UnusedAssertionChecker unusedAssertion = new UnusedAssertionChecker();
        assertion.check(assertable, failedAssertions, unusedAssertion);

        assertEquals(0, failedAssertions.size());

        assertEquals(1, unusedAssertion.getFailedAssertions().size());
        assertEquals(assertion, unusedAssertion.getFailedAssertions().get(0));
    }

}
