package com.fillumina.performance.assertion;

import com.fillumina.performance.util.collection.LinkedMap;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.OnlineMeasure;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertableTest {

    @Test
    public void shouldReturnTheGivenNames() {
        Map<String,Measure> map = LinkedMap.create(
                "first", new OnlineMeasure(1),
                "second", new OnlineMeasure(2),
                "third", new OnlineMeasure(3));

        Assertable assertable = new AssertableMapWrapper(map);

        Collection<? extends CharSequence> names = assertable.getNames();
        assertTrue(names.contains("first"));
        assertTrue(names.contains("second"));
        assertTrue(names.contains("third"));
    }

    @Test
    public void shouldReturnTheRightMeasure() {
        Map<String,Measure> map = LinkedMap.create(
                "first", new OnlineMeasure(1),
                "second", new OnlineMeasure(2),
                "third", new OnlineMeasure(3));

        Assertable assertable = new AssertableMapWrapper(map);

        assertEquals(new OnlineMeasure(2), assertable.getMeasure("second"));
    }

    @Test
    public void shouldReturnNullIfMeasureNotPresent() {
        Map<String,Measure> map = LinkedMap.create(
                "first", new OnlineMeasure(1),
                "second", new OnlineMeasure(2),
                "third", new OnlineMeasure(3));

        Assertable assertable = new AssertableMapWrapper(map);

        assertNull(assertable.getMeasure("not present"));
    }

    @Test
    public void shouldBeEmptyIfNoMeasureIsPresent() {
        Map<String,Measure> map = LinkedMap.empty();

        Assertable assertable = new AssertableMapWrapper(map);

        assertTrue(assertable.isEmpty());
    }

    @Test
    public void shouldReturnTheFirstMeasureInserted() {
        Map<String,Measure> map = LinkedMap.create(
                "first", new OnlineMeasure(1),
                "second", new OnlineMeasure(2),
                "third", new OnlineMeasure(3));

        Assertable assertable = new AssertableMapWrapper(map);

        assertEquals(new OnlineMeasure(1), assertable.getFirstMeasure());
    }
}
