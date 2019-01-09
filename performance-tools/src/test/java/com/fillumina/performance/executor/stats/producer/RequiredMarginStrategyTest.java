package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.stats.Ratio;
import com.fillumina.performance.util.unit.Magnitude;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class RequiredMarginStrategyTest {

    @Test
    public void tshouldIterationsbeEmpty() {
        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder()
                .build();

        assertEquals(UnmodifiableIntList.EMPTY, strategy.getIterations());
    }

    @Test
    public void shouldGetExpectedNumberOfSamples() {
        final int samples = 13;
        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder()
                .samples(samples)
                .build();

        assertEquals(samples, strategy.getExpectedNumberOfSamples());
    }

    @Test
    public void shouldRepeatExecutionBeAlwaysFalse() {
        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder()
                .build();

        assertFalse(strategy.repeatExecution(null));
    }

    @Test
    public void shouldContinueTakingSamplesIfRequiredMarginNotMet() {
        final Ratio requiredMargin = Ratio.percentage(5);
        final Ratio margin = Ratio.percentage(7);

        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder()
                .maxAllowedMargin(requiredMargin)
                .build();

        SampleProgressionStatus status = createStatus(7, 5, margin);

        assertNotEquals(0.0, strategy.errorToStopTakingSamplesCondition(status));
    }

    @Test
    public void shouldNotContinueTakingSamplesIfRequiredMarginIsMet() {
        final Ratio requiredMargin = Ratio.percentage(5);
        final Ratio margin = Ratio.percentage(3);

        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder()
                .samples(5)
                .maxAllowedMargin(requiredMargin)
                .build();

        SampleProgressionStatus status = createStatus(7, 5, margin);

        assertEquals(0.0, strategy.errorToStopTakingSamplesCondition(status), 0);
    }

    private SampleProgressionStatus createStatus(int executedSamples,
            int expectedSamples, Ratio margin) {

        MixedStatsHolder mixedAssertableHolder =
                createMixedAssertableHolderWithMargin(margin);

        SampleProgressionStatus status = new SampleProgressionStatus(
                executedSamples, expectedSamples, 0, UnmodifiableIntList.EMPTY, null,
                mixedAssertableHolder, 0,
                null);
        return status;
    }

    private MixedStatsHolder createMixedAssertableHolderWithMargin(
            Ratio requiredMargin) {
        Holder<MixedStatsHolder> holder = new Holder<>();
        ExpBinarySearcher.search(0, 200, o -> {
            MixedStatsHolder msh = createMixedAssertableHolder(o);
            Ratio maxMargin = RequiredMarginStrategy
                    .getMaxPercentageMargin(msh, Ratio.P_999);
            //System.out.println("d=" + o + "\tmargin=" + maxMargin.toString());
            holder.setValue(msh);
            return maxMargin.compareTo(requiredMargin);
        });
        return holder.getValue();
    }

    private MixedStatsHolder createMixedAssertableHolder(double stdev) {
        MixedStatsHolder mixedAssertableHolder =
                new StatsMockBuilder()
                        .addTest("slowest")
                            .mean(1.0)
                            .samples(100)
                            .stdev(1.0)
                        .endTest()
                        .addTest("test")
                            .mean(10.0)
                            .samples(100)
                            .stdev(stdev)
                        .endTest()
                        .buildWithSyntheticNormalValues(Magnitude.UNIT);

        return mixedAssertableHolder;
    }
}
