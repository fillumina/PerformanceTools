package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.StaticPath;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParameterizedSequence<C, A extends Assertable>
        extends AbstractAssertion<PHolder<PHolder<A>>>
        implements PerformanceConsumer<PHolder<PHolder<A>>>,
            StringGenerator<PHolder<PHolder<A>>>,
            ParameterizedSequenceAssertion<C, A> {

    private final Map<String,
                ParameterizedAssertion<ParameterizedSequenceAssertion<C, A>, A>>
            sequenceMap = new LinkedHashMap<>();

    private final List<Assertion<PHolder<A>>> assertionList = new ArrayList<>();

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
    public ParameterizedSequenceAssertion<C, A> addAssertion(
            Assertion<PHolder<A>> assertion) {
        assertionList.add(assertion);
        return this;
    }

    @Override
    public ParameterizedAssertion<ParameterizedSequenceAssertion<C, A>, A>
            forAllSequences() {
        AssertParameterized<ParameterizedSequenceAssertion<C, A>, A>
                allParameterizedPerformanceAssertion =
                    new AssertParameterized<>(
                        (ParameterizedSequenceAssertion<C,A>)this);
        assertionList.add(allParameterizedPerformanceAssertion);
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
        sequenceMap.put(sequence, pa);
        return pa;
    }

    private interface AssertionVisitor<A extends Assertable> {

        void visit(Assertion<PHolder<A>> assertion,
                StaticPath name,
                PHolder<PHolder<A>> performance);
    }

    private void visitAssertions(
            PHolder<PHolder<PHolder<A>>> performances,
            AssertionVisitor<A> visitor) {
        if (performances == null) {
            return;
        }
        for (PHolder<PHolder<A>> parameterizedStats : performances) {
            StaticPath testName = parameterizedStats.getName();

            AssertParameterized<ParameterizedSequenceAssertion<C, A>, A> assertion =
                    (AssertParameterized<ParameterizedSequenceAssertion<C, A>, A>)
                    sequenceMap.get(testName.getLastName());

            if (assertion != null) {
                visitor.visit(assertion, testName, parameterizedStats);
            }

            for (Assertion<PHolder<A>> a : assertionList) {
                visitor.visit(a, testName, parameterizedStats);
            }
        }
    }

    @Override
    public void consume(PHolder<PHolder<PHolder<A>>> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    Assertion<PHolder<A>> assertion,
                    StaticPath name,
                    PHolder<PHolder<A>> performance) {
                assertion.consume(performance);
            }
        });
    }

    @Override
    public String toString(PHolder<PHolder<PHolder<A>>> performances) {
        final StaticPath branch = performances.getName();
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(
                    Assertion<PHolder<A>> assertion,
                    StaticPath sequenceName,
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
