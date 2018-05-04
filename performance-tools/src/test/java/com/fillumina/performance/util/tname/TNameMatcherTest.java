package com.fillumina.performance.util.tname;

import static com.fillumina.performance.executor.TN.tname;
import com.fillumina.performance.util.tname.TNameMatcher.Result;
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
        TName ok = tname("alfa");
        TName nok = tname("beta");

        TNameMatcher pattern = TNameMatcher.builder().string("alfa").build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeALessThan() {
        TName ok = tname("10");
        TName nok = tname("30");

        TNameMatcher pattern = TNameMatcher.builder().lessThan(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAGreaterThan() {
        TName ok = tname("30");
        TName nok = tname("10");

        TNameMatcher pattern = TNameMatcher.builder().greaterThan(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAnEquals() {
        TName ok = tname("20");
        TName nok = tname("10");

        TNameMatcher pattern = TNameMatcher.builder().equalsTo(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeInterval() {
        TName ok = tname("20");
        TName nok = tname("10");

        TNameMatcher pattern = TNameMatcher.builder()
                .interval(15, 25)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeRegexp() {
        TName ok = tname("alfa");
        TName nok = tname("beta");

        TNameMatcher pattern = TNameMatcher.builder()
                .pattern("a.?.?a").build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeJolly() {
        TName ok1 = tname("alfa");
        TName ok2 = tname("beta");

        TNameMatcher pattern = TNameMatcher.builder().jolly().build();

        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
    }

    @Test
    public void shouldRecognizeAll() {
        TName ok1 = tname("alfa");
        TName ok2 = tname("beta");

        TNameMatcher pattern = TNameMatcher.builder().all().build();

        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
    }

    @Test
    public void shouldRecognizeFixedStringCondition() {
        TName ok = tname("root", "subroot", "alfa");
        TName nok = tname("root", "subroot", "beta");

        TNameMatcher pattern = TNameMatcher.builder()
                .string("root", "subroot", "alfa")
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeExternalCondition() {
        TName ok = tname("alfa");
        TName nok = tname("beta");

        TNameMatcher pattern = TNameMatcher.builder()
                .condition(value -> "alfa".equals(value) ?
                        Result.ACCEPT : Result.REJECT)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeMultiCondition() {
        TName ok = tname("alfa", "10");
        TName nok1 = tname("alfa", "20");
        TName nok2 = tname("beta", "10");
        TName nok3 = tname("alfa", "10", "other");

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
        TName ok = tname("alfa", "pippo", "10");
        TName nok1 = tname("alfa", "pippo", "20");
        TName nok2 = tname("beta", "pippo", "10");
        TName nok3 = tname("alfa", "pippo", "10", "other");

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
        TName ok = tname("alfa", "pippo", "10");
        TName nok1 = tname("alfa", "pippo", "20");
        TName nok2 = tname("beta", "pippo", "20");
        TName nok3 = tname("alfa", "pippo", "10", "other");

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
        TName ok = tname("alfa", "pippo", "10");
        TName nok1 = tname("gamma", "pippo", "20");
        TName nok2 = tname("beta", "pippo", "20");
        TName nok3 = tname("gamma", "pippo", "10")
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
        TName ok = tname("alfa", "one", "10");
        TName nok1 = tname("gamma", "two", "20");
        TName nok2 = tname("beta", "three", "20");
        TName nok3 = tname("gamma", "four", "10")
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
        TName ok = tname("alfa", "one");
        TName nok1 = tname("gamma", "two");
        TName nok2 = tname("beta", "three");
        TName nok3 = tname("gamma", "four");

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
        TName ok = tname("alfa", "one", "end");
        TName nok1 = tname("alfa", "one");
        TName nok2 = tname("beta", "end");
        TName nok3 = tname("one", "end");

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
        TName ok0 = tname("alfa", "one", "10");
        TName ok1 = tname();
        TName ok2 = tname("beta");
        TName ok3 = tname("");

        TNameMatcher pattern = TNameMatcher.builder()
                .all()
                .build();

        assertTrue(pattern.matches(ok0));
        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
        assertTrue(pattern.matches(ok3));
    }

    @Test
    public void shouldAcceptCharSequence() {
        TName tnameOk = tname("alfa");
        String stringOk = "alfa";

        TNameMatcher pattern = TNameMatcher.builder().string("alfa").build();

        assertTrue(pattern.matches(tnameOk));
        assertTrue(pattern.matches(stringOk));
    }

}
