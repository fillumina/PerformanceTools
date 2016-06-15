package com.fillumina.performance.suite.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.util.ComposedName;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.fillumina.performance.stats.assertion.PerformanceStatsAssertion;
import com.fillumina.performance.infrastructure.StringFormatter;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedPerformance<T>
        implements PerformanceConsumer<Map<ComposedName,PerformanceStats>>,
            StringFormatter<Map<ComposedName,PerformanceStats>> {

    private final T caller;
    private final Map<String, PerformanceStatsAssertion> map = new LinkedHashMap<>();
    private final Map<Pattern, PerformanceStatsAssertion> regexpMap =
            new LinkedHashMap<>();

    public static AssertParametrizedPerformance<?> create() {
        return new AssertParametrizedPerformance<>();
    }

    public AssertParametrizedPerformance() {
        this(null);
    }

    public AssertParametrizedPerformance(T caller) {
        this.caller = caller;
    }

    private PerformanceStatsAssertion allTestsAssertion;

    public AssertParametrizedPerformance<T> forTest(String testName,
            PerformanceStatsAssertion performanceAssertion) {
        map.put(testName, performanceAssertion);
        return this;
    }

    public AssertParametrizedPerformance<T> forRegexpTest(String regexp,
            PerformanceStatsAssertion performanceAssertion) {
        Pattern pattern = Pattern.compile(regexp);
        regexpMap.put(pattern, performanceAssertion);
        return this;
    }

    public AssertParametrizedPerformance<T> forAllTests(
            PerformanceStatsAssertion performanceAssertion) {
        allTestsAssertion = performanceAssertion;
        return this;
    }

    public T end() {
        return caller;
    }

    private interface PerformanceUser {
        void use(PerformanceStatsAssertion assertion,
                ComposedName name,
                PerformanceStats performances);
    }

    private void assertionVisitor(ComposedName name,
            Map<ComposedName, PerformanceStats> performances,
            PerformanceUser user) {
        for (Map.Entry<ComposedName, PerformanceStats> entry :
                performances.entrySet()) {
            ComposedName testName = entry.getKey();
            PerformanceStats stats = entry.getValue();

            PerformanceStatsAssertion assertion = map.get(testName.getLastName());
            if (assertion != null) {
                user.use(assertion, testName, stats);
            }
            if (allTestsAssertion != null) {
                user.use(allTestsAssertion, testName, stats);
            }
            for (Map.Entry<Pattern, PerformanceStatsAssertion> e :
                    regexpMap.entrySet()) {
                Pattern p = e.getKey();
                if (p.matcher(testName.getLastName()).matches()) {
                    user.use(e.getValue(), testName, stats);
                }
            }
        }
    }

    @Override
    public void consume(ComposedName name,
            Map<ComposedName, PerformanceStats> performances) {
        assertionVisitor(name, performances, new PerformanceUser() {
            @Override
            public void use(PerformanceStatsAssertion assertion, ComposedName name,
                    PerformanceStats performances) {
                assertion.consume(name, performances);
            }
        });
    }

    @Override
    public String toString(Map<ComposedName, PerformanceStats> performance) {
        return toString(null, performance);
    }

    @Override
    public String toString(ComposedName name,
            Map<ComposedName, PerformanceStats> performance) {
        final StringBuilder buf = new StringBuilder();
        assertionVisitor(name, performance, new PerformanceUser() {
            @Override
            public void use(PerformanceStatsAssertion assertion, ComposedName name,
                    PerformanceStats performances) {
                if (name != null) {
                    buf.append(name.toString()).append(System.lineSeparator());
                }
                buf.append(assertion.toString(null, performances));
            }
        });
        return buf.toString();
    }
}
