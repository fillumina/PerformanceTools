package com.fillumina.performance.util.filter;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MostUsedFilterTest {

    @Test
    public void shouldEliminateLessUsedValues() {
        List<Double> list = Arrays.asList(1.1, 3.0, 1.1, 5.0, 11.0, 8.0, 3.0, 1.1);
        List<Double> result = MostUsedFilter.<Double>instance().filter(list);
        assertEquals(Arrays.asList(1.1, 1.1, 1.1), result);
    }

    @Test
    public void shouldNotEliminateAnythingIfNotRepeatedValues() {
        List<Double> list = Arrays.asList(1.1, 3.0, 5.0, 11.0, 8.0);
        List<Double> result = MostUsedFilter.<Double>instance().filter(list);
        assertEquals(list, result);
    }

}
