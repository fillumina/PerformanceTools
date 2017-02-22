package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class ParameterizedSequenceAssertion<C, A extends Assertable>
        implements PerformanceConsumer<PHolder<PHolder<A>>>,
            Assertion<PHolder<PHolder<A>>>,
            StringGenerator<PHolder<PHolder<A>>>,
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
            ParameterizedSequenceAssertion<?,A> create() {
        return new ParameterizedSequenceAssertion<>();
    }

    public ParameterizedSequenceAssertion() {
        this(null);
    }

    public ParameterizedSequenceAssertion(C caller) {
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
                new ParameterizedAssertion<>(
                        (AssertParameterizedSequencePerformance<C,A>)this);
        return allParameterizedPerformanceAssertion;
    }

    @Override
    public AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A>
            forSequenceValue(String sequence) {
        final AssertParameterizedPerformance
                <AssertParameterizedSequencePerformance<C, A>, A> pa =
                    new ParameterizedAssertion<>(
                        (AssertParameterizedSequencePerformance<C,A>)this);
        map.put(sequence, pa);
        return pa;
    }

    @Override
    public void check(PHolder<PHolder<PHolder<A>>> assertable) {
        consume(assertable);
    }

    private interface AssertionVisitor<A extends Assertable> {

        void visit(ParameterizedAssertion<?, A> assertion,
                ComposedName name,
                PHolder<PHolder<A>> performance);
    }

    private void visitAssertions(
            PHolder<PHolder<PHolder<A>>> performances,
            AssertionVisitor<A> visitor) {
        if (performances == null) {
            return;
        }
        for (PHolder<PHolder<A>> parameterizedStats : performances) {
            ComposedName testName = parameterizedStats.getName();

            ParameterizedAssertion
                    <AssertParameterizedSequencePerformance<C, A>, A> assertion =
                    (ParameterizedAssertion
                    <AssertParameterizedSequencePerformance<C, A>, A>)
                    map.get(testName.getLastName());

            if (assertion != null) {
                visitor.visit(assertion, testName, parameterizedStats);
            }

            if (allParameterizedPerformanceAssertion != null) {
                visitor.visit((ParameterizedAssertion<?, A> )
                            allParameterizedPerformanceAssertion,
                        testName,
                        parameterizedStats);
            }
        }
    }

    @Override
    public void consume(PHolder<PHolder<PHolder<A>>> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    ParameterizedAssertion<?,A> assertion,
                    ComposedName name,
                    PHolder<PHolder<A>> performance) {
                assertion.consume(performance);
            }
        });
    }

    @Override
    public String toString(PHolder<PHolder<PHolder<A>>> performances) {
        final ComposedName branch = performances.getName();
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    ParameterizedAssertion<?,A> assertion,
                    ComposedName sequenceName,
                    PHolder<PHolder<A>> performance) {
                if (branch == null || branch.isEmpty() ||
                        branch.getFirstName().equals(sequenceName.getLastName())) {
                    buf.append(assertion.toString(performance))
                            .append(System.lineSeparator());
                }
            }
        });
        return buf.toString();
    }
}
