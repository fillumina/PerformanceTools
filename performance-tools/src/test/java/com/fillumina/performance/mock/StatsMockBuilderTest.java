package com.fillumina.performance.mock;

import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.stats.Ratio;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsMockBuilderTest {

    @Test
    public void shouldCreateCoincidentalSpeedStats() {
        MixedAssertableHolder mixedHolder = new StatsMockBuilder()
                .name("STATS (coincidental values):")
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .mean(10.0)
                    .stdev(5.0)
                .endTest()
                .addTest("second")
                    .mean(20.0)
                    .stdev(7.0)
                .endTest()
                .buildWithCoincidentalValues();

        StatsMock stats = mixedHolder.getStats(StatsMock.class).getAssertable();

        assertEquals(10.0, stats.getMeasure(TN.tname("first")).getMean(), 1.0);
        assertEquals(20.0, stats.getMeasure(TN.tname("second")).getMean(), 1.0);
    }

    @Test
    public void shouldCreateNormalDistributionSpeedStats() {
        MixedAssertableHolder mixedHolder = new StatsMockBuilder()
                .name("STATS (normal distribution):")
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .mean(10.0)
                    .stdev(3.0)
                    .samples(80)
                .endTest()
                .addTest("second")
                    .mean(20.0)
                    .stdev(5.0)
                    .samples(90)
                .endTest()
                .buildWithNormalDistribution();

        StatsMock stats = mixedHolder.getStats(StatsMock.class).getAssertable();

        assertEquals(10.0, stats.getMeasure(TN.tname("first")).getMean(), 1.0);
        assertEquals(20.0, stats.getMeasure(TN.tname("second")).getMean(), 1.0);
    }

    @Test
    public void shouldCreateSyntheticSpeedStats() {
        MixedAssertableHolder mixedHolder = new StatsMockBuilder()
                .name("STATS (synthetic pseudo normal distribution):")
                .confidence(Ratio.decimal(0.1))
                .addTest("first")
                    .mean(10.0)
                    .stdev(5.0)
                    .samples(80)
                .endTest()
                .addTest("second")
                    .mean(20.0)
                    .stdev(7.0)
                    .samples(90)
                .endTest()
                .buildWithSyntheticNormalValues();

        StatsMock stats = mixedHolder.getStats(StatsMock.class).getAssertable();

        assertEquals(10.0, stats.getMeasure(TN.tname("first")).getMean(), 1.0);
        assertEquals(20.0, stats.getMeasure(TN.tname("second")).getMean(), 1.0);
    }

}
