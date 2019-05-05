package com.fillumina.performance.executor.stats;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.mock.StatsMockBuilder;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import com.fillumina.performance.util.unit.Magnitude;
import com.fillumina.performance.util.unit.Unit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class StatsExpressionTest {

    @Test
    public void shouldReturnToCaller() {
        StatsExpression<StatsExpressionTest> solver = new StatsExpression<>(this);
        StatsExpression<StatsExpressionTest> caller =
                solver.addExpression(PN.pname("first"))
                .addTest(PN.pname("one")).multiplyBy(7)
                .subtractTest(PN.pname("two")).divideBy(3)
                .addExpression()
                    .addTest(PN.pname("three", "four")).multiplyBy(2)
                    .subtractTest(PN.pname("one"))
                .endExpression().divideBy(5)
            .end();

        assertEquals(solver, caller);
        assertEquals(this, caller.end());
    }

    @Test
    public void shouldCreateAnExpressionUsingTNames() {
        StatsExpression<?> solver = new StatsExpression<>();
        solver.addExpression(PN.pname("first"))
                .addTest(PN.pname("one")).multiplyBy(7)
                .subtractTest(PN.pname("two")).divideBy(3)
                .addExpression()
                    .addTest(PN.pname("three", "four")).multiplyBy(2)
                    .subtractTest(PN.pname("one"))
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
                .name(PN.pname("first"))
                .addTest(PN.pname("one")).mean(10.0).endTest()
                .addTest(PN.pname("two")).mean(20.0).endTest()
                .addTest(PN.pname("three", "four")).mean(30.0).endTest()
                .buildWithCoincidentalValues(Magnitude.UNIT)
                .getFirstStatsHolder()
                .getStats()
                .as(Magnitude.UNIT);

        StatsExpression<?> expression = new StatsExpression<>();
        expression.addExpression(PN.pname("first"))
                .addTest(PN.pname("one")).multiplyBy(7)
                .subtractTest(PN.pname("two")).divideBy(3)
                .addExpression()
                    .addTest(PN.pname("three", "four")).multiplyBy(2)
                    .subtractTest(PN.pname("one"))
                .endExpression().divideBy(5);

        Map<PathName,Measure> measureMap = expression.solve(stats);

        Measure measure = measureMap.get(PN.pname("first"));

        double result = 10.0 * 7.0 - 20.0 / 3.0 + (30.0 * 2 - 10.0) / 5.0;

        assertEquals(result, measure.getMean(), 0.001);
    }

    @Test
    public void shouldSolveTheParametrizedExpression() {
        Stats stats = new StatsMockBuilder()

                // there is only one test
                .name(PN.pname("first"))

                // with one parameter called alfa
                .addTest(PN.pname("first", "one-alfa-1")).mean(10.0).endTest()
                .addTest(PN.pname("first", "one-alfa-2")).mean(20.0).endTest()
                .addTest(PN.pname("first", "one-alfa-3")).mean(30.0).endTest()

                // these are the values of the parameter for each test
                .addParametersForTest(PN.pname("one-alfa-1"))
                    .addOption("alfa", "alfa-1", 1)
                .end()
                .addParametersForTest(PN.pname("one-alfa-2"))
                    .addOption("alfa", "alfa-2", 2)
                .end()
                .addParametersForTest(PN.pname("one-alfa-3"))
                    .addOption("alfa", "alfa-3", 3)
                .end()

                // sequence value is the same for all tests in the same stats
                .addSequencesForTest(PN.pname("one-alfa-1"))
                    .addOption("beta", "beta-10", 10)
                .end()
                .addSequencesForTest(PN.pname("one-alfa-2"))
                    .addOption("beta", "beta-10", 10)
                .end()
                .addSequencesForTest(PN.pname("one-alfa-3"))
                    .addOption("beta", "beta-10", 10)
                .end()

                .buildWithCoincidentalValues(Magnitude.UNIT)
                .getFirstStatsHolder()
                .getStats()
                .as(Magnitude.UNIT);

        StatsExpression<?> expression = new StatsExpression<>();
        expression.addExpression(PN.pname("first"))
                .addTest(PN.CURRENT).factors()
                    .multiplyBy("alfa")
                    .divideBy("beta")
                .end()
                .endExpression();


        Map<PathName, DimensionalMeasure> dimensionalMeasureMap =
                createMixedMap(stats, expression);

        Stats eStats = new Stats(stats.getStatsType(),
                        dimensionalMeasureMap, Magnitude.UNIT,
                        stats.getPayloadMap(), null);

//        System.out.println("EXPR:\n" + expression.toString());
//        System.out.println("\nstats:\n" + stats.toString());
//        System.out.println("\n\nestats:\n" + eStats.toString());

        assertEquals("first: [current] * alfa / (beta) ",
                expression.toString());

        assertEquals(3, stats.getMeasureMap().size());
        assertEquals(10.0,
                stats.getMeasure(PN.pname("first", "one-alfa-1")).getMean(), 0);
        assertEquals(20.0,
                stats.getMeasure(PN.pname("first", "one-alfa-2")).getMean(), 0);
        assertEquals(30.0,
                stats.getMeasure(PN.pname("first", "one-alfa-3")).getMean(), 0);

        assertEquals(6, eStats.getMeasureMap().size());
        assertEquals(10.0,
                eStats.getMeasure(PN.pname("first", "one-alfa-1")).getMean(), 0);
        assertEquals(20.0,
                eStats.getMeasure(PN.pname("first", "one-alfa-2")).getMean(), 0);
        assertEquals(30.0,
                eStats.getMeasure(PN.pname("first", "one-alfa-3")).getMean(), 0);
        assertEquals(1.0,
                eStats.getMeasure(PN.pname("first", "one-alfa-1*")).getMean(), 0);
        assertEquals(4.0,
                eStats.getMeasure(PN.pname("first", "one-alfa-2*")).getMean(), 0);
        assertEquals(9.0,
                eStats.getMeasure(PN.pname("first", "one-alfa-3*")).getMean(), 0);
    }


    private static Map<PathName,DimensionalMeasure> createMixedMap(
            Stats stats,
            ExpressionSolver expression) {
        StatsType type = stats.getStatsType();
        Map<PathName, DimensionalMeasure> measures = stats.getMeasureMap();

        if (measures.isEmpty()) {
            throw new RuntimeException("measures cannot be empty");
        }
        Unit<?> unit = measures.values().iterator().next().getUnit();
        Map<PathName,DimensionalMeasure> map = new LinkedHashMap<>(measures);
        expression.solve(stats)
                .forEach((CharSequence s, Measure m) ->
                    map.put(PN.pname(s), new OnlineDimensionalMeasure(unit, m)) );
        return Collections.unmodifiableMap(map);
    }

}
