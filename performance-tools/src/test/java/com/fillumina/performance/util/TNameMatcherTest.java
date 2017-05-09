package com.fillumina.performance.util;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.util.TNameMatcher.Result;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TNameMatcherTest {

    @Test
    public void shouldRecognizeAFixedName() {
        TName ok = TN.name("alfa");
        TName nok = TN.name("beta");

        TNameMatcher pattern = TNameMatcher.builder().string("alfa").build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeALessThan() {
        TName ok = TN.name("10");
        TName nok = TN.name("30");

        TNameMatcher pattern = TNameMatcher.builder().lessThan(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAGreaterThan() {
        TName ok = TN.name("30");
        TName nok = TN.name("10");

        TNameMatcher pattern = TNameMatcher.builder().greaterThan(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAnEquals() {
        TName ok = TN.name("20");
        TName nok = TN.name("10");

        TNameMatcher pattern = TNameMatcher.builder().equalsTo(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeInterval() {
        TName ok = TN.name("20");
        TName nok = TN.name("10");

        TNameMatcher pattern = TNameMatcher.builder()
                .interval(15, 25)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeRegexp() {
        TName ok = TN.name("alfa");
        TName nok = TN.name("beta");

        TNameMatcher pattern = TNameMatcher.builder()
                .pattern("a.?.?a").build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeJolly() {
        TName ok = TN.name("alfa");
        TName nok = TN.name("beta");

        TNameMatcher pattern = TNameMatcher.builder().jolly().build();

        assertTrue(pattern.matches(ok));
        assertTrue(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAll() {
        TName ok = TN.name("alfa");
        TName nok = TN.name("beta");

        TNameMatcher pattern = TNameMatcher.builder().all().build();

        assertTrue(pattern.matches(ok));
        assertTrue(pattern.matches(nok));
    }


    @Test
    public void shouldRecognizeExternalCondition() {
        TName ok = TN.name("alfa");
        TName nok = TN.name("beta");

        TNameMatcher pattern = TNameMatcher.builder()
                .condition(value -> "alfa".equals(value) ?
                        Result.OK : Result.REJECT)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeMultiCondition() {
        TName ok = TN.name("alfa", "10");
        TName nok1 = TN.name("alfa", "20");
        TName nok2 = TN.name("beta", "10");
        TName nok3 = TN.name("alfa", "10", "other");

        TNameMatcher pattern = TNameMatcher.builder()
                .pattern("a.?.?a")
                .equalsTo(10)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithJolly() {
        TName ok = TN.name("alfa", "pippo", "10");
        TName nok1 = TN.name("alfa", "pippo", "20");
        TName nok2 = TN.name("beta", "pippo", "10");
        TName nok3 = TN.name("alfa", "pippo", "10", "other");

        TNameMatcher pattern = TNameMatcher.builder()
                .pattern("a.?.?a")
                .jolly()
                .equalsTo(10)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithAllAtBeginning() {
        TName ok = TN.name("alfa", "pippo", "10");
        TName nok1 = TN.name("alfa", "pippo", "20");
        TName nok2 = TN.name("beta", "pippo", "20");
        TName nok3 = TN.name("alfa", "pippo", "10", "other");

        TNameMatcher pattern = TNameMatcher.builder()
                .all()
                .equalsTo(10)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithAllAtEnd() {
        TName ok = TN.name("alfa", "pippo", "10");
        TName nok1 = TN.name("gamma", "pippo", "20");
        TName nok2 = TN.name("beta", "pippo", "20");
        TName nok3 = TN.name("gamma", "pippo", "10")
                .append("other");

        TNameMatcher pattern = TNameMatcher.builder()
                .pattern("a.?.?a")
                .all()
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithAllAtBeginningAndEnd() {
        TName ok = TN.name("alfa", "one", "10");
        TName nok1 = TN.name("gamma", "two", "20");
        TName nok2 = TN.name("beta", "three", "20");
        TName nok3 = TN.name("gamma", "four", "10")
                .append("other");

        TNameMatcher pattern = TNameMatcher.builder()
                .all()
                .string("one")
                .all()
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithAllPastEnd() {
        TName ok = TN.name("alfa", "one");
        TName nok1 = TN.name("gamma", "two");
        TName nok2 = TN.name("beta", "three");
        TName nok3 = TN.name("gamma", "four");

        TNameMatcher pattern = TNameMatcher.builder()
                .jolly()
                .string("one")
                .all()
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithAllPastEndAndAnotherNode() {
        TName ok = TN.name("alfa", "one", "end");
        TName nok1 = TN.name("alfa", "one");
        TName nok2 = TN.name("beta", "end");
        TName nok3 = TN.name("one", "end");

        TNameMatcher pattern = TNameMatcher.builder()
                .jolly()
                .string("one")
                .all()
                .string("end")
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok1));
        assertFalse(pattern.matches(nok2));
        assertFalse(pattern.matches(nok3));
    }

    @Test
    public void shouldRecognizeMultiConditionWithOnlyAll() {
        TName ok0 = TN.name("alfa", "one", "10");
        TName ok1 = TN.EMPTY;
        TName ok2 = TN.name("beta");
        TName ok3 = TN.name("");

        TNameMatcher pattern = TNameMatcher.builder()
                .all()
                .build();

        assertTrue(pattern.matches(ok0));
        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
        assertTrue(pattern.matches(ok3));
    }
}
