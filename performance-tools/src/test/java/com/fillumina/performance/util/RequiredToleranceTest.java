package com.fillumina.performance.util;

import com.fillumina.performance.util.stats.Ratio;
import java.util.function.BiPredicate;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RequiredToleranceTest {

    /**
     * The predicate is satisfied for tolerances less than given one
     * whichever the order.
     */
    private BiPredicate<RelativeOrder,Ratio> createPredicate(final Ratio tolerance) {
        return (RelativeOrder o, Ratio t) -> {
            switch (o) {
                case EQUALS: return t.compareTo(tolerance) == 1;
                case GREATER: return t.compareTo(tolerance) == 1;
                case LESS: return t.compareTo(tolerance) == 1;
            }
            throw new AssertionError("unvalid option " + o);
        };
    }

    private RequiredTolerance createPredicateWithToleranceLessThan(int perc) {
        return new RequiredTolerance(createPredicate(Ratio.percentage(perc)));
    }

    @Test
    public void shouldReturnTheRightRequiredTolerance_1() {
        RequiredTolerance rt = createPredicateWithToleranceLessThan(10);
        assertEquals(Ratio.percentage(11),
                rt.getRequiredToleranceFor(RelativeOrder.GREATER));
    }

    @Test
    public void shouldReturnTheRightRequiredTolerance_2() {
        RequiredTolerance rt = createPredicateWithToleranceLessThan(23);
        // order is meaninless here
        assertEquals(Ratio.percentage(24),
                rt.getRequiredToleranceFor(RelativeOrder.EQUALS));
    }

}
