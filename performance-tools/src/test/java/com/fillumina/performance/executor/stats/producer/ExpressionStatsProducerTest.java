package com.fillumina.performance.executor.stats.producer;

import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.StatsExpression;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.mock.StatsProducerMock;
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

        StatsHolder stats = mixedHolder.getHolder(MockStatsType.INSTANCE);

        stats.check()
                .tolerance(Ratio.P_05)
                .value("one").equalsTo(10.0)
                .percentage().string("two").end().equalsTo(Ratio.P_100)
                .value("expr").equalsTo(10.0)
                .order().string("expr").end().equalsTo().string("one").end()
                .end();
    }
}
