package com.fillumina.performance.util.pathname;

import static com.fillumina.performance.executor.PN.pname;
import com.fillumina.performance.util.pathname.PathNameMatcher.Result;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class PathNameMatcherTest {

    @Test
    public void shouldRecognizeAFixedName() {
        PathName ok = pname("alfa");
        PathName nok = pname("beta");

        PathNameMatcher pattern = PathNameMatcher.builder().string("alfa").build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeALessThan() {
        PathName ok = pname("10");
        PathName nok = pname("30");

        PathNameMatcher pattern = PathNameMatcher.builder().lessThan(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAGreaterThan() {
        PathName ok = pname("30");
        PathName nok = pname("10");

        PathNameMatcher pattern = PathNameMatcher.builder().greaterThan(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeAnEquals() {
        PathName ok = pname("20");
        PathName nok = pname("10");

        PathNameMatcher pattern = PathNameMatcher.builder().equalsTo(20).build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeInterval() {
        PathName ok = pname("20");
        PathName nok = pname("10");

        PathNameMatcher pattern = PathNameMatcher.builder()
                .interval(15, 25)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeRegexp() {
        PathName ok = pname("alfa");
        PathName nok = pname("beta");

        PathNameMatcher pattern = PathNameMatcher.builder()
                .pattern("a.?.?a").build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeJolly() {
        PathName ok1 = pname("alfa");
        PathName ok2 = pname("beta");

        PathNameMatcher pattern = PathNameMatcher.builder().jolly().build();

        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
    }

    @Test
    public void shouldRecognizeAll() {
        PathName ok1 = pname("alfa");
        PathName ok2 = pname("beta");

        PathNameMatcher pattern = PathNameMatcher.builder().all().build();

        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
    }

    @Test
    public void shouldRecognizeFixedStringCondition() {
        PathName ok = pname("root", "subroot", "alfa");
        PathName nok = pname("root", "subroot", "beta");

        PathNameMatcher pattern = PathNameMatcher.builder()
                .string("root", "subroot", "alfa")
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeExternalCondition() {
        PathName ok = pname("alfa");
        PathName nok = pname("beta");

        PathNameMatcher pattern = PathNameMatcher.builder()
                .condition(value -> "alfa".equals(value) ?
                        Result.ACCEPT : Result.REJECT)
                .build();

        assertTrue(pattern.matches(ok));
        assertFalse(pattern.matches(nok));
    }

    @Test
    public void shouldRecognizeMultiCondition() {
        PathName ok = pname("alfa", "10");
        PathName nok1 = pname("alfa", "20");
        PathName nok2 = pname("beta", "10");
        PathName nok3 = pname("alfa", "10", "other");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok = pname("alfa", "pippo", "10");
        PathName nok1 = pname("alfa", "pippo", "20");
        PathName nok2 = pname("beta", "pippo", "10");
        PathName nok3 = pname("alfa", "pippo", "10", "other");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok = pname("alfa", "pippo", "10");
        PathName nok1 = pname("alfa", "pippo", "20");
        PathName nok2 = pname("beta", "pippo", "20");
        PathName nok3 = pname("alfa", "pippo", "10", "other");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok = pname("alfa", "pippo", "10");
        PathName nok1 = pname("gamma", "pippo", "20");
        PathName nok2 = pname("beta", "pippo", "20");
        PathName nok3 = pname("gamma", "pippo", "10")
                .append("other");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok = pname("alfa", "one", "10");
        PathName nok1 = pname("gamma", "two", "20");
        PathName nok2 = pname("beta", "three", "20");
        PathName nok3 = pname("gamma", "four", "10")
                .append("other");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok = pname("alfa", "one");
        PathName nok1 = pname("gamma", "two");
        PathName nok2 = pname("beta", "three");
        PathName nok3 = pname("gamma", "four");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok = pname("alfa", "one", "end");
        PathName nok1 = pname("alfa", "one");
        PathName nok2 = pname("beta", "end");
        PathName nok3 = pname("one", "end");

        PathNameMatcher pattern = PathNameMatcher.builder()
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
        PathName ok0 = pname("alfa", "one", "10");
        PathName ok1 = pname();
        PathName ok2 = pname("beta");
        PathName ok3 = pname("");

        PathNameMatcher pattern = PathNameMatcher.builder()
                .all()
                .build();

        assertTrue(pattern.matches(ok0));
        assertTrue(pattern.matches(ok1));
        assertTrue(pattern.matches(ok2));
        assertTrue(pattern.matches(ok3));
    }

    @Test
    public void shouldAcceptCharSequence() {
        PathName tnameOk = pname("alfa");
        String stringOk = "alfa";

        PathNameMatcher pattern = PathNameMatcher.builder().string("alfa").build();

        assertTrue(pattern.matches(tnameOk));
        assertTrue(pattern.matches(stringOk));
    }

}
