package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.unit.Magnitude;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsExpressionTest {

    @Test
    public void shouldCreateAnExpressionUsingTNames() {
        StatsExpression<?> solver = new StatsExpression<>();
        solver.addExpression(TN.tname("first"))
                .addTest(TN.tname("one")).multiplyBy(7)
                .subtractTest(TN.tname("two")).divideBy(3)
                .addExpression()
                    .addTest(TN.tname("three", "four")).multiplyBy(2)
                    .subtractTest(TN.tname("one"))
                .endExpression().divideBy(5);

        assertEquals(
                "first: [one] * 7.0 - [two] / 3.0 + " +
                        "([three : four] * 2.0 - [one]) / 5.0",
                solver.toString());
    }

    @Test
    public void shouldCreateAnExpressionUsingStrings() {
        StatsExpression<?> solver = new StatsExpression<>();
        solver.addExpression("first")
                .addTest("one").multiplyBy(7)
                .subtractTest("two").divideBy(3)
                .addExpression()
                    .addTest("three").multiplyBy(2)
                    .subtractTest("one")
                .endExpression().divideBy(5);

        assertEquals(
                "first: [one] * 7.0 - [two] / 3.0 + " +
                        "([three] * 2.0 - [one]) / 5.0",
                solver.toString());
    }

    @Test
    public void shouldSolveTheExpression() {
        Stats stats = new StatsMockBuilder()
                .name(TN.tname("first"))
                .addTest(TN.tname("one")).mean(10.0).endTest()
                .addTest(TN.tname("two")).mean(20.0).endTest()
                .addTest(TN.tname("three", "four")).mean(30.0).endTest()
                .buildWithCoincidentalValues(Magnitude.UNIT)
                .getFirstStatsHolder()
                .getStats()
                .as(Magnitude.UNIT);

        StatsExpression<?> expression = new StatsExpression<>();
        expression.addExpression(TN.tname("first"))
                .addTest(TN.tname("one")).multiplyBy(7)
                .subtractTest(TN.tname("two")).divideBy(3)
                .addExpression()
                    .addTest(TN.tname("three", "four")).multiplyBy(2)
                    .subtractTest(TN.tname("one"))
                .endExpression().divideBy(5);

        Map<TName,Measure> measureMap = expression.solve(stats);

        Measure measure = measureMap.get(TN.tname("first"));

        double result = 10.0 * 7.0 - 20.0 / 3.0 + (30.0 * 2 - 10.0) / 5.0;

        assertEquals(result, measure.getMean(), 0.001);
    }
}
