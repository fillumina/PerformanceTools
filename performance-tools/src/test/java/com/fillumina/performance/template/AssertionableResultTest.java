package com.fillumina.performance.template;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.Assertion;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionableResultTest {

    private Stats createStats(String name) {
        return new StatsMockBuilder()
                .name(name)
                .addTest("test").mean(10.0).stdev(2.0).endTest()
                .buildWithCoincidentalValues().getFirstStatsHolder().getStats();
    }

    @Test
    public void shouldReturnTheGivenAssertHolder() {
        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(
                MockStatsType.INSTANCE,
                stats);

        AssertionableResult<?> aResult =
                AssertionableResult.builder()
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        assertEquals(holder, aResult.getStatsHolder());
    }

    @Test
    public void shouldReturnUnsatisfiedAssertion() {
        Assertion assertion = new Assertion() {
            @Override public void accept(Assertable t) {
                throw new AssertionError();
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(MockStatsType.INSTANCE, stats);

        AssertionableResult<?> result =
                AssertionableResult.builder()
                        .addAssertion(assertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        Map<Assertable, List<Assertion>> map = result.getFailedAssertions();

        assertEquals(1, map.size());
        assertEquals(assertion, map.get(stats).get(0));
    }

    @Test
    public void shouldReturnAssertionNotFound() {
        Assertion assertion = new Assertion() {
            @Override public void accept(Assertable t) {
                throw new MeasureNotFoundException("not found");
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(
                MockStatsType.INSTANCE,
                stats);

        AssertionableResult<?> aResult =
                AssertionableResult.builder()
                        .addAssertion(assertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        Map<Assertable, List<Assertion>> map = aResult.getFailedAssertions();

        assertEquals(1, map.size());
        assertEquals(assertion, map.get(AssertionableResult.UNCHECKED).get(0));
    }

    @Test
    public void shouldReturnNothingIfAllAssertionsAreSatisfied() {
        Assertion assertion = new Assertion() {
            @Override public void accept(Assertable t) {
                // do nothing
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(MockStatsType.INSTANCE, stats);

        AssertionableResult<?> result = AssertionableResult.builder()
                        .addAssertion(assertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        Map<Assertable, List<Assertion>> map = result.getFailedAssertions();

        assertTrue(map.isEmpty());
    }

    @Test
    public void shouldReturnForDifferentResults() {
        Assertion okAssertion = new Assertion() {
            @Override public void accept(Assertable t) {
                // do nothing
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        Assertion failingAssertion1 = new Assertion() {
            @Override public void accept(Assertable t) {
                throw new AssertionError("not found");
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        Assertion failingAssertion2 = new Assertion() {
            @Override public void accept(Assertable t) {
                throw new AssertionError("not found");
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        Assertion notFoundAssertion = new Assertion() {
            @Override public void accept(Assertable t) {
                throw new MeasureNotFoundException("not found");
            }

            @Override
            public void appendTo(Appendable appendable, Assertable assertable)
                    throws IOException {
                // do nothing
            }

        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(
                MockStatsType.INSTANCE,
                stats);

        AssertionableResult<?> aResult =
                AssertionableResult.builder()
                        .addAssertion(okAssertion)
                        .addAssertion(failingAssertion1)
                        .addAssertion(failingAssertion2)
                        .addAssertion(notFoundAssertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        Map<Assertable, List<Assertion>> map = aResult.getFailedAssertions();

        assertEquals(2, map.size());
        assertEquals(notFoundAssertion,
                map.get(AssertionableResult.UNCHECKED).get(0));
        assertEquals(failingAssertion1, map.get(stats).get(0));
        assertEquals(failingAssertion2, map.get(stats).get(1));
    }

}
