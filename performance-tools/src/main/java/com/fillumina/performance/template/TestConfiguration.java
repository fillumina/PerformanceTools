package com.fillumina.performance.template;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.TestContainer;
import com.fillumina.performance.executor.param.ParameterizedTestProducer;
import com.fillumina.performance.executor.param.SequencedTestProducer;
import com.fillumina.performance.executor.param.SubTreeBuilder;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.collection.LinkedTree;
import com.fillumina.performance.util.collection.Tree;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.tname.TName;
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
                TestContainer<TestConfiguration<C>,Runnable> {

    private static final String TAB = "    ";
    private static final String CRLF = System.lineSeparator();

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
    public TestConfiguration<C> ignoreTest(TName name, Runnable test) {
        return this;
    }

    @Override
    public TestConfiguration<C> addTest(Runnable test) {
        return addTest("test_" + Integer.toString(tests.size()), test);
    }

    @Override
    public TestConfiguration<C> addTest(String name, Runnable test) {
        tests.put(TN.tname(name), test);
        return this;
    }

    @Override
    public TestConfiguration<C> addTest(TName name, Runnable test) {
        tests.put(name, test);
        return this;
    }

    @Override
    public TestConfiguration<C> clearTests() {
        tests.clear();
        return this;
    }

    public SubTreeBuilder<TestConfiguration<C>> parameters() {
        return new SubTreeBuilder<>(this, parameters);
    }

    public SubTreeBuilder<TestConfiguration<C>>.Value addParameter(
            String paramName) {
        return new SubTreeBuilder<>(this, parameters).name(paramName);
    }

    @Override
    public LinkedTree<String, Object> getParameters() {
        return parameters;
    }

    public SubTreeBuilder<TestConfiguration<C>> sequences() {
        return new SubTreeBuilder<>(this, sequences);
    }

    public SubTreeBuilder<TestConfiguration<C>>.Value addSequence(
            String sequenceName) {
        return new SubTreeBuilder<>(this, sequences).name(sequenceName);
    }

    @Override
    public LinkedTree<String, Object> getSequences() {
        return sequences;
    }

    public String toString(String testName) {
        return toStringTests(tests) +
                toStringTree(sequences, "sequences") +
                toStringTree(parameters, "parameters") +
                toStringParameterizedTree(testName);
    }

    public static String toStringTests(LinkedMap<TName,Runnable> tests) {
        StringBuilder buf = new StringBuilder();
        buf.append("tests:").append(CRLF);
        for (TName name : tests.keySet()) {
            buf.append(' ').append(name.toString())
                    .append(CRLF);
        }
        buf.append(CRLF);
        return buf.toString();
    }

    public static String toStringTree(
            LinkedTree<String, Object> tree, String type) {
        if (tree == null || tree.isNull()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        buf.append(type).append(":").append(CRLF);
        TableFormatter table = new TableFormatter();
        for (Tree<String,Object> t : tree) {
            table.cell().cell(t.getKey()).cell(t.getValue()).endl();
        }
        table.appendToCatchingIOException(buf);
        buf.append(CRLF);
        return buf.toString();
    }

    public String toStringParameterizedTree(String testName) {
        if (sequences.isEmpty() && parameters.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("tree:").append(CRLF);
        String tab = "";
        if (testName != null && !testName.isEmpty()) {
            buf.append(testName).append(CRLF);
            tab = TAB;
        }
        if (sequences.isEmpty()) {
            getTestsParamsTree(buf, tab);
        } else {
            for (Tree<String,Object> seq : sequences) {
                String seqName = seq.getKey();
                for (Tree<String,Object> s : seq) {
                    buf.append(seqName)
                            .append("=")
                            .append(s.getKey())
                            .append(CRLF);
                    getTestsParamsTree(buf, tab + TAB);
                }
            }
        }
        return buf.toString();
    }

    private void getTestsParamsTree(StringBuilder buf, String tab) {
        for (TName test : tests.keySet()) {
            buf.append(tab).append(test.toString()).append(CRLF);
            for (Tree<String,Object> param : parameters) {
                String paramName = param.getKey();
                buf.append(tab).append(TAB)
                        .append(paramName)
                        .append("=")
                        .append(param.keySet().toString())
                        .append(CRLF);
            }
        }
    }

    @Override
    public TestConfiguration<C> build() {
        return this;
    }
}
