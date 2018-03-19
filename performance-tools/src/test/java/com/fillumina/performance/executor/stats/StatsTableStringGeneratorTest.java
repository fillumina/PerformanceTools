package com.fillumina.performance.executor.stats;

import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.unit.Magnitude;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsTableStringGeneratorTest {

    private boolean output;

    public static void main(final String[] args) {
        StatsTableStringGeneratorTest test = new StatsTableStringGeneratorTest();
        test.output = true;
        test.shouldGenerateStringFromSingleStats();
        test.shouldGenerateStringFromMultipleStats();
    }

    @Test
    public void shouldGenerateStringFromSingleStats() {
        Stats stats = new StatsMockBuilder()
                .name("single")
                .addTest("one")
                    .mean(10.0)
                    .stdev(2.3)
                    .samples(33)
                .endTest()
                .buildWithSyntheticNormalValues(Magnitude.UNIT)
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats()
                .as(Magnitude.UNIT);

        String str = StatsTableStringGenerator.INSTANCE.toString(stats);
        assertNotNull(str);
        if (output) {
            System.out.println(str);
        }
    }

    @Test
    public void shouldGenerateStringFromMultipleStats() {
        Stats stats = new StatsMockBuilder()
                .name("multiple")
                .addTest("one")
                    .mean(10.0)
                    .stdev(2.3)
                    .samples(33)
                .endTest()
                .addTest("two")
                    .mean(20.0)
                    .stdev(4.3)
                    .samples(100)
                .endTest()
                .buildWithSyntheticNormalValues(Magnitude.UNIT)
                .getStatsHolder(MockStatsType.INSTANCE)
                .getStats()
                .as(Magnitude.UNIT);

        String str = StatsTableStringGenerator.INSTANCE.toString(stats);
        assertNotNull(str);
        if (output) {
            System.out.println(str);
        }
    }

}
