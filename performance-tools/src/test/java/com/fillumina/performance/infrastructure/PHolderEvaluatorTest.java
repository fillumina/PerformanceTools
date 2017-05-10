package com.fillumina.performance.infrastructure;

import com.fillumina.performance.assertion.OrderAssertionError;
import com.fillumina.performance.assertion.ValueAssertionError;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.TNameMatcher;
import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PHolderEvaluatorTest {

    @Test
    public void shouldSetAndConsumeAnAssertion() {
        List<TName> names = Arrays.asList(TN.tname("one"), TN.tname("two"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.order(TNameMatcher.builder().string("one").build())
                .lessThan(TNameMatcher.builder().string("two").build());

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        ev.consume(stats);
    }

    @Test(expected=OrderAssertionError.class)
    public void shouldSetAndConsumeAnInvalidAssertion() {
        List<TName> names = Arrays.asList(TN.tname("one"), TN.tname("two"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.order(TNameMatcher.builder().string("one").build())
                .greaterThan(TNameMatcher.builder().string("two").build());

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        ev.consume(stats);
    }

    @Test
    public void shouldSetAndConsumeTwoAssertions() {
        List<TName> names = Arrays.asList(TN.tname("one"), TN.tname("two"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.order(TNameMatcher.builder().string("one").build())
                .lessThan(TNameMatcher.builder().string("two").build());

        ev.value(TNameMatcher.builder().string("one").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        ev.consume(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldSetAndConsumeTwoAssertionsOneOfWhichIsInvalid() {
        List<TName> names = Arrays.asList(TN.tname("one"), TN.tname("two"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.order(TNameMatcher.builder().string("one").build())
                .lessThan(TNameMatcher.builder().string("two").build());

        ev.value(TNameMatcher.builder().string("one").build())
                .equalsTo(99.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one"), new OnlineMeasure(10.0),
                        TN.tname("two"), new OnlineMeasure(20.0)));

        ev.consume(stats);
    }

    @Test
    public void shouldSetAndConsumeParameterizedAssertions() {
        List<TName> names = Arrays.asList(
                TN.tname("one", "10"),
                TN.tname("one", "100"),
                TN.tname("two", "10"),
                TN.tname("two", "100"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.order(TNameMatcher.builder().jolly().equalsTo(10.0).build())
                .lessThan(TNameMatcher.builder().jolly().equalsTo(100.0).build());

        ev.value(TNameMatcher.builder().string("one").string("10").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0),
                        TN.tname("one", "100"), new OnlineMeasure(100.0),
                        TN.tname("two", "10"), new OnlineMeasure(20.0),
                        TN.tname("two", "100"), new OnlineMeasure(200.0)
                )
        );

        ev.consume(stats);
    }

    @Test
    public void shouldInterceptNoTestException() {
        List<TName> names = Arrays.asList(
                TN.tname("one", "10"),
                TN.tname("one", "100"),
                TN.tname("NOT_EXIST", "10"),
                TN.tname("NOT_EXIST", "100"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.value(TNameMatcher.builder().string("one").string("10").build())
                .equalsTo(10.0);

        ev.value(TNameMatcher.builder().string("NOT_EXIST").string("10").build())
                .equalsTo(10.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0),
                        TN.tname("one", "100"), new OnlineMeasure(100.0),
                        TN.tname("two", "10"), new OnlineMeasure(20.0),
                        TN.tname("two", "100"), new OnlineMeasure(200.0)
                )
        );

        ev.consume(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldInterceptNoTestExceptionAndCheckValidity() {
        List<TName> names = Arrays.asList(
                TN.tname("one", "10"),
                TN.tname("one", "100"),
                TN.tname("NOT_EXIST", "10"),
                TN.tname("NOT_EXIST", "100"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.value(TNameMatcher.builder().string("NOT_EXIST").string("10").build())
                .equalsTo(10.0);

        ev.value(TNameMatcher.builder().string("one").string("10").build())
                .equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0),
                        TN.tname("one", "100"), new OnlineMeasure(100.0)
                )
        );

        ev.consume(stats);
    }

    @Test(expected=ValueAssertionError.class)
    public void shouldUseFluidInterface() {
        List<TName> names = Arrays.asList( TN.tname("one", "10"));

        PHolderEvaluator<Void,AssertableMock> ev = new PHolderEvaluator<>(names);

        ev.value().string("one", "10").end()
                .equalsTo(9999.0);

        AssertableMock stats = new AssertableMock("test",
                LinkedMap.<TName,Measure>create(
                        TN.tname("one", "10"), new OnlineMeasure(10.0))
        );

        ev.consume(stats);
    }
}
