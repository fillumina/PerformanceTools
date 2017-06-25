package com.fillumina.performance.util;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SelectableTest {

    private static final Selectable<Integer> S1 = (Integer t) -> t * 1;
    private static final Selectable<Integer> S2 = (Integer t) -> t * 2;
    private static final Selectable<Integer> S3 = (Integer t) -> t * 3;

    @Test
    public void shouldReturnTheHigherSelectableFromArray() {
        @SuppressWarnings("unchecked")
        Selectable<Integer> higher = Selectable.select(3, S1, S2, S3);

        assertTrue(higher == S3);
    }

    @Test
    public void shouldReturnTheHigherSelectableFromList() {
        List<Selectable<Integer>> list = Arrays.asList(S1, S2, S3);
        Selectable<Integer> higher = Selectable.select(3, list);

        assertTrue(higher == S3);
    }
}
