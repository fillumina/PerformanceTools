package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedSequencePerformanceImpl<C, A extends AssertableMultiStats>
        implements PerformanceConsumer<Map<ComposedName, Map<ComposedName, A>>>,
            Assertion<Map<ComposedName, Map<ComposedName, A>>>,
            StringGenerator<Map<ComposedName, Map<ComposedName, A>>>,
            AssertParametrizedSequencePerformance<C, A> {

    private final C caller;
    private final Map<String,
            AssertParametrizedPerformanceImpl<AssertParametrizedSequencePerformanceImpl<C,A>,A>>
            map = new LinkedHashMap<>();
    private AssertParametrizedPerformanceImpl<AssertParametrizedSequencePerformanceImpl<C,A>,A>
            allParametrizedPerformanceAssertion;

    public static <A extends AssertableMultiStats>
            AssertParametrizedSequencePerformanceImpl<?,A> create() {
        return new AssertParametrizedSequencePerformanceImpl<>();
    }

    public AssertParametrizedSequencePerformanceImpl() {
        this(null);
    }

    public AssertParametrizedSequencePerformanceImpl(C caller) {
        this.caller = caller;
    }

    @Override
    public AssertParametrizedPerformance<AssertParametrizedSequencePerformanceImpl<C,A>,A>
            forAllSequences() {
        allParametrizedPerformanceAssertion =
                new AssertParametrizedPerformanceImpl<>(this);
        return allParametrizedPerformanceAssertion;
    }

    @Override
    public AssertParametrizedPerformance<AssertParametrizedSequencePerformanceImpl<C,A>,A>
            forSequenceValue(String sequence) {
        AssertParametrizedPerformanceImpl<AssertParametrizedSequencePerformanceImpl<C,A>,A>
                parametrizedPerformanceAssertion =
                    new AssertParametrizedPerformanceImpl<>(this);
        map.put(sequence, parametrizedPerformanceAssertion);
        return parametrizedPerformanceAssertion;
    }

    @Override
    public C endSequence() {
        return caller;
    }

    @Override
    public void check(Map<ComposedName, Map<ComposedName, A>> assertable) {
        consume(null, assertable);
    }

    private interface AssertionVisitor<A extends AssertableMultiStats> {
        void visit(AssertParametrizedPerformanceImpl<?, A> assertion,
                ComposedName name,
                Map<ComposedName, A> performance);
    }

    private void visitAssertions(
            Map<ComposedName, Map<ComposedName, A>> performances,
            AssertionVisitor<A> visitor) {
        if (performances == null) {
            return;
        }
        for (Map.Entry<ComposedName, Map<ComposedName, A>> entry :
                performances.entrySet()) {
            ComposedName testName = entry.getKey();
            Map<ComposedName, A> parametrizedStats = entry.getValue();

            AssertParametrizedPerformanceImpl<?,A> assertion =
                    map.get(testName.getLastName());
            if (assertion != null) {
                visitor.visit(assertion, testName, parametrizedStats);
            }
            if (allParametrizedPerformanceAssertion != null) {
                visitor.visit(allParametrizedPerformanceAssertion,
                        testName,
                        parametrizedStats);
            }
        }
    }

    @Override
    public void consume(ComposedName name,
            Map<ComposedName, Map<ComposedName, A>> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    AssertParametrizedPerformanceImpl<?,A> assertion,
                    ComposedName name,
                    Map<ComposedName, A> performance) {
                assertion.consume(name, performance);
            }
        });
    }

    @Override
    public String toString(Map<ComposedName, Map<ComposedName, A>> performance) {
        return toString(null, performance);
    }

    @Override
    public String toString(final ComposedName branch,
            Map<ComposedName, Map<ComposedName, A>> performances) {
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    AssertParametrizedPerformanceImpl<?,A> assertion,
                    ComposedName sequenceName,
                    Map<ComposedName, A> performance) {
                if (branch == null ||
                        branch.getFirstName().equals(sequenceName.getLastName())) {
                    buf.append(assertion.toString(branch, performance))
                            .append(System.lineSeparator());
                }
            }
        });
        return buf.toString();
    }
}
