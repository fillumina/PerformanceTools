package com.fillumina.performance.mem.sample;

import com.fillumina.performance.infrastructure.type.Mem;
import com.fillumina.performance.util.stats.Measure;
import com.fillumina.performance.util.stats.MeasureRatio;
import com.fillumina.performance.util.unit.DimensionalOnlineMeasure;
import com.fillumina.performance.util.unit.MemUnit;
import java.io.Serializable;
import java.util.Objects;
import com.fillumina.performance.infrastructure.type.AssertableSample;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSample implements Mem, AssertableSample, Serializable {
    private static final long serialVersionUID = 1L;

    private final String testName;
    private final long bytes;

    public MemSample(String testName, long bytes) {
        this.testName = testName;
        this.bytes = bytes;
    }

    public String getTestName() {
        return testName;
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public Measure getValue(String testName) {
        return new DimensionalOnlineMeasure(MemUnit.B, (double)bytes);
    }

    @Override
    public MeasureRatio getRatioWithSlowestTest(String testName) {
        return new MeasureRatio(getValue(testName), 0.99);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.testName);
        hash = 29 * hash + (int) (this.bytes ^ (this.bytes >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MemSample other = (MemSample) obj;
        if (this.bytes != other.bytes) {
            return false;
        }
        if (!Objects.equals(this.testName, other.testName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MemSample{" + "testName=" + testName + ", bytes=" + bytes + '}';
    }
}
