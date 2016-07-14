package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedPerformance<C, A extends AssertableMultiStats>
        implements
            PerformanceConsumer<Map<ComposedName, A>>,
            Assertion<Map<ComposedName, A>>,
            StringGenerator<Map<ComposedName, A>> {

    private final C caller;
    private final Map<String, StatsAssertion<A>> map = new LinkedHashMap<>();
    private final Map<Pattern, StatsAssertion<A>> regexpMap = new LinkedHashMap<>();

    public AssertParametrizedPerformance() {
        this(null);
    }

    public AssertParametrizedPerformance(C caller) {
        this.caller = caller;
    }

    private StatsAssertion<A> allTestsAssertion;

    public AssertParametrizedPerformance<C,A> forTest(String testName,
            StatsAssertion<A> performanceAssertion) {
        map.put(testName, performanceAssertion);
        return this;
    }

    public AssertParametrizedPerformance<C,A> forRegexpTest(String regexp,
            StatsAssertion<A> performanceAssertion) {
        Pattern pattern = Pattern.compile(regexp);
        regexpMap.put(pattern, performanceAssertion);
        return this;
    }

    public AssertParametrizedPerformance<C,A> forAllTests(
            StatsAssertion<A> performanceAssertion) {
        allTestsAssertion = performanceAssertion;
        return this;
    }

    public C endTests() {
        return caller;
    }

    @Override
    public void check(Map<ComposedName, A> assertable) {
        consume(null, assertable);
    }

    @Override
    public Collection<Assertion<AssertableMultiStats>> getLeaves(
            final ComposedName branchName) {
        final List<Assertion<AssertableMultiStats>> list = new ArrayList<>();
        visitAssertions(null, new AssertionVisitor<A>() {
            @Override
            @SuppressWarnings("unchecked")
            public void visit(
                    Assertion<A> assertion,
                    ComposedName name,
                    A performance) {
                if (branchName.containsAll(name)) {
                    list.add((Assertion<AssertableMultiStats>)assertion);
                }
            }
        });
        return Collections.unmodifiableList(list);
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
            for (Map.Entry<Pattern, StatsAssertion<A>> e : regexpMap.entrySet()) {
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
    public String toString(ComposedName name,
            Map<ComposedName, A> performance) {
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performance, new AssertionVisitor<A>() {
            @Override
            public void visit(Assertion<A> assertion,
                    ComposedName name,
                    A performances) {
                if (name != null) {
                    buf.append(name.toString()).append(System.lineSeparator());
                }
                buf.append(assertion.toString(null, performances))
                    .append(System.lineSeparator());
            }
        });
        return buf.toString();
    }
}
