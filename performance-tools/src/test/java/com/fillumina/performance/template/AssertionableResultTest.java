package com.fillumina.performance.template;

import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.unit.Magnitude;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.ExperimentAssertion;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertionableResultTest {

    private Stats createStats(String name) {
        return new StatsMockBuilder()
                .name(name)
                .addTest("test").mean(10.0).stdev(2.0).endTest()
                .buildWithCoincidentalValues(Magnitude.UNIT)
                .getFirstStatsHolder()
                .getStats()
                .as(Magnitude.UNIT);
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
        ExperimentAssertion assertion = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                throw new AssertionError();
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
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

        Map<AssertableExperiment, List<ExperimentAssertion>> map = result.getFailedAssertions();

        assertEquals(1, map.size());
        assertEquals(assertion, map.get(stats).get(0));
    }

    @Test
    public void shouldReturnAssertionNotFound() {
        ExperimentAssertion assertion = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                throw new MeasureNotFoundException("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
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

        Map<AssertableExperiment, List<ExperimentAssertion>> map = aResult.getFailedAssertions();

        assertEquals(1, map.size());
        assertEquals(assertion, map.get(AssertionableResult.UNCHECKED).get(0));
    }

    @Test
    public void shouldReturnNothingIfAllAssertionsAreSatisfied() {
        ExperimentAssertion assertion = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                // do nothing
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
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

        Map<AssertableExperiment, List<ExperimentAssertion>> map = result.getFailedAssertions();

        assertTrue(map.isEmpty());
    }

    @Test
    public void shouldReturnForDifferentResults() {
        ExperimentAssertion okAssertion = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                // do nothing
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                // do nothing
            }

        };

        ExperimentAssertion failingAssertion1 = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                throw new AssertionError("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                // do nothing
            }

        };

        ExperimentAssertion failingAssertion2 = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                throw new AssertionError("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                // do nothing
            }

        };

        ExperimentAssertion notFoundAssertion = new ExperimentAssertion() {
            @Override public void accept(AssertableExperiment t) {
                throw new MeasureNotFoundException("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
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

        Map<AssertableExperiment, List<ExperimentAssertion>> map = aResult.getFailedAssertions();

        assertEquals(2, map.size());
        assertEquals(notFoundAssertion,
                map.get(AssertionableResult.UNCHECKED).get(0));
        assertEquals(failingAssertion1, map.get(stats).get(0));
        assertEquals(failingAssertion2, map.get(stats).get(1));
    }

}
