package com.fillumina.performance.util.instrument;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TelescopicTest {

    private static class TI<S> implements TelescopicGenerics<TI<S>> {
        private final S value;

        public TI(S s) {
            this.value = s;
        }

        public S getValue() {
            return value;
        }
    }

    @Test
    public void shouldWorkFirstLevel() {
        TI<String> tis = new TI<>("hello");
        final String value = tis.getValue();
        assertEquals("hello", value);
    }

    @Test
    public void shouldWorkSecondLevel() {
        TI<String> tis = new TI<>("hello");
        TI<TI<String>> ttis = new TI<>(tis);

        final TI<String> firstLevel = ttis.getValue();
        final String value = firstLevel.getValue();

        assertEquals("hello", value);
    }

    @Test
    public void shouldWorkThirdLevel() {
        TI<String> tis = new TI<>("hello");
        TI<TI<String>> ttis = new TI<>(tis);
        TI<TI<TI<String>>> tttis = new TI<>(ttis);

        final TI<TI<String>> secondLevel = tttis.getValue();
        final TI<String> firstLevel = secondLevel.getValue();
        final String value = firstLevel.getValue();

        assertEquals("hello", value);
    }

    //... and so on!
}
