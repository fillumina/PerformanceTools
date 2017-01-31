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
public class AssertParameterizedSequencePerformanceImpl<C, A extends AssertableMultiStats>
        implements PerformanceConsumer<Map<ComposedName, Map<ComposedName, A>>>,
            Assertion<Map<ComposedName, Map<ComposedName, A>>>,
            StringGenerator<Map<ComposedName, Map<ComposedName, A>>>,
            AssertParameterizedSequencePerformance<C, A> {

    private final C caller;
    private final Map<String,
            AssertParameterizedPerformanceImpl<AssertParameterizedSequencePerformanceImpl<C,A>,A>>
            map = new LinkedHashMap<>();
    private AssertParameterizedPerformanceImpl<AssertParameterizedSequencePerformanceImpl<C,A>,A>
            allParameterizedPerformanceAssertion;

    public static <A extends AssertableMultiStats>
            AssertParameterizedSequencePerformanceImpl<?,A> create() {
        return new AssertParameterizedSequencePerformanceImpl<>();
    }

    public AssertParameterizedSequencePerformanceImpl() {
        this(null);
    }

    public AssertParameterizedSequencePerformanceImpl(C caller) {
        this.caller = caller;
    }

    @Override
    public AssertParameterizedPerformance<AssertParameterizedSequencePerformanceImpl<C,A>,A>
            forAllSequences() {
        allParameterizedPerformanceAssertion =
                new AssertParameterizedPerformanceImpl<>(this);
        return allParameterizedPerformanceAssertion;
    }

    @Override
    public AssertParameterizedPerformance<AssertParameterizedSequencePerformanceImpl<C,A>,A>
            forSequenceValue(String sequence) {
        AssertParameterizedPerformanceImpl<AssertParameterizedSequencePerformanceImpl<C,A>,A>
                parameterizedPerformanceAssertion =
                    new AssertParameterizedPerformanceImpl<>(this);
        map.put(sequence, parameterizedPerformanceAssertion);
        return parameterizedPerformanceAssertion;
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
        void visit(AssertParameterizedPerformanceImpl<?, A> assertion,
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
            Map<ComposedName, A> parameterizedStats = entry.getValue();

            AssertParameterizedPerformanceImpl<?,A> assertion =
                    map.get(testName.getLastName());
            if (assertion != null) {
                visitor.visit(assertion, testName, parameterizedStats);
            }
            if (allParameterizedPerformanceAssertion != null) {
                visitor.visit(allParameterizedPerformanceAssertion,
                        testName,
                        parameterizedStats);
            }
        }
    }

    @Override
    public void consume(ComposedName name,
            Map<ComposedName, Map<ComposedName, A>> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    AssertParameterizedPerformanceImpl<?,A> assertion,
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
                    AssertParameterizedPerformanceImpl<?,A> assertion,
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
