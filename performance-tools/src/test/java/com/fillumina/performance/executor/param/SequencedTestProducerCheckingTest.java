package com.fillumina.performance.executor.param;

import com.fillumina.performance.assertion.OrderAssertionError;
import com.fillumina.performance.executor.AssertableHolder;
import com.fillumina.performance.executor.MixedAssertableHolder;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.annotation.Sequence;
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
public class SequencedTestProducerCheckingTest {
    private boolean OUTPUT = false;

    public static void main(final String[] args) {
        SequencedTestProducerCheckingTest test =
                new SequencedTestProducerCheckingTest();
        test.OUTPUT = true;
        test.init();
        test.shouldTheParamsBeAssignedToTests();
    }

    private static final TName ONE_A = TN.tname("one", "a");
    private static final TName ONE_B = TN.tname("one", "b");
    private static final TName TWO_A = TN.tname("two", "a");
    private static final TName TWO_B = TN.tname("two", "b");
    private static final TName A_THREE = TN.tname("a","three");

    public static class RunnableImpl implements Runnable {
        @Sequence private int value;

        @Override public void run() {}
    }

    private StatsProducerMock<Integer> statsProducer;
    private AssertableHolder<StatsMock> holder;

    @Before
    public void init() {
        statsProducer = new StatsProducerMock<Integer>(
                ONE_A, 10.0,
                ONE_B, 100.0,
                TWO_A, 20.0,
                TWO_B, 200.0,
                A_THREE, 30.0) {

            @Override
            public Integer evaluate(CharSequence testName, Runnable test) {
                return ((RunnableImpl)test).value;
            }

        };

        LinkedTree<String,Object> sequence =
                LinkedTree.<String,Object>builder()
                            .branch("value")
                                .leaf("one", 1)
                                .leaf("two", 2)
                            .end()
                            .getRoot();

        SequencedTestProducer producer =
                new SequencedTestProducer(sequence);

        producer.instrument(statsProducer);

        producer.addTest("a", new RunnableImpl());
        producer.addTest("b", new RunnableImpl());

        MixedAssertableHolder mixedHolder = producer.execute();
        mixedHolder.printIf(OUTPUT);

        holder = mixedHolder.getStats(StatsMock.class);
    }

    @Test
    public void shouldReturnTheGivenStats() {
        String err = holder.toString();

        StatsMock a = holder.getAssertable(TN.tname("one"));
        assertEquals(err, 10.0, a.getMeasure(ONE_A).getMean(), 0.1);
        assertEquals(err, 100.0, a.getMeasure(ONE_B).getMean(), 0.1);

        StatsMock b = holder.getAssertable(TN.tname("two"));
        assertEquals(err, 20.0, b.getMeasure(TWO_A).getMean(), 0.1);
        assertEquals(err, 200.0, b.getMeasure(TWO_B).getMean(), 0.1);
    }

    @Test
    public void shouldTheParamsBeAssignedToTests() {
        final List<Map<CharSequence, Integer>> tree =
                statsProducer.getEvaluatedTree();

        // first tree branch is the stats producer call counter (0, 1)
        // second tree branch is the value of param as recorded in evaluate()
        assertEquals(1, tree.get(0).get(ONE_A), 0);
        assertEquals(1, tree.get(0).get(ONE_B), 0);

        assertEquals(2, tree.get(1).get(TWO_A), 0);
        assertEquals(2, tree.get(1).get(TWO_B), 0);
    }

    @Test
    public void shouldAssertOrderCondition() {
        holder.check()
                .order().string("one", "a").end()
                .lessThan().string("one", "b").end();
    }

    @Test(expected = OrderAssertionError.class)
    public void shouldNotAssertOrderCondition() {
        holder.check()
                .order().string("one", "a").end()
                .greaterThan().string("one", "b").end();
    }

}
