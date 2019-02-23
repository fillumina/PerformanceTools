package com.fillumina.performance.executor.param;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.executor.annotation.Sequence;
import com.fillumina.performance.executor.stats.MixedStatsHolder;
import com.fillumina.performance.executor.stats.Stats;
import com.fillumina.performance.executor.stats.StatsHolder;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.mock.NameStatsProducerMock;
import com.fillumina.performance.util.Holder;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.pathname.PathName;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SequencedParametrizedMixedTest {

    private boolean OUTPUT = false;

    public static void main(final String[] args) {
        SequencedParametrizedMixedTest test =
                new SequencedParametrizedMixedTest();
        test.OUTPUT = true;
        test.shouldProduceMixedResults();
        test.shouldProduceMixedResultsWith2ParamsAnd2Sequences();
    }

    public static class RunnableImpl implements Runnable {
        @Sequence private String name;
        @Param private int age;

        @Override public void run() {}
    }

    @Test
    public void shouldProduceMixedResults() {
        NameStatsProducerMock statsProducer = new NameStatsProducerMock();

        LinkedTree<String,Object> sequences =
                LinkedTree.<String,Object>builder()
                            .branch("name")
                                .leaf("bob", "Bob")
                                .leaf("tom", "Tom")
                            .end()
                            .getRoot();

        LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("age")
                                .leaf("young", 10)
                                .leaf("adult", 40)
                            .end()
                            .getRoot();

        SequencedTestProducer sequencedProducer =
                new SequencedTestProducer(sequences);

        ParameterizedTestProducer parameterizedProducer =
                new ParameterizedTestProducer(params);

        parameterizedProducer.instrument(statsProducer);
        sequencedProducer.instrument(parameterizedProducer);

        sequencedProducer.addTest("swim", new RunnableImpl());
        sequencedProducer.addTest("bike", new RunnableImpl());

        sequencedProducer.setName("2017");

        MixedStatsHolder mixedHolder = sequencedProducer.execute();
        mixedHolder.printIf(OUTPUT);

        StatsHolder holder = mixedHolder.getStatsHolder(MockStatsType.INSTANCE);

        LinkedTree<PathName,Stats> statsTree = holder.getTree();
        assertEquals(2, statsTree.getHeight());
        assertEquals(2, statsTree.size());

        // first list: stats taken
        // second list: tests in the same stats
        List<List<CharSequence>> tree = statsProducer.getTree();
        print(tree);
        assertEquals("samples taken", 4, tree.size());

        assertEquals(PN.pname("2017", "bob", "swim", "young"), tree.get(0).get(0));
        assertEquals(PN.pname("2017", "bob", "swim", "adult"), tree.get(0).get(1));

        assertEquals(PN.pname("2017", "bob", "bike", "young"), tree.get(1).get(0));
        assertEquals(PN.pname("2017", "bob", "bike", "adult"), tree.get(1).get(1));

        assertEquals(PN.pname("2017", "tom", "swim", "young"), tree.get(2).get(0));
        assertEquals(PN.pname("2017", "tom", "swim", "adult"), tree.get(2).get(1));

        assertEquals(PN.pname("2017", "tom", "bike", "young"), tree.get(3).get(0));
        assertEquals(PN.pname("2017", "tom", "bike", "adult"), tree.get(3).get(1));
    }


    public static class TwoRunnableImpl implements Runnable {
        @Sequence private String name;
        @Sequence private String season;

        @Param private int age;
        @Param private String type;

        @Override public void run() {}
    }

    @Test
    public void shouldProduceMixedResultsWith2ParamsAnd2Sequences() {
        NameStatsProducerMock statsProducer = new NameStatsProducerMock();

        LinkedTree<String,Object> sequences =
                LinkedTree.<String,Object>builder()
                            .branch("name")
                                .leaf("bob", "Bob")
                                .leaf("tom", "Tom")
                            .end()
                            .branch("season")
                                .leaf("summer", "summer")
                                .leaf("winter", "winter")
                            .end()
                            .getRoot();

        LinkedTree<String,Object> params =
                LinkedTree.<String,Object>builder()
                            .branch("age")
                                .leaf("young", 10)
                                .leaf("adult", 40)
                            .end()
                            .branch("type")
                                .leaf("pro", "pro")
                                .leaf("beg", "beg")
                            .end()
                            .getRoot();

        SequencedTestProducer sequencedProducer =
                new SequencedTestProducer(sequences);

        ParameterizedTestProducer parameterizedProducer =
                new ParameterizedTestProducer(params);

        parameterizedProducer.instrument(statsProducer);
        sequencedProducer.instrument(parameterizedProducer);

        sequencedProducer.addTest("swim", new TwoRunnableImpl());
        sequencedProducer.addTest("bike", new TwoRunnableImpl());

        sequencedProducer.setName("2017");

        MixedStatsHolder mixedHolder = sequencedProducer.execute();
        mixedHolder.printIf(OUTPUT);

        StatsHolder holder = mixedHolder.getStatsHolder(MockStatsType.INSTANCE);

        LinkedTree<PathName,Stats> statsTree = holder.getTree();
        assertEquals(2, statsTree.getHeight());
        assertEquals(4, statsTree.size());

        // first list: stats taken
        // second list: tests in the same stats
        List<List<CharSequence>> tree = statsProducer.getTree();
        print(tree);
        assertEquals("samples taken", 8, tree.size());

        eq(tree, 0, 0, "bob", "summer", "swim", "young", "pro");
        eq(tree, 0, 1, "bob", "summer", "swim", "adult", "pro");
        eq(tree, 0, 2, "bob", "summer", "swim", "young", "beg");
        eq(tree, 0, 3, "bob", "summer", "swim", "adult", "beg");

        eq(tree, 1, 0, "bob", "summer", "bike", "young", "pro");
        eq(tree, 1, 1, "bob", "summer", "bike", "adult", "pro");
        eq(tree, 1, 2, "bob", "summer", "bike", "young", "beg");
        eq(tree, 1, 3, "bob", "summer", "bike", "adult", "beg");

        eq(tree, 2, 0, "tom", "summer", "swim", "young", "pro");
        eq(tree, 2, 1, "tom", "summer", "swim", "adult", "pro");
        eq(tree, 2, 2, "tom", "summer", "swim", "young", "beg");
        eq(tree, 2, 3, "tom", "summer", "swim", "adult", "beg");

        eq(tree, 3, 0, "tom", "summer", "bike", "young", "pro");
        eq(tree, 3, 1, "tom", "summer", "bike", "adult", "pro");
        eq(tree, 3, 2, "tom", "summer", "bike", "young", "beg");
        eq(tree, 3, 3, "tom", "summer", "bike", "adult", "beg");

        eq(tree, 4, 0, "bob", "winter", "swim", "young", "pro");
        eq(tree, 4, 1, "bob", "winter", "swim", "adult", "pro");
        eq(tree, 4, 2, "bob", "winter", "swim", "young", "beg");
        eq(tree, 4, 3, "bob", "winter", "swim", "adult", "beg");

        eq(tree, 5, 0, "bob", "winter", "bike", "young", "pro");
        eq(tree, 5, 1, "bob", "winter", "bike", "adult", "pro");
        eq(tree, 5, 2, "bob", "winter", "bike", "young", "beg");
        eq(tree, 5, 3, "bob", "winter", "bike", "adult", "beg");

        eq(tree, 6, 0, "tom", "winter", "swim", "young", "pro");
        eq(tree, 6, 1, "tom", "winter", "swim", "adult", "pro");
        eq(tree, 6, 2, "tom", "winter", "swim", "young", "beg");
        eq(tree, 6, 3, "tom", "winter", "swim", "adult", "beg");

        eq(tree, 7, 0, "tom", "winter", "bike", "young", "pro");
        eq(tree, 7, 1, "tom", "winter", "bike", "adult", "pro");
        eq(tree, 7, 2, "tom", "winter", "bike", "young", "beg");
        eq(tree, 7, 3, "tom", "winter", "bike", "adult", "beg");
    }

    private void eq(List<List<CharSequence>> tree,
            int stats, int test, String... pname) {
        PathName n = PN.pname("2017").append(pname);
        assertEquals(n, tree.get(stats).get(test));
    }

    private void print(List<List<CharSequence>> tree) {
        if (OUTPUT) {
            Holder.Integer index = new Holder.Integer();
            tree.forEach(l -> {
                System.out.println("stats= " + index.getValue());
                index.incrementAndGet();
                l.forEach(c -> {
                    System.out.println("name= " + c);
                });
                System.out.println("");
            });
        }
    }
}
