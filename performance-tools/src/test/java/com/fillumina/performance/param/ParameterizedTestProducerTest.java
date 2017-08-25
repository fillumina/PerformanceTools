package com.fillumina.performance.param;

import com.fillumina.performance.annotation.Param;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mock.StatsProducerMock;
import com.fillumina.performance.util.ReflectionHelper;
import com.fillumina.performance.util.tname.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedTestProducerTest {
    private boolean printout;

    @Test
    public void shouldSubstituteSingleParameterInSingleClass() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .getRoot();

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            });

        LinkedTree<TName, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(1, exec.size());

        Tree<TName, Runnable> one = exec.getTree(TN.tname("one"));
        assertValues(one.get(TN.tname("one", "linked")), LinkedList.class, 0);
        assertValues(one.get(TN.tname("one", "array")), ArrayList.class, 0);
    }

    @Test
    public void shouldSubstituteParameter() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .branch("size")
                                .leaf("10", 10)
                                .leaf("100", 100)
                            .end()
                            .getRoot();

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            },
                        "two", new Runnable() {
                                @Param
                                private List<String> list;

                                @Param
                                private int size;

                                @Override
                                public void run() {}
                            }
                );

        LinkedTree<TName, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());

        checkTree(exec);
    }

    public static class InnerRunnable implements Runnable {
        @Param
        private List<String> list;

        @Param
        private int size;

        @Override
        public void run() {}
    }

    @Test
    public void shouldSubstituteParameterInExtendedClass() {
        final LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("list")
                                .leaf("linked", new LinkedList<>())
                                .leaf("array", new ArrayList<>())
                            .end()
                            .branch("size")
                                .leaf("10", 10)
                                .leaf("100", 100)
                            .end()
                            .getRoot();

        final LinkedMap<String,Runnable> tests =
                LinkedMap.<String,Runnable>create(
                        "one", new InnerRunnable(),
                        "two", new InnerRunnable());

        LinkedTree<TName, Runnable> exec = getExecutedTests(tests, params);

        if (printout) {
            printTree(exec);
        }

        assertEquals(2, exec.size());


        checkTree(exec);
    }

    private void checkTree(LinkedTree<TName, Runnable> exec) {
        Tree<TName, Runnable> one = exec.getTree(TN.tname("one"));
        assertValues(
                one.get(TN.tname("one", "linked", "10")),
                LinkedList.class, 10);
        assertValues(
                one.get(TN.tname("one", "linked", "100")),
                LinkedList.class, 100);
        assertValues(
                one.get(TN.tname("one", "array", "10")),
                ArrayList.class, 10);
        assertValues(
                one.get(TN.tname("one", "array", "100")),
                ArrayList.class, 100);

        Tree<TName, Runnable> two = exec.getTree(TN.tname("two"));
        assertValues(
                two.get(TN.tname("two", "linked", "10")),
                LinkedList.class, 10);
        assertValues(
                two.get(TN.tname("two", "linked", "100")),
                LinkedList.class, 100);
        assertValues(
                two.get(TN.tname("two", "array", "10")),
                ArrayList.class, 10);
        assertValues(
                two.get(TN.tname("two", "array", "100")),
                ArrayList.class, 100);
    }

    private void assertValues(Runnable runnable, Class<?> clazz, int size) {
        final Object listFieldValue =
                ReflectionHelper.getFieldValue(runnable, "list");
        final Object sizeFieldValue =
                ReflectionHelper.getFieldValue(runnable, "size");

        assertEquals(clazz, listFieldValue.getClass());
        assertEquals(size, sizeFieldValue);
    }

    private void printTree(LinkedTree<TName, Runnable> exec) {
        for (Tree<TName, Runnable> entry : exec) {
            System.out.println("");
            System.out.println("test= " + entry.getKey());

            for (Entry<TName, Runnable> e : entry) {
                final Object listFieldValue =
                        ReflectionHelper.getFieldValue(e.getValue(), "list");
                final Object sizeFieldValue =
                        ReflectionHelper.getFieldValue(e.getValue(), "size");

                System.out.println(e.getKey() +
                        "\t" + listFieldValue.getClass().getCanonicalName() +
                        "\t" + sizeFieldValue);
            }
        }
    }

    private LinkedTree<TName, Runnable> getExecutedTests(
            LinkedMap<String,Runnable> tests,
            LinkedTree<String,Object> params) {
        StatsProducerMock statsProducer = new StatsProducerMock();
        ParameterizedTestProducer parameterizedTestProducer =
                new ParameterizedTestProducer(params);
        parameterizedTestProducer.instrument(statsProducer);
        for (Entry<String,Runnable> entry : tests) {
            parameterizedTestProducer.addTest(entry.getKey(), entry.getValue());
        }
        parameterizedTestProducer.execute();
        return statsProducer.getExecutedTests();
    }

    public static void main(final String[] args) {
        final ParameterizedTestProducerTest test =
                new ParameterizedTestProducerTest();
        test.printout = true;
        test.shouldSubstituteSingleParameterInSingleClass();
//        test.shouldSubstituteParameter();
//        test.shouldSubstituteParameterInExtendedClass();
    }
}
