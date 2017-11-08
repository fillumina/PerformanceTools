package com.fillumina.performance.executor.param;

import com.fillumina.performance.assertion.OrderAssertionError;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.mock.StatsMock;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.tname.TName;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducerCheckingTest {

    private boolean OUTPUT = false;

    public static void main(final String[] args) {
        ParameterizedTestProducerCheckingTest test =
                new ParameterizedTestProducerCheckingTest();
        test.OUTPUT = true;
        test.init();
        test.shouldTheParamsBeAssignedToTests();
    }

    private static final TName A_ONE = TN.tname("a","one");
    private static final TName A_TWO = TN.tname("a","two");
    private static final TName B_ONE = TN.tname("b","one");
    private static final TName B_TWO = TN.tname("b","two");
    private static final TName A_THREE = TN.tname("a","three");

    public static class RunnableImpl implements Runnable {
        @Param private int value;

        @Override public void run() {}
    }

    private StatsProducerMock<Integer> statsProducer;
    private AssertableHolder<StatsMock> holder;

    @Before
    public void init() {
        statsProducer = new StatsProducerMock<Integer>(
                A_ONE, 10.0,
                A_TWO, 20.0,
                B_ONE, 100.0,
                B_TWO, 200.0,
                A_THREE, 30.0) {

            @Override
            public Integer evaluate(CharSequence testName, Runnable test) {
                return ((RunnableImpl)test).value;
            }

        };

        LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("value")
                                .leaf("one", 1)
                                .leaf("two", 2)
                            .end()
                            .getRoot();

        ParameterizedTestProducer producer =
                new ParameterizedTestProducer(params);

        producer.instrument(statsProducer);

        producer.addTest("a", new RunnableImpl());
        producer.addTest("b", new RunnableImpl());

        MixedAssertableHolder mixedHolder = producer.execute();
        mixedHolder.printIf(OUTPUT);

        holder = mixedHolder.getStats(StatsMock.class);
    }

    @Test
    public void shouldReturnTheGivenStats() {
        StatsMock a = holder.getAssertable(TN.tname("a"));
        assertEquals(10.0, a.getMeasure(A_ONE).getMean(), 0.1);
        assertEquals(20.0, a.getMeasure(A_TWO).getMean(), 0.1);

        StatsMock b = holder.getAssertable(TN.tname("b"));
        assertEquals(100.0, b.getMeasure(B_ONE).getMean(), 0.1);
        assertEquals(200.0, b.getMeasure(B_TWO).getMean(), 0.1);
    }

    @Test
    public void shouldTheParamsBeAssignedToTests() {
        final List<Map<CharSequence, Integer>> tree =
                statsProducer.getEvaluatedTree();

        // first tree branch is the stats producer call counter (0, 1)
        // second tree branch is the value of param as recorded in evaluate()
        assertEquals(1, tree.get(0).get(A_ONE), 0);
        assertEquals(2, tree.get(0).get(A_TWO), 0);

        assertEquals(1, tree.get(1).get(B_ONE), 0);
        assertEquals(2, tree.get(1).get(B_TWO), 0);
    }

    @Test
    public void shouldAssertOrderCondition() {
        holder.check()
                .order().string("a", "one").end()
                .lessThan().string("a", "two").end();
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldNotAssertOrderCondition() {
        holder.check()
                .order().string("a", "one").end()
                .greaterThan().string("a", "two").end();
    }

}
