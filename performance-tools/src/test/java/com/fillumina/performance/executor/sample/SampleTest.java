package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.util.pathname.PathName;
import com.fillumina.performance.util.pathname.PathNamedMap;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Collection;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleTest {

    @Test
    public void shouldGetSampleValue() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(PN.pname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(PN.pname("three"), 20, IntervalUnit.MILLISECONDS);

        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        assertEquals(oneValue, sample.getSampleValue("one"));
        assertEquals(twoValue, sample.getSampleValue("two"));
        assertEquals(threeValue, sample.getSampleValue("three"));
    }

    @Test
    public void shouldGetValuesMap() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(PN.pname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(PN.pname("three"), 20, IntervalUnit.MILLISECONDS);

        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        Map<PathName,SampleValue> m = sample.getValuesMap();
        assertEquals(oneValue, m.get("one"));
        assertEquals(twoValue, m.get("two"));
        assertEquals(threeValue, m.get("three"));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldGetValuesMapBeUnmodifiable() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12, IntervalUnit.MILLISECONDS);

        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>().add(oneValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        Map<PathName,SampleValue> m = sample.getValuesMap();

        PathName two = PN.pname("two");

        SampleValue twoValue = new SampleValue(two, 17, IntervalUnit.MILLISECONDS);

        m.put(two, twoValue);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetValue() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12.2, IntervalUnit.NANOSECONDS);
        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>().add(oneValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        assertTrue((sample.<IntervalUnit>getQuantity("one"))
                .isEqualsTo(
                    IntervalUnit.NANOSECONDS.quantity(12.2)) );
    }

    @Test
    public void shouldGetTestNames() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(PN.pname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(PN.pname("three"), 20, IntervalUnit.MILLISECONDS);

        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Collection<? extends CharSequence> coll =
                new Sample(MockStatsType.INSTANCE, map).getTestNames();

        assertTrue(coll.contains(PN.pname("one")));
        assertTrue(coll.contains(PN.pname("two")));
        assertTrue(coll.contains(PN.pname("three")));
    }

    @Test
    public void shouldGetCsvString() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(PN.pname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(PN.pname("three"), 20, IntervalUnit.MILLISECONDS);

        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample =new Sample(MockStatsType.INSTANCE, map);

        assertEquals("Mock Stats, one, 12.0, two, 17.0, three, 20.0",
                sample.toCsv());
    }

    @Test
    public void shouldGetString() {
        SampleValue oneValue =
                new SampleValue(PN.pname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(PN.pname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(PN.pname("three"), 20, IntervalUnit.MILLISECONDS);

        PathNamedMap<SampleValue> map = new PathNamedMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample =new Sample(MockStatsType.INSTANCE, map);

        assertEquals(
                "Mock Stats{one=12.0000 ms, two=17.0000 ms, three=20.0000 ms}",
                sample.toString());
    }
}
