package com.fillumina.performance.assertion;

import com.fillumina.performance.util.stats.Ratio;
import java.io.IOException;
import org.junit.Test;

/**
 * This is an example on how this package could be used.
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class DefaultAssertableExperimentTest {

    public static void main(final String[] args) throws IOException {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", 6.5, 7.0, 5.5)
                .add("Lola", 7.0, 8.0)
                .add("Kenny", 4, 6.0, 5.5);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").lessThan("Lola")
                .assertValue("Kenny").equalsTo(5)
                .appendTo(System.out, votes);
    }

    @Test
    public void shouldCompareVotes() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", 6.5, 7.0, 5.5)
                .add("Lola", 7.0, 8.0)
                .add("Kenny", 4, 6.0, 5.5);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").lessThan("Lola")
                .assertValue("Kenny").equalsTo(5)
                .accept(votes);
    }

    @Test(expected=OrderAssertionError.class)
    public void shouldFindException() {
        DefaultAssertableExperiment votes = new DefaultAssertableExperiment()
                .add("Carl", 6.5, 7.0, 5.5)
                .add("Lola", 7.0, 8.0)
                .add("Kenny", 4, 6.0, 5.5);

        Assertions.withTolerance(Ratio.percentage(25))
                .assertOrder("Carl").greaterThan("Lola")
                .accept(votes);
    }
}
