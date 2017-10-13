package com.fillumina.performance.mock;

import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.sample.Sample;
import com.fillumina.performance.util.tname.TName;

/**
 *
 * @author Francesco Illuminati
 */
public class SampleMock extends Sample {


    public Sample addTest(String name, double... values) {

        return new SampleValueBuilder(TN.tname(name));
    }

    public Sample create(TName name, double... values) {
        return new SampleValueBuilder(name);
    }
}
