package com.fillumina.performance.infrastructure;

/**
 * Ancillary class used to access package accessors of {@link Testable}.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestableController {

    public static final TestableController INSTANCE = new TestableController();

    public boolean setUp(Testable testable) {
        return testable.innerSetUp();
    }

    public boolean tearDown(Testable testable) {
        return testable.innerTearDown();
    }

    public void tearDownOnException(Testable testable) {
        testable.innerTearDownOnException();
    }
}
