package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.collection.UnmodifiableIntList;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class InvalidRatioStoppingTest {
    @Test
    public void shouldContinueSamplingWhenTheRatioIntervalIsInvalid() {
        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder().samples(33).build();
        assertTrue(strategy.errorToStopTakingSamplesCondition(invalidStatus()) > 0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailAFixedRunWhenTheRatioIntervalIsInvalid() {
        RequiredMarginStrategy strategy = RequiredMarginStrategy.builder()
                .samples(33).fixedSampels(true).build();
        strategy.errorToStopTakingSamplesCondition(invalidStatus());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRejectAValidationWithTooFewObservationsPerVariant() {
        MixedStatsHolder holder = new StatsMockBuilder()
                .addTest("one").mean(1).samples(10).endTest()
                .addTest("two").mean(2).samples(10).endTest()
                .buildWithCoincidentalValues(Magnitude.UNIT);
        RequiredMarginStrategy.builder().build().validate(holder);
    }

    private SampleProgressionStatus invalidStatus() {
        OnlineDimensionalMeasure noisy = new OnlineDimensionalMeasure();
        OnlineDimensionalMeasure steady = new OnlineDimensionalMeasure();
        for (int i = 0; i < 33; i++) {
            noisy.addSample(i % 2 == 0 ? 102 : -98);
            steady.addSample(1);
        }
        Map<PathName, DimensionalMeasure> measures = new LinkedHashMap<>();
        measures.put(PN.pname("noisy"), noisy);
        measures.put(PN.pname("steady"), steady);
        MixedStatsHolder holder = MixedStatsHolder.builder()
                .addStats(PN.EMPTY, new Stats(measures)).build();
        return new SampleProgressionStatus(33, 33, 0, UnmodifiableIntList.EMPTY,
                null, holder, 0, null);
    }
}
