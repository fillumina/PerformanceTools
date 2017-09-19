package com.fillumina.performance.util.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AbstractConditionalFilterTest {

    private static class EvenListFilter
            extends AbstractConditionalFilter<Integer> {
        @Override
        protected boolean acceptCondition(Integer value) {
            int v = value;
            return (v & ~1) == v;
        }
    }

    @Test
    public void shouldEliminateNonPairValues() {
        EvenListFilter filter = new EvenListFilter();
        List<String> list = Arrays.asList("1", "3", "2", "16", "7271", "34578");
        List<String> pairs = filter.filter(list, s -> Integer.parseInt(s));

        assertEquals(new HashSet<>(Arrays.asList("2", "16", "34578")),
                new HashSet<>(pairs));
    }

    @Test
    public void shouldReturnAnEmptyListIfNoPairValues() {
        EvenListFilter filter = new EvenListFilter();
        List<String> list = Arrays.asList("1", "3", "7", "31", "7271", "34579");
        List<String> pairs = filter.filter(list, s -> Integer.parseInt(s));

        assertTrue(pairs.isEmpty());
    }
}
