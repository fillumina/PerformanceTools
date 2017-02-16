package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
//Map<ComposedName, Map<ComposedName, A>>
public class AssertParameterizedSequencePerformanceImpl<C, A extends Assertable>
        implements PerformanceConsumer<A>,
            Assertion<A>,
            StringGenerator<A>,
            AssertParameterizedSequencePerformance<C, A> {

    private final Map<String,
                AssertParameterizedPerformance
                    <AssertParameterizedSequencePerformance<C, A>, A>> map =
            new LinkedHashMap<>();

    private AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A>
            allParameterizedPerformanceAssertion;

    private C caller;

    public static <A extends Assertable>
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
    @SuppressWarnings("unchecked")
    public C endSequences() {
        if (caller == null) {
            return (C) this;
        }
        return caller;
    }

    @Override
    public AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A>
            forAllSequences() {
        allParameterizedPerformanceAssertion =
                new AssertParameterizedPerformanceImpl<>(
                        (AssertParameterizedSequencePerformance<C,A>)this);
        return allParameterizedPerformanceAssertion;
    }

    @Override
    public AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A>
            forSequenceValue(String sequence) {
        final AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A> pa =
                    new AssertParameterizedPerformanceImpl<>(
                        (AssertParameterizedSequencePerformance<C,A>)this);
        map.put(sequence, pa);
        return pa;
    }

    @Override
    public void check(PHolder<A> assertable) {
        consume(assertable);
    }

    private interface AssertionVisitor<A extends Assertable> {

        void visit(AssertParameterizedPerformanceImpl<?, A> assertion,
                ComposedName name,
                PHolder<A> performance);
    }

    private void visitAssertions(
            PHolder<A> performances,
            AssertionVisitor<A> visitor) {
        if (performances == null) {
            return;
        }
        for (PHolder<A> parameterizedStats : performances) {
            ComposedName testName = parameterizedStats.getName();

            AssertParameterizedPerformanceImpl
                    <AssertParameterizedSequencePerformance<C, A>, A> assertion =
                    (AssertParameterizedPerformanceImpl
                    <AssertParameterizedSequencePerformance<C, A>, A>)
                    map.get(testName.getLastName());

            if (assertion != null) {
                visitor.visit(assertion, testName, parameterizedStats);
            }

            if (allParameterizedPerformanceAssertion != null) {
                visitor.visit((AssertParameterizedPerformanceImpl<?, A> )
                            allParameterizedPerformanceAssertion,
                        testName,
                        parameterizedStats);
            }
        }
    }

    @Override
    public void consume(PHolder<A> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    AssertParameterizedPerformanceImpl<?,A> assertion,
                    ComposedName name,
                    PHolder<A> performance) {
                assertion.consume(performance);
            }
        });
    }

    @Override
    public String toString(PHolder<A> performances) {
        final ComposedName branch = performances.getName();
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    AssertParameterizedPerformanceImpl<?,A> assertion,
                    ComposedName sequenceName,
                    PHolder<A> performance) {
                if (branch == null ||
                        branch.getFirstName().equals(sequenceName.getLastName())) {
                    buf.append(assertion.toString(performance))
                            .append(System.lineSeparator());
                }
            }
        });
        return buf.toString();
    }
}
