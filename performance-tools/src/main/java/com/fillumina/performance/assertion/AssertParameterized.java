package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.CName;
import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.StaticPath;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @param C caller (to allow going back in fluent idioms)
 * @param A assertable
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParameterized<C, A extends Assertable>
        extends AbstractAssertion<PHolder<A>>
        implements
            PerformanceConsumer<PHolder<A>>,
            StringGenerator<PHolder<A>>,
            ParameterizedAssertion<C, A> {

    private final Map<String, StatsAssertion<ParameterizedAssertion<C,A>,A>>
            map = new LinkedHashMap<>();
    private final Map<Pattern, StatsAssertion<ParameterizedAssertion<C,A>,A>>
            regexpMap = new LinkedHashMap<>();

    private StatsAssertion<ParameterizedAssertion<C, A>,A> allTestsAssertion;
    private C caller;

    public AssertParameterized() {
        this(null);
    }

    public AssertParameterized(C caller) {
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
    public StatsAssertion<ParameterizedAssertion<C,A>,A>
            forTest(String testName) {
        StatsAssertion<ParameterizedAssertion<C, A>,A> pa =
                createAssertPerformance();
        map.put(testName, pa);
        return pa;
    }

    @Override
    public StatsAssertion<ParameterizedAssertion<C,A>,A>
            forRegexpTest(String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        StatsAssertion<ParameterizedAssertion<C, A>,A> pa =
                createAssertPerformance();
        regexpMap.put(pattern, pa);
        return pa;
    }

    @Override
    public StatsAssertion<ParameterizedAssertion<C,A>,A> forAllTests() {
        allTestsAssertion = createAssertPerformance();
        return allTestsAssertion;
    }

    private StatsAssertion<ParameterizedAssertion<C, A>, A>
        createAssertPerformance() {
        StatsAssertion<ParameterizedAssertion<C, A>,A> pa =
                new AssertStats<>(
                        (ParameterizedAssertion<C,A>)this,
                        new ArrayList<Assertion<A>>());
        return pa;
    }

    private interface AssertionVisitor<A extends Assertable> {
        void visit(StaticPath name,
                Assertion<A> assertion,
                PHolder<A> performances);
    }

    // Map<String, A>
    private void visitAssertions(PHolder<PHolder<A>> performances,
            AssertionVisitor<A> visitor) {
        for (PHolder<A> subperf : performances) {
            StaticPath testName = StaticPath.chooseIfNull(
                    subperf.getName(), CName.EMPTY);

           Assertion<A> assertion = map.get(testName.getLastName());
            if (assertion != null) {
                visitor.visit(testName, assertion, subperf);
            }
            if (allTestsAssertion != null) {
                visitor.visit(testName, allTestsAssertion, subperf);
            }
            for (Map.Entry<Pattern,
                    StatsAssertion<ParameterizedAssertion<C,A>,A>> e :
                    regexpMap.entrySet()) {
                Pattern p = e.getKey();
                if (p.matcher(testName.getLastName()).matches()) {
                    visitor.visit(testName, e.getValue(), subperf);
                }
            }
        }
    }

    @Override
    public void consume(PHolder<PHolder<A>> performances) {
        visitAssertions(performances, new AssertionVisitor<A>() {
            @Override
            public void visit(StaticPath name,
                    Assertion<A> assertion,
                    PHolder<A> performances) {
                assertion.consume(performances);
            }
        });
    }

    @Override
    public String toString(PHolder<PHolder<A>> performance) {
        final StaticPath branch = performance.getName();
        final StringBuilder buf = new StringBuilder();
        visitAssertions(performance, new AssertionVisitor<A>() {
            @Override
            public void visit(StaticPath name, Assertion<A> assertion,
                    PHolder<A> performances) {
                if (branch == null || name.equals(branch)) {
                    buf.append(assertion.toString(performances));
                }
            }
        });
        return buf.toString();
    }
}
