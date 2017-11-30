package com.fillumina.performance.assertion;

import com.fillumina.performance.mock.AssertionMock;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class UnusedAssertionCheckerTest {

    @Test
    public void shouldNotReportUsedAssertions() {
        Assertion a1 = new AssertionMock();
        Assertion a2 = new AssertionMock();
        Assertion a3 = new AssertionMock();

        UnusedAssertionChecker unused = new UnusedAssertionChecker();
        unused.setUsed(a1);
        unused.setUsed(a2);
        unused.setUsed(a3);

        assertTrue(unused.getList().isEmpty());
    }

    @Test
    public void shouldReportNotUsedAssertions() {
        Assertion a1 = new AssertionMock();
        Assertion a2 = new AssertionMock();
        Assertion a3 = new AssertionMock();

        UnusedAssertionChecker unused = new UnusedAssertionChecker();
        unused.setUnused(a1);
        unused.setUnused(a2);
        unused.setUnused(a3);

        assertEquals(3, unused.getList().size());
    }

    @Test
    public void shouldReportNotUsedMixedAssertions() {
        Assertion a1 = new AssertionMock();
        Assertion a2 = new AssertionMock();
        Assertion a3 = new AssertionMock();

        UnusedAssertionChecker unused = new UnusedAssertionChecker();
        unused.setUnused(a1);
        unused.setUsed(a2);
        unused.setUnused(a3);

        List<Assertion> list = unused.getList();
        assertEquals(2, list.size());
        assertTrue(list.contains(a1));
        assertTrue(list.contains(a3));
    }

}
