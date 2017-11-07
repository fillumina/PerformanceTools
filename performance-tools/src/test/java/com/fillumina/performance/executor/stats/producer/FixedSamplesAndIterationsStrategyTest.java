package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class FixedSamplesAndIterationsStrategyTest {

    @Test
    public void shouldGetExpectedNumberOfSamplesWithoutWarmUp() {
        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .samples(7)
                        .build();

        assertEquals(7, strategy.getExpectedNumberOfSamples());
    }

    @Test
    public void shouldGetExpectedNumberOfSamplesWithWarmUp() {
        final int warmupSamples = 3;
        final int effectiveSamples = 7;

        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .warmupSamples(warmupSamples)
                        .samples(effectiveSamples)
                        .build();

        assertEquals(warmupSamples, strategy.getExpectedNumberOfSamples());

        strategy.repeatExecution(null);

        assertEquals(effectiveSamples, strategy.getExpectedNumberOfSamples());
    }

    @Test
    public void shouldGetIterations() {
        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .iterations(1, 2, 3)
                        .samples(1)
                        .build();

        UnmodifiableIntList list = strategy.getIterations();
        assertEquals(new UnmodifiableIntList(1, 2, 3), list);
    }

    @Test
    public void shouldRepeatExecutionIfWarmupIsPresent() {
        final int warmupSamples = 5;
        final int samples = 7;
        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .warmupSamples(warmupSamples)
                        .samples(samples)
                        .build();

        iterate(strategy, warmupSamples);
        assertTrue(strategy.repeatExecution(null));

        iterate(strategy, samples);
        assertFalse(strategy.repeatExecution(null));
    }

    @Test
    public void shouldNotRepeatExecutionIfWarmupIsAbsent() {
        final int samples = 7;
        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .samples(samples)
                        .build();

        iterate(strategy, samples);
        assertFalse(strategy.repeatExecution(null));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAcceptZeroSamples() {
        FixedSamplesAndIterationsStrategy.builder()
                .warmupSamples(3)
                .samples(0)
                .build();
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAcceptNegativeSamples() {
        FixedSamplesAndIterationsStrategy.builder()
                .samples(-3)
                .build();
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAcceptNegativeWarmupSamples() {
        FixedSamplesAndIterationsStrategy.builder()
                .warmupSamples(-3)
                .samples(0)
                .build();
    }

    @Test
    public void shouldContinueTakingSamplesIfNotFinishedWarmup() {
        final int warmup = 3;
        final int samples = 5;
        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .warmupSamples(warmup)
                        .samples(samples)
                        .build();

        for (int i=0; i<warmup; i++) {
            SampleProgressionStatus status = createStatus(i, warmup);
            assertTrue("i=" + i + ", warmup=" + warmup,
                    strategy.continueTakingSamples(status));
        }
        assertTrue(strategy.repeatExecution(null));

        for (int i=0; i<samples; i++) {
            SampleProgressionStatus status = createStatus(i, samples);
            assertTrue("i=" + i + ", samples=" + samples,
                    strategy.continueTakingSamples(status));
        }
        assertFalse(strategy.repeatExecution(null));
    }

    private void iterate(FixedSamplesAndIterationsStrategy strategy, int times) {
        for (int i=0; i<times; i++) {
            SampleProgressionStatus status = createStatus(i, times);
            strategy.continueTakingSamples(status);
        }
    }

    private SampleProgressionStatus createStatus(int i, final int samples) {
        return new SampleProgressionStatus(i,
                UnmodifiableIntList.EMPTY, samples, 0, null,
                MixedAssertableHolder.EMPTY, 0, "");
    }

    @Test
    public void shouldBuildStrategy() {
        FixedSamplesAndIterationsStrategy strategy =
                FixedSamplesAndIterationsStrategy.builder()
                        .iterations(1, 2, 3)
                        .warmupSamples(7)
                        .samples(25)
                        .build();

        assertEquals(7, strategy.getExpectedNumberOfSamples());
        assertEquals(new UnmodifiableIntList(1,2,3), strategy.getIterations());
        strategy.repeatExecution(null);
        assertEquals(25, strategy.getExpectedNumberOfSamples());
    }
}
