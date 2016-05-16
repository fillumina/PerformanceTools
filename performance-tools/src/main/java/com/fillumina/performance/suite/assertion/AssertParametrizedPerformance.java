package com.fillumina.performance.suite.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.stats.PerformanceStats;
import com.fillumina.performance.stats.assertion.PerformanceAssertion;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedPerformance<T>
        implements PerformanceConsumer<Map<String,PerformanceStats>> {

    private final T caller;
    private final Map<String, PerformanceAssertion> map = new LinkedHashMap<>();
    private final Map<Pattern, PerformanceAssertion> regexpMap =
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

    private PerformanceAssertion allTestsAssertion;

    public AssertParametrizedPerformance<T> forTest(String testName,
            PerformanceAssertion performanceAssertion) {
        map.put(testName, performanceAssertion);
        return this;
    }

    public AssertParametrizedPerformance<T> forRegexpTest(String regexp,
            PerformanceAssertion performanceAssertion) {
        Pattern pattern = Pattern.compile(regexp);
        regexpMap.put(pattern, performanceAssertion);
        return this;
    }

    public AssertParametrizedPerformance<T> forAllTests(
            PerformanceAssertion performanceAssertion) {
        allTestsAssertion = performanceAssertion;
        return this;
    }

    public T end() {
        return caller;
    }

    @Override
    public void consume(String message,
            Map<String, PerformanceStats> performances) {
        for (Map.Entry<String, PerformanceStats> entry :
                performances.entrySet()) {
            String testName = entry.getKey();
            PerformanceStats stats = entry.getValue();

            PerformanceAssertion assertion = map.get(testName);
            if (assertion != null) {
                assertion.consume(testName, stats);
            }
            if (allTestsAssertion != null) {
                allTestsAssertion.consume(message, stats);
            }
            for (Map.Entry<Pattern, PerformanceAssertion> e :
                    regexpMap.entrySet()) {
                Pattern p = e.getKey();
                if (p.matcher(testName).matches()) {
                    e.getValue().consume(message, stats);
                }
            }
        }
    }
}
