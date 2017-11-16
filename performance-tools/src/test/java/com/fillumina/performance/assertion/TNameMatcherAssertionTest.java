package com.fillumina.performance.assertion;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import com.fillumina.performance.util.tname.TNameMatcher;
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

        builder.order(TNameMatcher.builder().string("one").build())
                .lessThan(TNameMatcher.builder().string("two").build());

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        builder.build().accept(stats);
    }

    @Test(expected=OrderAssertionError.class)
    public void shouldSetAndConsumeAnInvalidAssertion() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(TNameMatcher.builder().string("one").build())
                .greaterThan(TNameMatcher.builder().string("two").build());

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        builder.build().accept(stats);
    }

    @Test
    public void shouldSetAndConsumeTwoAssertionsUsingFluidInterface() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order().string("one").end()
                .lessThan().string("two").end();

        builder.value().string("one").end().equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        builder.build().accept(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldSetAndConsumeTwoAssertionsOneOfWhichIsInvalid() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(TNameMatcher.builder().string("one").build())
                .lessThan(TNameMatcher.builder().string("two").build());

        builder.value(TNameMatcher.builder().string("one").build())
                .equalsTo(99.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        builder.build().accept(stats);
    }

    @Test
    public void shouldSetAndConsumeParameterizedAssertions() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.order(TNameMatcher.builder().jolly().equalsTo(10.0).build())
                .lessThan(TNameMatcher.builder().jolly().equalsTo(100.0).build());

        builder.value(TNameMatcher.builder().string("one").string("10").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0),
                        TN.tname("one", "100"), new OnlineMeasure(100.0),
                        TN.tname("two", "10"), new OnlineMeasure(20.0),
                        TN.tname("two", "100"), new OnlineMeasure(200.0)
                )
        );

        builder.build().accept(stats);
    }

    @Test
    public void shouldInterceptNoTestException() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value(TNameMatcher.builder().string("one").string("10").build())
                .equalsTo(10.0);

        builder.value(TNameMatcher.builder().string("NOT_EXIST").string("10").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0),
                        TN.tname("one", "100"), new OnlineMeasure(100.0),
                        TN.tname("two", "10"), new OnlineMeasure(20.0),
                        TN.tname("two", "100"), new OnlineMeasure(200.0)
                )
        );

        builder.build().accept(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldInterceptNoTestExceptionAndCheckValidity() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value(TNameMatcher.builder().string("NOT_EXIST").string("10").build())
                .equalsTo(10.0);

        builder.value(TNameMatcher.builder().string("one").string("10").build())
                .equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0),
                        TN.tname("one", "100"), new OnlineMeasure(100.0)
                )
        );

        builder.build().accept(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldUseFluidInterfaceAndCheckWrongValue() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value().string("one", "10").end().equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0))
        );

        builder.build().accept(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldUseShortNotationAndCheckWrongValue() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.value("one", "10").equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0))
        );

        builder.build().accept(stats);
    }

    @Test(expected=OrderAssertionError.class)
    public void shouldUseShortNotationWithPrefixAndCheckWrongOrder() {
        TNameMatcherAssertion.Builder<Void> builder =
                TNameMatcherAssertion.builder();

        builder.forTest("one").order("a").greaterThan("b");

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<CharSequence,Measure>create(
                        TN.tname("one", "a"), new OnlineMeasure(10.0),
                        TN.tname("one", "b"), new OnlineMeasure(20.0)) );

        builder.build().accept(stats);
    }
}
