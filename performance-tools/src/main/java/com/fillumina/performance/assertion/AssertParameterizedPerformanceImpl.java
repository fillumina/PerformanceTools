package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
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
public class AssertParameterizedPerformanceImpl<C, A extends AssertableMultiStats>
        implements
            PerformanceConsumer<Map<ComposedName, A>>,
            Assertion<Map<ComposedName, A>>,
            StringGenerator<Map<ComposedName, A>>,
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
        allTestsAssertion = createAssertPerformance();;
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
    public void check(Map<ComposedName, A> assertable) {
        consume(null, assertable);
    }

    private interface AssertionVisitor<A extends AssertableMultiStats> {
        void visit(Assertion<A> assertion,
                ComposedName name,
                A performances);
    }

    private void visitAssertions(Map<ComposedName, A> performances,
            AssertionVisitor<A> visitor) {
        for (Map.Entry<ComposedName, A> entry : performances.entrySet()) {
            ComposedName testName = entry.getKey();
            A stats = entry.getValue();

            Assertion<A> assertion = map.get(testName.getLastName());
            if (assertion != null) {
                visitor.visit(assertion, testName, stats);
            }
            if (allTestsAssertion != null) {
                visitor.visit(allTestsAssertion, testName, stats);
            }
            for (Map.Entry<Pattern,
                    StatsAssertion<AssertParameterizedPerformance<C,A>,A>> e :
                    regexpMap.entrySet()) {
                Pattern p = e.getKey();
                if (p.matcher(testName.getLastName()).matches()) {
                    visitor.visit(e.getValue(), testName, stats);
                }
            }
        }
    }

    @Override
    public void consume(ComposedName name, Map<ComposedName, A> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(Assertion<A> assertion,
                    ComposedName name,
                    A performances) {
                assertion.consume(name, performances);
            }
        });
    }

    @Override
    public String toString(Map<ComposedName, A> performance) {
        return toString(null, performance);
    }

    @Override
    public String toString(final ComposedName branch,
            Map<ComposedName, A> performance) {
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performance, new AssertionVisitor<A>() {
            @Override
            public void visit(Assertion<A> assertion,
                    ComposedName name,
                    A performances) {
                if (branch == null || name.equals(branch)) {
                    buf.append(assertion.toString(null, performances));
                }
            }
        });
        return buf.toString();
    }
}
