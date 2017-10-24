package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.executor.sample.SampleValue;
import com.fillumina.performance.util.tname.TNameMap;
import com.fillumina.performance.util.unit.IntervalUnit;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SampleCreator {

    public static Sample createSample(Object... array) {
        TNameMap<SampleValue> map = createMap(array);
        return new Sample(map);
    }

    public static TNameMap<SampleValue> createMap(Object... array)
            throws NumberFormatException {
        TNameMap<SampleValue> map = new TNameMap<>();
        for (int i=0,l=array.length; i<l; i+=2) {
            String name = (String) array[i];
            double value = Double.valueOf(Objects.toString(array[i+1]));
            SampleValue sampleValue = new SampleValue(
                    TN.tname(name), value, IntervalUnit.MILLISECONDS);
            map.add(sampleValue);
        }
        return map;
    }
}
