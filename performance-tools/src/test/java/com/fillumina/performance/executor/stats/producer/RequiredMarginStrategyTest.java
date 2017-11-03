/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.util.ExpBinarySearcher;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.collection.ROIntList;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

        assertEquals(ROIntList.EMPTY, strategy.getIterations());
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
                .samples(5)
                .maxAllowedMargin(requiredMargin)
                .build();

        SampleProgressionStatus status = createStatus(7, 5, margin);

        assertTrue(strategy.continueTakingSamples(status));
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

        assertFalse(strategy.continueTakingSamples(status));
    }

    private SampleProgressionStatus createStatus(int executedSamples,
            int expectedSamples, Ratio margin) {
        MixedAssertableHolder mixedAssertableHolder =
                createMixedAssertableHolderWithMargin(margin);

        SampleProgressionStatus status = new SampleProgressionStatus(
                executedSamples,
                ROIntList.EMPTY, expectedSamples, 0, null,
                mixedAssertableHolder, 0,
                null);
        return status;
    }

    private MixedAssertableHolder createMixedAssertableHolderWithMargin(
            Ratio requiredMargin) {
        Holder<MixedAssertableHolder> holder = new Holder<>();
        ExpBinarySearcher.search(0, 200, (o) -> {
            MixedAssertableHolder mah = createMixedAssertableHolder(o);
            Ratio maxMargin = RequiredMarginStrategy.getMaxPercentageMargin(mah);
            //System.out.println("d=" + o + "\tmargin=" + maxMargin.toString());
            holder.setValue(mah);
            return maxMargin.compareTo(requiredMargin);
        });
        return holder.getValue();
    }

    private MixedAssertableHolder createMixedAssertableHolder(double stdev) {
        MixedAssertableHolder mixedAssertableHolder =
                StatsMock.builder()
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
                        .buildWithSyntheticNormalValues();
        return mixedAssertableHolder;
    }
}
