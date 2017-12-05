package com.fillumina.performance.executor.sample;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.mock.MockStatsType;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Collection;
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
                new SampleValue(TN.tname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(TN.tname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(TN.tname("three"), 20, IntervalUnit.MILLISECONDS);

        TNameMap<SampleValue> map = new TNameMap<SampleValue>()
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
                new SampleValue(TN.tname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(TN.tname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(TN.tname("three"), 20, IntervalUnit.MILLISECONDS);

        TNameMap<SampleValue> map = new TNameMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        TNameMap<SampleValue> m = sample.getValuesMap();
        assertEquals(oneValue, m.get("one"));
        assertEquals(twoValue, m.get("two"));
        assertEquals(threeValue, m.get("three"));
    }

    @Test(expected=UnsupportedOperationException.class)
    public void shouldGetValuesMapBeUnmodifiable() {
        SampleValue oneValue =
                new SampleValue(TN.tname("one"), 12, IntervalUnit.MILLISECONDS);

        TNameMap<SampleValue> map = new TNameMap<SampleValue>().add(oneValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        TNameMap<SampleValue> m = sample.getValuesMap();

        SampleValue twoValue =
                new SampleValue(TN.tname("two"), 17, IntervalUnit.MILLISECONDS);

        m.add(twoValue);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldGetValue() {
        SampleValue oneValue =
                new SampleValue(TN.tname("one"), 12.2, IntervalUnit.NANOSECONDS);
        TNameMap<SampleValue> map = new TNameMap<SampleValue>().add(oneValue);

        Sample sample = new Sample(MockStatsType.INSTANCE, map);

        assertTrue((sample.<IntervalUnit>getQuantity("one"))
                .isEqualsTo(
                    IntervalUnit.NANOSECONDS.quantity(12.2)) );
    }

    @Test
    public void shouldGetTestNames() {
        SampleValue oneValue =
                new SampleValue(TN.tname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(TN.tname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(TN.tname("three"), 20, IntervalUnit.MILLISECONDS);

        TNameMap<SampleValue> map = new TNameMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Collection<? extends CharSequence> coll =
                new Sample(MockStatsType.INSTANCE, map).getTestNames();

        assertTrue(coll.contains(TN.tname("one")));
        assertTrue(coll.contains(TN.tname("two")));
        assertTrue(coll.contains(TN.tname("three")));
    }

    @Test
    public void shouldGetCsvString() {
        SampleValue oneValue =
                new SampleValue(TN.tname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(TN.tname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(TN.tname("three"), 20, IntervalUnit.MILLISECONDS);

        TNameMap<SampleValue> map = new TNameMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample =new Sample(MockStatsType.INSTANCE, map);

        assertEquals("Mock Stats, one, 1.2E7, two, 1.7E7, three, 2.0E7",
                sample.toCsv());
    }

    @Test
    public void shouldGetString() {
        SampleValue oneValue =
                new SampleValue(TN.tname("one"), 12, IntervalUnit.MILLISECONDS);
        SampleValue twoValue =
                new SampleValue(TN.tname("two"), 17, IntervalUnit.MILLISECONDS);
        SampleValue threeValue =
                new SampleValue(TN.tname("three"), 20, IntervalUnit.MILLISECONDS);

        TNameMap<SampleValue> map = new TNameMap<SampleValue>()
                .add(oneValue)
                .add(twoValue)
                .add(threeValue);

        Sample sample =new Sample(MockStatsType.INSTANCE, map);

        assertEquals("Mock Stats{one=12 ms, two=17 ms, three=20 ms}",
                sample.toString());
    }
}
