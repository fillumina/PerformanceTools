package com.fillumina.performance.infrastructure;

import com.fillumina.performance.mock.MockTestable;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.rnd.XorShiftPlusRandom;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestableTest {

    private final TestableController TC = TestableController.INSTANCE;

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(ProgressionAssertion assertions) {
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly()
                        .setSamples(33);
            }

            @Override
            public void addTests(TestContainer<Testable> tests) {
                tests.addTest("sin", new Testable() {
                    XorShiftPlusRandom rnd = new XorShiftPlusRandom();
                    @Override
                    public void test() {
                        Sink.drain(Math.sin(rnd.nextDouble()));
                    }
                });
            }

        }.executeWithFullOutput();
    }

    @Test
    public void shouldCallSetUpThroughController() {
        MockTestable testable = new MockTestable();
        assertTrue(TC.setUp(testable));

        testable.assertCalls(1);
        testable.assertEvent(0, MockTestable.TMethod.SET_UP);
    }

    @Test
    public void should_NOT_CallTearsDownIfSetupHasNotBeenCalled() {
        MockTestable testable = new MockTestable();
        assertFalse(TC.tearDown(testable));

        testable.assertCalls(0);
        testable.negateMethod(MockTestable.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallTearsDownThroughController() {
        MockTestable testable = new MockTestable();
        assertTrue(TC.setUp(testable));
        assertTrue(TC.tearDown(testable));

        testable.assertCalls(2);
        testable.assertEvent(0, MockTestable.TMethod.SET_UP);
        testable.assertEvent(1, MockTestable.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallTearsDownIfCalledTheSameNumberOfSetup() {
        MockTestable testable = new MockTestable();
        assertTrue(TC.setUp(testable));
        assertFalse(TC.setUp(testable));

        assertFalse(TC.tearDown(testable));
        assertTrue(TC.tearDown(testable));

        testable.assertCalls(2);
        testable.assertEvent(0, MockTestable.TMethod.SET_UP);
        testable.assertEvent(1, MockTestable.TMethod.TEAR_DOWN);
    }

    @Test
    public void should_NOT_CallTearsDownIfNotCalledTheSameNumberOfSetup() {
        MockTestable testable = new MockTestable();
        assertTrue(TC.setUp(testable));
        assertFalse(TC.setUp(testable));

        assertFalse(TC.tearDown(testable));

        testable.assertCalls(1);
        testable.assertEvent(0, MockTestable.TMethod.SET_UP);
        testable.negateMethod(MockTestable.TMethod.TEAR_DOWN);
    }

    @Test
    public void shouldCallTearsDownOnExceptionNoMatterWhat() {
        MockTestable testable = new MockTestable();
        assertTrue(TC.setUp(testable));
        assertFalse(TC.setUp(testable));
        assertFalse(TC.setUp(testable));

        TC.tearDownOnException(testable);

        testable.assertCalls(2);
        testable.assertEvent(0, MockTestable.TMethod.SET_UP);
        testable.assertEvent(1, MockTestable.TMethod.TEAR_DOWN);
    }
}
