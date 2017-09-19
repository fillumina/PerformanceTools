package com.fillumina.performance.util.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FilterChainTest {

    private static class PairListFilter
            extends AbstractConditionalFilter<Integer> {

        @Override
        protected boolean acceptCondition(Integer value) {
            int v = value;
            return (v & ~1) == v;
        }
    }

    private static class PositiveListFilter
            extends AbstractConditionalFilter<Integer> {

        @Override
        protected boolean acceptCondition(Integer value) {
            return value >= 0;
        }
    }

    @SuppressWarnings("unchecked")
    private final ListFilter<Integer> filterChain =
                new FilterChain<>(0,
                        new PairListFilter(),
                        new PositiveListFilter());

    @Test
    public void shouldConcatenateFilters() {
        List<String> list = Arrays.asList("1", "3", "2", "-16", "7271", "34578");
        List<String> pairs = filterChain.filter(list, s -> Integer.parseInt(s));

        assertEquals(new HashSet<>(Arrays.asList("2", "34578")),
                new HashSet<>(pairs));
    }
}
