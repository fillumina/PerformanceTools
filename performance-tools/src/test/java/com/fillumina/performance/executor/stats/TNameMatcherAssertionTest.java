package com.fillumina.performance.executor.stats;

import com.fillumina.performance.assertion.OrderAssertionError;
import com.fillumina.performance.assertion.ValueAssertionError;
import com.fillumina.performance.executor.PN;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.collection.IndexedHashMap;
import com.fillumina.performance.util.pathname.PathNameMatcher;
import com.fillumina.performance.util.unit.DimensionalMeasure;
import com.fillumina.performance.util.unit.OnlineDimensionalMeasure;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcherAssertionTest {

    @Test
    public void shouldSetAndConsumeAnAssertion() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(PathNameMatcher.builder().string("one").build())
                .lessThan(PathNameMatcher.builder().string("two").build());

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("two"), new OnlineDimensionalMeasure(20.0)));

        builder.build().check(stats);
    }

    @Test(expected=OrderAssertionError.class)
    public void shouldSetAndConsumeAnInvalidAssertion() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(PathNameMatcher.builder().string("one").build())
                // GREATER: 10 > 20!
                .greaterThan(PathNameMatcher.builder().string("two").build());

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("two"), new OnlineDimensionalMeasure(20.0)));

        builder.build().check(stats);
    }

    @Test
    public void shouldSetAndConsumeTwoAssertionsUsingFluentInterface() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order().string("one").end()
                .lessThan().string("two").end();

        builder.value().string("one").end().equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("two"), new OnlineDimensionalMeasure(20.0)));

        builder.build().check(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldSetAndConsumeTwoAssertionsOneOfWhichIsInvalid() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(PathNameMatcher.builder().string("one").build())
                .lessThan(PathNameMatcher.builder().string("two").build());

        builder.value(PathNameMatcher.builder().string("one").build())
                .equalsTo(99.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("two"), new OnlineDimensionalMeasure(20.0)));

        builder.build().check(stats);
    }

    @Test
    public void shouldSetAndConsumeParameterizedAssertions() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(PathNameMatcher.builder().jolly().equalsTo(10.0).build())
                .lessThan(PathNameMatcher.builder().jolly().equalsTo(100.0).build());

        builder.value(PathNameMatcher.builder().string("one").string("10").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one", "10"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("one", "100"), new OnlineDimensionalMeasure(100.0),
                        PN.pname("two", "10"), new OnlineDimensionalMeasure(20.0),
                        PN.pname("two", "100"), new OnlineDimensionalMeasure(200.0)
                )
        );

        builder.build().check(stats);
    }

    @Test
    public void shouldInterceptNoTestException() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value(PathNameMatcher.builder().string("one").string("10").build())
                .equalsTo(10.0);

        builder.value(PathNameMatcher.builder().string("NOT_EXIST").string("10").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one", "10"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("one", "100"), new OnlineDimensionalMeasure(100.0),
                        PN.pname("two", "10"), new OnlineDimensionalMeasure(20.0),
                        PN.pname("two", "100"), new OnlineDimensionalMeasure(200.0)
                )
        );

        builder.build().check(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldInterceptNoTestExceptionAndCheckValidity() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value(PathNameMatcher.builder().string("NOT_EXIST").string("10").build())
                .equalsTo(10.0);

        builder.value(PathNameMatcher.builder().string("one").string("10").build())
                .equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one", "10"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("one", "100"), new OnlineDimensionalMeasure(100.0)
                )
        );

        builder.build().check(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldUseFluidInterfaceAndCheckWrongValue() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value().string("one", "10").end().equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one", "10"), new OnlineDimensionalMeasure(10.0))
        );

        builder.build().check(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldUseShortNotationAndCheckWrongValue() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value("one", "10").equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one", "10"), new OnlineDimensionalMeasure(10.0))
        );

        builder.build().check(stats);
    }

    @Test(expected=OrderAssertionError.class)
    public void shouldUseShortNotationWithPrefixAndCheckWrongOrder() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.forTest("one").order("a").greaterThan("b");

        AssertableMock stats = new AssertableMock("test",
                IndexedHashMap.<CharSequence,DimensionalMeasure>create(
                        PN.pname("one", "a"), new OnlineDimensionalMeasure(10.0),
                        PN.pname("one", "b"), new OnlineDimensionalMeasure(20.0)) );

        builder.build().check(stats);
    }
}
