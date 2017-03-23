package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.TestableMock;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestableTest {

    private final TestableController TC = TestableController.INSTANCE;

    @Test
    public void shouldCallSetUpThroughController() {
        TestableMock testable = new TestableMock();
        assertTrue(TC.setUp(testable));

        testable.assertCalls(1);
        testable.assertEvent(0, TestableMock.TMethod.SET_UP);
    }

    @Test
    public void should_NOT_CallTearsDownIfSetupHasNotBeenCalled() {
        TestableMock testable = new TestableMock();
        assertFalse(TC.tearDown(testable));

        testable.assertCalls(0);
        testable.negateMethod(TestableMock.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallTearsDownThroughController() {
        TestableMock testable = new TestableMock();
        assertTrue(TC.setUp(testable));
        assertTrue(TC.tearDown(testable));

        testable.assertCalls(2);
        testable.assertEvent(0, TestableMock.TMethod.SET_UP);
        testable.assertEvent(1, TestableMock.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallTearsDownIfCalledTheSameNumberOfSetup() {
        TestableMock testable = new TestableMock();
        assertTrue(TC.setUp(testable));
        assertFalse(TC.setUp(testable));

        assertFalse(TC.tearDown(testable));
        assertTrue(TC.tearDown(testable));

        testable.assertCalls(2);
        testable.assertEvent(0, TestableMock.TMethod.SET_UP);
        testable.assertEvent(1, TestableMock.TMethod.TEAR_DOWN);
    }

    @Test
    public void should_NOT_CallTearsDownIfNotCalledTheSameNumberOfSetup() {
        TestableMock testable = new TestableMock();
        assertTrue(TC.setUp(testable));
        assertFalse(TC.setUp(testable));

        assertFalse(TC.tearDown(testable));

        testable.assertCalls(1);
        testable.assertEvent(0, TestableMock.TMethod.SET_UP);
        testable.negateMethod(TestableMock.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallTearsDownOnExceptionNoMatterWhat() {
        TestableMock testable = new TestableMock();
        assertTrue(TC.setUp(testable));
        assertFalse(TC.setUp(testable));
        assertFalse(TC.setUp(testable));

        TC.tearDownOnException(testable);

        testable.assertCalls(2);
        testable.assertEvent(0, TestableMock.TMethod.SET_UP);
        testable.assertEvent(1, TestableMock.TMethod.TEAR_DOWN);
    }
}
