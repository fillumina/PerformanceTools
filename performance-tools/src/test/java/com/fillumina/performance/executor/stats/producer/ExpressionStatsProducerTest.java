package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsExpression;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.stats.Ratio;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ExpressionStatsProducerTest {

    @Test
    public void shouldAddExpressionToResultStats() {
        StatsExpression<?> expression = new StatsExpression<>();
        expression.addExpression("expr")
                .addTest("two").subtractTest("one");

        MixedStatsHolder mixedHolder =
                new StatsProducerMock<>("one", 10.0, "two", 20.0)
                    .instrumentedBy(new ExpressionStatsProducer(expression))
                        .addTest("one", NullRunnable.INSTANCE)
                        .addTest("two", NullRunnable.INSTANCE)
                        .execute();

        //mixedHolder.print();

        StatsHolder stats = mixedHolder.getStatsHolder(MockStatsType.INSTANCE);

        stats.check()
                .tolerance(Ratio.P_05)
                .value("one").equalsTo(10.0)
                .percentage().string("two").end().equalsTo(Ratio.P_100)
                .value("expr").equalsTo(10.0)
                .order().string("expr").end().equalsTo().string("one").end()
                .end();
    }

    @Test
    public void shouldAddExpressionToParameterizedResultStats() {
        StatsExpression<?> expression = new StatsExpression<>();
        expression.addExpression("expr")
                .addTest("first", "two").subtractTest("first", "one");

        LinkedTree<String,Object> params = LinkedTree.<String,Object>builder()
                .branch("param")
                    .leaf("one", 1)
                    .leaf("two", 2)
                .getRoot();

        ParameterizedTestProducer parameterizer =
                new ParameterizedTestProducer(params);

        MixedStatsHolder mixedHolder =
                new StatsProducerMock<>(
                                        PN.pname("first", "one"), 10.0,
                                        PN.pname("first", "two"), 20.0)
                        .instrumentedBy(parameterizer)
                        .instrumentedBy(new ExpressionStatsProducer(expression))
                            .addTest("first", new Runnable() {
                                @Param int param;
                                @Override
                                public void run() {
                                }
                            })
                            .execute();

        //mixedHolder.print();

        StatsHolder stats = mixedHolder.getStatsHolder(MockStatsType.INSTANCE);

        stats.check()
                .tolerance(Ratio.P_05)
                .value("first", "one").equalsTo(10.0)
                .percentage().string("first", "two").end().equalsTo(Ratio.P_100)
                .value("expr").equalsTo(10.0)
                .order().string("expr").end().equalsTo().string("first", "one").end()
                .end();
    }
}
