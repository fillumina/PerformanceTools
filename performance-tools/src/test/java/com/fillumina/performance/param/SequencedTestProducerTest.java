package com.fillumina.performance.param;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.infrastructure.annotation.Sequence;
import com.fillumina.performance.mock.MockStatsProducer;
import com.fillumina.performance.util.ReflectionHelper;
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

        LinkedTree<String, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        assertValues(exec.getTree("P95").get("one"), Ratio.P_95, 0);
        assertValues(exec.getTree("P99").get("one"), Ratio.P_99, 0);
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

        LinkedTree<String, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(4, exec.size());

        assertValueForSequence(exec, "P95_10", Ratio.P_95, 10);
        assertValueForSequence(exec, "P95_100", Ratio.P_95, 100);
        assertValueForSequence(exec, "P99_10", Ratio.P_99, 10);
        assertValueForSequence(exec, "P99_100", Ratio.P_99, 100);
    }

    private void assertValueForSequence(Tree<String, Runnable> tree, String key,
            Ratio ratio, int size) {
        Tree<String, Runnable> subTree = tree.getTree(key);
        assertValues(subTree.get("one"), ratio, size);
        assertValues(subTree.get("two"), ratio, size);
    }

    private void assertValues(Runnable runnable, Ratio ratio, int size) {
        final Ratio ratioFieldValue = (Ratio)
                ReflectionHelper.getFieldValue(runnable, "ratio");
        final int sizeFieldValue = (int)
                ReflectionHelper.getFieldValue(runnable, "size");

        assertEquals(ratio, ratioFieldValue);
        assertEquals(size, sizeFieldValue, 0);
    }

    private void printTree(LinkedTree<String, Runnable> exec) {
        for (Tree<String, Runnable> entry : exec) {
            System.out.println("");
            System.out.println("test: " + entry.getKey());

            for (Entry<String, Runnable> e : entry) {
                final Ratio ratioFieldValue = (Ratio)
                        ReflectionHelper.getFieldValue(e.getValue(), "ratio");
                final int sizeFieldValue = (int)
                        ReflectionHelper.getFieldValue(e.getValue(), "size");

                System.out.println(e.getKey() +
                        "\t" + ratioFieldValue.getPercentage() +
                        "\t" + sizeFieldValue);
            }
        }
    }

    private LinkedTree<String, Runnable> getExecutedTests(
            LinkedMap<String,Runnable> tests,
            LinkedTree<String,Object> sequence) {
        MockStatsProducer<Assertable> statsProducer = new MockStatsProducer<>();
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
