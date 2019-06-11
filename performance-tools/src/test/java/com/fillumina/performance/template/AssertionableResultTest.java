package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AssertableExperiment;
import com.fillumina.performance.assertion.ExperimentAssertion;
import com.fillumina.performance.assertion.MeasureNotFoundException;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.AppendableWrapper;
import com.fillumina.performance.util.unit.Magnitude;
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
                .buildWithCoincidentalValues(Magnitude.UNIT)
                .getFirstStatsHolder()
                .getStats()
                .as(Magnitude.UNIT);
    }

    @Test
    public void shouldReturnTheGivenAssertHolder() {
        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(stats);

        AssertionableResult<?> aResult =
                AssertionableResult.builder()
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        assertEquals(holder, aResult.getStatsHolder());
    }

    @Test
    public void shouldReturnUnsatisfiedAssertion() {
        ExperimentAssertion assertion = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                throw new AssertionError();
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                // do nothing
            }

        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(stats);

        AssertionableResult<?> result =
                AssertionableResult.builder()
                        .addAssertion(assertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        Map<AssertableExperiment, List<ExperimentAssertion>> map =
                result.getReport().getCatalog().getFailedAssertions();

        assertEquals(1, map.size());
        assertEquals(assertion, map.get(stats).get(0));
    }

    @Test
    public void shouldReturnAssertionNotFound() {
        ExperimentAssertion assertion = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                throw new MeasureNotFoundException("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                new AppendableWrapper(appendable).print("TEST ASSERTION");
            }
        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(stats);

        AssertionableResult<?> aResult =
                AssertionableResult.builder()
                        .addAssertion(assertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        List<ExperimentAssertion> list =
                aResult.getReport().getUnused().getUnusedAssertionList();

        //System.out.println(aResult.getReport().toString());

        assertEquals(1, list.size());
        assertEquals(assertion, list.get(0));
    }

    @Test
    public void shouldReturnNothingIfAllAssertionsAreSatisfied() {
        ExperimentAssertion assertion = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                // do nothing
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                new AppendableWrapper(appendable).print("TEST ASSERTION");
            }
        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(stats);

        AssertionableResult<?> result = AssertionableResult.builder()
                        .addAssertion(assertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        Map<AssertableExperiment, List<ExperimentAssertion>> map =
                result.getReport().getCatalog().getFailedAssertions();

        assertTrue(map.isEmpty());
    }

    @Test
    public void shouldReturnForDifferentResults() {
        ExperimentAssertion okAssertion = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                // do nothing
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                new AppendableWrapper(appendable).print("OK ASSERTION");
            }
        };

        ExperimentAssertion failingAssertion1 = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                throw new AssertionError("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                new AppendableWrapper(appendable).print("FAIL ASSERTION 1");
            }
        };

        ExperimentAssertion failingAssertion2 = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                throw new AssertionError("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                new AppendableWrapper(appendable).print("FAIL ASSERTION 2");
            }
        };

        ExperimentAssertion notFoundAssertion = new ExperimentAssertion() {
            @Override public void check(AssertableExperiment t) {
                throw new MeasureNotFoundException("not found");
            }

            @Override
            public void appendTo(Appendable appendable, AssertableExperiment assertable)
                    throws IOException {
                new AppendableWrapper(appendable).print("NOT FOUND ASSERTION");
            }

            @Override public String toString() { return "NOT FOUND ASSERTION"; }
        };

        final Stats stats = createStats("one");
        StatsHolder holder = new StatsHolder(stats);

        AssertionableResult<?> aResult =
                AssertionableResult.builder()
                        .addAssertion(okAssertion)
                        .addAssertion(failingAssertion1)
                        .addAssertion(failingAssertion2)
                        .addAssertion(notFoundAssertion)
                        .setStatsHolder(holder)
                        .buildWithSetter(null);

        //System.out.println(aResult.getReport());

        Map<AssertableExperiment, List<ExperimentAssertion>> map =
                aResult.getReport().getCatalog().getFailedAssertions();

        final List<ExperimentAssertion> failedList = map.get(stats);

        assertEquals(2, failedList.size());
        assertEquals(failingAssertion1, failedList.get(0));
        assertEquals(failingAssertion2, failedList.get(1));

        List<ExperimentAssertion> unusedList =
                aResult.getReport().getUnused().getUnusedAssertionList();

        assertEquals(notFoundAssertion, unusedList.get(0));
    }

}
