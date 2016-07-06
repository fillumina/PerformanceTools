package com.fillumina.performance.mem;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemSample implements Serializable {
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
