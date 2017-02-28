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
public class AssertParameterizedSequence<C, A extends Assertable>
        implements PerformanceConsumer<PHolder<PHolder<A>>>,
            Assertion<PHolder<PHolder<A>>>,
            StringGenerator<PHolder<PHolder<A>>>,
            ParameterizedSequenceAssertion<C, A> {

    private final Map<String,
                ParameterizedAssertion
                    <ParameterizedSequenceAssertion<C, A>, A>> map =
            new LinkedHashMap<>();

    private ParameterizedAssertion
                <ParameterizedSequenceAssertion<C, A>, A>
            allParameterizedPerformanceAssertion;

    private C caller;

    public static <A extends Assertable>
            AssertParameterizedSequence<?,A> create() {
        return new AssertParameterizedSequence<>();
    }

    public AssertParameterizedSequence() {
        this(null);
    }

    public AssertParameterizedSequence(C caller) {
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
    public ParameterizedAssertion
                <ParameterizedSequenceAssertion<C, A>, A>
            forAllSequences() {
        allParameterizedPerformanceAssertion =
                new AssertParameterized<>(
                        (ParameterizedSequenceAssertion<C,A>)this);
        return allParameterizedPerformanceAssertion;
    }

    @Override
    public ParameterizedAssertion
                <ParameterizedSequenceAssertion<C, A>, A>
            forSequenceValue(String sequence) {
        final ParameterizedAssertion
                <ParameterizedSequenceAssertion<C, A>, A> pa =
                    new AssertParameterized<>(
                        (ParameterizedSequenceAssertion<C,A>)this);
        map.put(sequence, pa);
        return pa;
    }

    @Override
    public void check(PHolder<PHolder<PHolder<A>>> assertable) {
        consume(assertable);
    }

    private interface AssertionVisitor<A extends Assertable> {

        void visit(AssertParameterized<?, A> assertion,
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

            AssertParameterized
                    <ParameterizedSequenceAssertion<C, A>, A> assertion =
                    (AssertParameterized
                    <ParameterizedSequenceAssertion<C, A>, A>)
                    map.get(testName.getLastName());

            if (assertion != null) {
                visitor.visit(assertion, testName, parameterizedStats);
            }

            if (allParameterizedPerformanceAssertion != null) {
                visitor.visit((AssertParameterized<?, A> )
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
                    AssertParameterized<?,A> assertion,
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
                    AssertParameterized<?,A> assertion,
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
