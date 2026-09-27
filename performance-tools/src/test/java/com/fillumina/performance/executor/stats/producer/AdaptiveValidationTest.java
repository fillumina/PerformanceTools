package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.SampleProducerMock;
import com.fillumina.performance.util.filter.ListFilter;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.Arrays;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AdaptiveValidationTest {
    @Test
    public void shouldReportFreshFixedSamplesInsteadOfSelectedExploratorySamples() {
        double[] first = new double[66];
        double[] second = new double[66];
        Arrays.fill(first, 0, 33, 1);
        Arrays.fill(first, 33, 66, 2);
        Arrays.fill(second, 0, 33, 1);
        Arrays.fill(second, 33, 66, 4);
        Stats stats = execute(first, second);

        assertEquals(33, stats.getMeasure("first").getCount());
        assertEquals(2, stats.getMeasure("first").getMean(), 0);
        assertEquals(4, stats.getMeasure("second").getMean(), 0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfFreshValidationDoesNotMeetTheMargin() {
        double[] first = new double[66];
        double[] second = new double[66];
        Arrays.fill(first, 0, 33, 1);
        Arrays.fill(second, 0, 33, 1);
        for (int i = 33; i < 66; i++) {
            first[i] = i % 2 == 0 ? 1 : 10;
            second[i] = 10;
        }
        execute(first, second);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRejectStronglyAutocorrelatedValidationSamples() {
        double[] first = new double[66];
        double[] second = new double[66];
        Arrays.fill(first, 0, 33, 10);
        Arrays.fill(second, 0, 33, 10);
        Random random = new Random(125);
        double state = 0;
        for (int i = 33; i < 66; i++) {
            state = 0.9 * state + 0.1 * random.nextGaussian();
            first[i] = 10 + state;
            second[i] = 10;
        }
        execute(first, second);
    }

    private Stats execute(double[] first, double[] second) {
        ConfigurableStatsProducer producer = RequiredMarginStrategy.builder()
                .samples(33)
                .setCoolDownCpuActive(false)
                .setSampleFilter(ListFilter.identity())
                .buildStatsProducer();
        producer.instrument(new SampleProducerMock(Magnitude.UNIT)
                .addSamples("first", first).addSamples("second", second));
        producer.addTest("first", () -> {});
        producer.addTest("second", () -> {});
        return producer.get().getFirstStatsHolder().getStats();
    }
}
