package com.fillumina.performance.assertion;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionTest {

    public static class AssertionImpl implements Assertion {
        private final Consumer<Assertable> consumer;

        public AssertionImpl(Consumer<Assertable> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void accept(Assertable t) {
            consumer.accept(t);
        }

        @Override
        public void appendTo(Appendable appendable, Assertable assertable)
                throws IOException {
            // do nothing
        }

    }

    @Test
    public void shouldReportTheFailingAssertion() {
        Assertion assertion = new AssertionImpl(a -> {
            throw new AssertionError(); } );
        final AssertableMock assertable = new AssertableMock("one");
        Map<Assertable, List<Assertion>> failedAssertions = new LinkedHashMap<>();
        Map<Assertion, Boolean> checkedAssertionMap = new LinkedHashMap<>();
        assertion.check(assertable, failedAssertions, checkedAssertionMap);

        assertEquals(1, failedAssertions.size());
        assertEquals(1, checkedAssertionMap.size());
        System.out.println("");
    }

    @Test
    public void shouldReportTheNotFoundAssertion() {
        Assertion assertion = new AssertionImpl(a -> {
            throw new TestNotFoundException("not found"); } );
        final AssertableMock assertable = new AssertableMock("one");
        Map<Assertable, List<Assertion>> failedAssertions = new LinkedHashMap<>();
        Map<Assertion, Boolean> checkedAssertionMap = new LinkedHashMap<>();
        assertion.check(assertable, failedAssertions, checkedAssertionMap);

        assertEquals(0, failedAssertions.size());
        assertEquals(1, checkedAssertionMap.size());
    }

}
