package com.fillumina.performance.assertion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MultiAssertionNavigatorTest {

    private static class NamedAssertion extends AssertionMock {
        private final String name;

        public NamedAssertion(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    public void shouldIterateThroughSimpleCollection() {
        List<Assertion> list = new ArrayList<>();
        list.add(new NamedAssertion("1"));
        list.add(new NamedAssertion("2"));
        list.add(new NamedAssertion("3"));

        MultiAssertionNavigator navigator = new MultiAssertionNavigator(list);

        List<String> result = new ArrayList<>();

        navigator.forEach(null, a -> result.add(a.toString()));

        assertEquals(Arrays.asList("1", "2", "3"), result);
    }

    @Test
    public void shouldIterateThroughMultiAssertionTree() {
        List<Assertion> list = new ArrayList<>();

        list.add(new NamedAssertion("1"));
        list.add(new MultiAssertionNavigator(Arrays.asList(
                new NamedAssertion("2a"), new NamedAssertion("2b"))));
        list.add(new NamedAssertion("3"));

        MultiAssertionNavigator navigator = new MultiAssertionNavigator(list);

        List<String> result = new ArrayList<>();

        navigator.forEach(null, a -> result.add(a.toString()));

        assertEquals(Arrays.asList("1", "2a", "2b", "3"), result);
    }

    @Test
    public void shouldAppendTo() throws Exception {
        List<Assertion> list = new ArrayList<>();

        list.add(new NamedAssertion("1"));
        list.add(new MultiAssertionNavigator(Arrays.asList(
                new NamedAssertion("2a"), new NamedAssertion("2b"))));
        list.add(new NamedAssertion("3"));

        MultiAssertionNavigator navigator = new MultiAssertionNavigator(list);

        StringBuilder buf = new StringBuilder();
        navigator.appendTo(buf, null);

        assertEquals("12a2b3", buf.toString());
    }

}
