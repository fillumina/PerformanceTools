package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.PerformanceHolder;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParameterizedPerformanceImpl<C, A extends Assertable>
        implements
            PerformanceConsumer<A>,
            Assertion<A>,
            StringGenerator<A>,
            AssertParameterizedPerformance<C, A> {

    private final Map<String, StatsAssertion<AssertParameterizedPerformance<C,A>,A>>
            map = new LinkedHashMap<>();
    private final Map<Pattern, StatsAssertion<AssertParameterizedPerformance<C,A>,A>>
            regexpMap = new LinkedHashMap<>();

    private StatsAssertion<AssertParameterizedPerformance<C, A>,A> allTestsAssertion;
    private C caller;

    public AssertParameterizedPerformanceImpl() {
        this(null);
    }

    public AssertParameterizedPerformanceImpl(C caller) {
        this.caller = caller;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C endTests() {
        if (caller == null) {
            return (C) this;
        }
        return caller;
    }

    @Override
    public StatsAssertion<AssertParameterizedPerformance<C,A>,A>
            forTest(String testName) {
        StatsAssertion<AssertParameterizedPerformance<C, A>,A> pa =
                createAssertPerformance();
        map.put(testName, pa);
        return pa;
    }

    @Override
    public StatsAssertion<AssertParameterizedPerformance<C,A>,A>
            forRegexpTest(String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        StatsAssertion<AssertParameterizedPerformance<C, A>,A> pa =
                createAssertPerformance();
        regexpMap.put(pattern, pa);
        return pa;
    }

    @Override
    public StatsAssertion<AssertParameterizedPerformance<C,A>,A> forAllTests() {
        allTestsAssertion = createAssertPerformance();
        return allTestsAssertion;
    }

    private StatsAssertion<AssertParameterizedPerformance<C, A>, A>
        createAssertPerformance() {
        StatsAssertion<AssertParameterizedPerformance<C, A>,A> pa =
                new AssertPerformance<>(
                        (AssertParameterizedPerformance<C,A>)this,
                        new ArrayList<Assertion<A>>());
        return pa;
    }

    @Override
    public void check(final PerformanceHolder<A> assertable) {
        consume(assertable);
    }

    private interface AssertionVisitor<A extends Assertable> {
        void visit(ComposedName name,
                Assertion<A> assertion,
                PerformanceHolder<A> performances);
    }

    // Map<String, A>
    private void visitAssertions(PerformanceHolder<A> performances,
            AssertionVisitor<A> visitor) {
        for (PerformanceHolder<A> subperf : performances) {
            ComposedName testName = subperf.getName();

            Assertion<A> assertion = map.get(testName.getLastName());
            if (assertion != null) {
                visitor.visit(testName, assertion, subperf);
            }
            if (allTestsAssertion != null) {
                visitor.visit(testName, allTestsAssertion, subperf);
            }
            for (Map.Entry<Pattern,
                    StatsAssertion<AssertParameterizedPerformance<C,A>,A>> e :
                    regexpMap.entrySet()) {
                Pattern p = e.getKey();
                if (p.matcher(testName.getLastName()).matches()) {
                    visitor.visit(testName, e.getValue(), subperf);
                }
            }
        }
    }

    @Override
    public void consume(PerformanceHolder<A> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(ComposedName name,
                    Assertion<A> assertion,
                    PerformanceHolder<A> performances) {
                assertion.consume(performances);
            }
        });
    }

    @Override
    public String toString(PerformanceHolder<A> performance) {
        final ComposedName branch = performance.getName();
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performance, new AssertionVisitor<A>() {
            @Override
            public void visit(ComposedName name, Assertion<A> assertion,
                    PerformanceHolder<A> performances) {
                if (branch == null || name.equals(branch)) {
                    buf.append(assertion.toString(performances));
                }
            }
        });
        return buf.toString();
    }
}
