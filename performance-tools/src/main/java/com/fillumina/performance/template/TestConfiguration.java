package com.fillumina.performance.template;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.infrastructure.TestContainer;
import com.fillumina.performance.param.ParameterizedTestProducer;
import com.fillumina.performance.param.ParametersBuilder;
import com.fillumina.performance.param.SequencedBuilder;
import com.fillumina.performance.param.SequencedTestProducer;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.formatter.TableFormatter;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestConfiguration<C>
        extends CallBackBuilder<C, TestConfiguration<C>>
        implements
                ParameterizedTestProducer.Configuration,
                SequencedTestProducer.Configuration,
                TestContainer<Runnable> {

    private final LinkedMap<TName, Runnable> tests = new LinkedMap<>();
    private final LinkedTree<String, Object> parameters = new LinkedTree<>();
    private final LinkedTree<String, Object> sequences = new LinkedTree<>();

    public TestConfiguration() {
        super();
    }

    public TestConfiguration(C caller) {
        super(caller);
    }

    public TestConfiguration(Setter<C, TestConfiguration<C>> setter) {
        super(setter);
    }

    @Override
    public LinkedMap<TName, Runnable> getTests() {
        return tests.getUnmodifiableCopy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TestConfiguration<C> addTests(Map<TName, Runnable> tests) {
        this.tests.putAll(tests);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TestConfiguration<C> ignoreTest(String name, Runnable test) {
        return this;
    }

    @Override
    public TestContainer<Runnable> ignoreTest(TName name, Runnable test) {
        return this;
    }

    @Override
    public TestContainer<Runnable> addTest(String name, Runnable test) {
        tests.put(TN.tname(name), test);
        return this;
    }

    @Override
    public TestContainer<Runnable> addTest(TName name, Runnable test) {
        tests.put(name, test);
        return this;
    }

    @Override
    public TestContainer<Runnable> clearTests() {
        tests.clear();
        return this;
    }

    public ParametersBuilder<TestConfiguration<C>> parameters() {
        return new ParametersBuilder<>((builtObject) -> {
            parameters.merge(builtObject);
            return this;
        });
    }

    @Override
    public LinkedTree<String, Object> getParameters() {
        return parameters;
    }

    public SequencedBuilder<TestConfiguration<C>> sequences() {
        return new SequencedBuilder<>((builtObject) -> {
            sequences.merge(builtObject);
            return this;
        });
    }

    @Override
    public LinkedTree<String, Object> getSequences() {
        return sequences;
    }

    @Override
    public String toString() {
        return toStringTests(tests) +
                toStringTree(sequences, "sequences") +
                toStringTree(parameters, "parameters");
    }

    public static String toStringTests(LinkedMap<TName,Runnable> tests) {
        StringBuilder buf = new StringBuilder();
        buf.append("tests:").append(System.lineSeparator());
        for (TName name : tests.keySet()) {
            buf.append(name.toString()).append(System.lineSeparator());
        }
        return buf.toString();
    }

    public static String toStringTree(
            LinkedTree<String, Object> tree, String type) {
        StringBuilder buf = new StringBuilder();
        buf.append(type).append("s :").append(System.lineSeparator());
        TableFormatter table = new TableFormatter();
        for (Tree<String,Object> t : tree) {
            table.cell(type).endl()
                .cell().cell(t.getKey()).cell(t.getValue()).endl();
        }
        table.appendToCatchingIOException(buf);
        return buf.toString();
    }

    @Override
    public TestConfiguration<C> build() {
        return this;
    }
}
