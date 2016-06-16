package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PerformanceConsumer;
import com.fillumina.performance.infrastructure.StringGenerator;
import com.fillumina.performance.util.ComposedName;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParametrizedSequencePerformance<C, A extends AssertableMultiTest>
        implements PerformanceConsumer<Map<ComposedName, Map<ComposedName, A>>>,
            StringGenerator<Map<ComposedName, Map<ComposedName, A>>> {

    private final C caller;
    private final Map<String,
            AssertParametrizedPerformance<AssertParametrizedSequencePerformance<C,A>,A>>
            map = new LinkedHashMap<>();
    private AssertParametrizedPerformance<AssertParametrizedSequencePerformance<C,A>,A>
            allParametrizedPerformanceAssertion;

    public static <A extends AssertableMultiTest>
            AssertParametrizedSequencePerformance<?,A> create() {
        return new AssertParametrizedSequencePerformance<>();
    }

    public AssertParametrizedSequencePerformance() {
        this(null);
    }

    public AssertParametrizedSequencePerformance(C caller) {
        this.caller = caller;
    }

    public AssertParametrizedPerformance<AssertParametrizedSequencePerformance<C,A>,A>
            forAllSequences() {
        allParametrizedPerformanceAssertion =
                new AssertParametrizedPerformance<>(this);
        return allParametrizedPerformanceAssertion;
    }

    public AssertParametrizedPerformance<AssertParametrizedSequencePerformance<C,A>,A>
            forSequence(String sequence) {
        AssertParametrizedPerformance<AssertParametrizedSequencePerformance<C,A>,A>
                parametrizedPerformanceAssertion =
                    new AssertParametrizedPerformance<>(this);
        map.put(sequence, parametrizedPerformanceAssertion);
        return parametrizedPerformanceAssertion;
    }

    public C end() {
        return caller;
    }

    private interface AssertionVisitor<A extends AssertableMultiTest> {
        void use(AssertParametrizedPerformance<?,A> assertion,
                ComposedName name,
                Map<ComposedName, A> performance);
    }

    private void visitAssertions(ComposedName name,
            Map<ComposedName, Map<ComposedName, A>> performances,
            AssertionVisitor<A> user) {
        for (Map.Entry<ComposedName, Map<ComposedName, A>> entry :
                performances.entrySet()) {
            ComposedName testName = entry.getKey();
            Map<ComposedName, A> parametrizedStats = entry.getValue();

            AssertParametrizedPerformance<?,A> assertion =
                    map.get(testName.getLastName());
            if (assertion != null) {
                user.use(assertion, testName, parametrizedStats);
            }
            if (allParametrizedPerformanceAssertion != null) {
                user.use(allParametrizedPerformanceAssertion,
                        name,
                        parametrizedStats);
            }
        }
    }

    @Override
    public void consume(ComposedName name,
            Map<ComposedName, Map<ComposedName, A>> performances) {
        visitAssertions(name, performances, new AssertionVisitor<A>() {
            @Override
            public void use(
                    AssertParametrizedPerformance<?,A> assertion,
                    ComposedName name,
                    Map<ComposedName, A> performance) {
                assertion.consume(name, performance);
            }
        });
    }

    @Override
    public String toString(Map<ComposedName, Map<ComposedName, A>> performance) {
        return toString(null, performance);
    }

    @Override
    public String toString(ComposedName name,
            Map<ComposedName, Map<ComposedName, A>> performances) {
        final StringBuilder buf = new StringBuilder();
        visitAssertions(name, performances, new AssertionVisitor<A>() {
            @Override
            public void use(
                    AssertParametrizedPerformance<?,A> assertion,
                    ComposedName name,
                    Map<ComposedName, A> performance) {
                buf.append(assertion.toString(name, performance))
                        .append(System.lineSeparator());
            }
        });
        return buf.toString();
    }
}
