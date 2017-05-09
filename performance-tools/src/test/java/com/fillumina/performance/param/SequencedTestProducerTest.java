package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.infrastructure.annotation.Sequence;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.ReflectionHelper;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.stats.Ratio;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedTestProducerTest {
    private boolean printout;

    @Test
    public void shouldSubstituteSinlgeParameterSingleTest() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("confidence")
                                .leaf("P95", Ratio.P_95)
                                .leaf("P99", Ratio.P_99)
                            .end()
                            .getRoot();

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Sequence("confidence")
                                private Ratio ratio;

                                @Sequence
                                private int size;

                                @Override
                                public void run() {}
                            });

        LinkedTree<TName, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        assertValues(exec
                    .getTree(TN.tname("P95"))
                    .get(TN.tname("P95", "one")),
                Ratio.P_95, 0);
        assertValues(exec
                    .getTree(TN.tname("P99"))
                    .get(TN.tname("P99", "one")),
                Ratio.P_99, 0);
    }

    @Test
    public void shouldSubstituteParameter() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("confidence")
                                .leaf("P95", Ratio.P_95)
                                .leaf("P99", Ratio.P_99)
                            .end()
                            .branch("size")
                                .leaf("10", 10)
                                .leaf("100", 100)
                            .end()
                            .getRoot();

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Sequence("confidence")
                                private Ratio ratio;

                                @Sequence
                                private int size;

                                @Override
                                public void run() {}
                            },
                        "two", new Runnable() {
                                @Sequence("confidence")
                                private Ratio ratio;

                                @Sequence
                                private int size;

                                @Override
                                public void run() {}
                            }
                );

        LinkedTree<TName, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(4, exec.size());

        assertValueForSequence(exec, TN.tname("P95", "10"), Ratio.P_95, 10);
        assertValueForSequence(exec, TN.tname("P95", "100"), Ratio.P_95, 100);
        assertValueForSequence(exec, TN.tname("P99", "10"), Ratio.P_99, 10);
        assertValueForSequence(exec, TN.tname("P99", "100"), Ratio.P_99, 100);
    }

    private void assertValueForSequence(Tree<TName, Runnable> tree,
            TName key,
            Ratio ratio,
            int size) {
        Tree<TName, Runnable> subTree = tree.getTree(key);
        assertValues(subTree.get(key.append("one")), ratio, size);
        assertValues(subTree.get(key.append("two")), ratio, size);
    }

    private void assertValues(Runnable runnable, Ratio ratio, int size) {
        final Ratio ratioFieldValue = (Ratio)
                ReflectionHelper.getFieldValue(runnable, "ratio");
        final int sizeFieldValue = (int)
                ReflectionHelper.getFieldValue(runnable, "size");

        assertEquals(ratio, ratioFieldValue);
        assertEquals(size, sizeFieldValue, 0);
    }

    private void printTree(LinkedTree<TName, Runnable> exec) {
        for (Tree<TName, Runnable> entry : exec) {
            System.out.println("");
            System.out.println("test= '" + entry.getKey() + "'");

            for (Entry<TName, Runnable> e : entry) {
                final Ratio ratioFieldValue = (Ratio)
                        ReflectionHelper.getFieldValue(e.getValue(), "ratio");
                final int sizeFieldValue = (int)
                        ReflectionHelper.getFieldValue(e.getValue(), "size");

                System.out.println(e.getKey() +
                        "\t\t" + ratioFieldValue.getPercentage() +
                        "\t" + sizeFieldValue);
            }
        }
    }

    private LinkedTree<TName, Runnable> getExecutedTests(
            LinkedMap<String,Runnable> tests,
            LinkedTree<String,Object> sequence) {
        StatsProducerMock<Assertable> statsProducer = new StatsProducerMock<>();
        SequencedTestProducer<Assertable> sequencedTestProducer =
                new SequencedTestProducer<>(sequence);
        sequencedTestProducer.instrument(statsProducer);
        for (Entry<String,Runnable> entry : tests) {
            sequencedTestProducer.addTest(entry.getKey(), entry.getValue());
        }
        sequencedTestProducer.execute();
        return statsProducer.getExecutedTests();
    }

    public static void main(final String[] args) {
        SequencedTestProducerTest test = new SequencedTestProducerTest();
        test.printout = true;
        test.shouldSubstituteParameter();
    }
}
