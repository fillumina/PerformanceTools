package com.fillumina.performance.mock;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.time.sample.TimeSampleCollector;
import com.fillumina.performance.util.pathname.PathName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class SpeedSampleMock {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<TestSample> list = new ArrayList<>();

        public TestSample addTest(String name) {
            return new TestSample(name);
        }

        public TestSample addTest(PathName name) {
            return new TestSample(name);
        }

        public class TestSample {
            private final PathName name;
            private long timeNs;
            private long iterations = 10L;

            public TestSample(String name) {
                this(PN.pname(name));
            }

            public TestSample(PathName name) {
                this.name = name;
            }

            public TestSample nansecondsPerOp(final long value) {
                this.timeNs = value;
                return this;
            }

            public TestSample iterations(final long value) {
                this.iterations = value;
                return this;
            }

            public Builder endTest() {
                Builder.this.list.add(this);
                return Builder.this;
            }
        }

        public TimeSampleCollector createSample() {
            TimeSampleCollector collector = new TimeSampleCollector();
            for (TestSample ts : list) {
                collector.add(ts.name,
                        ts.timeNs * ts.iterations,
                        (int)ts.iterations);
            }
            return collector;
        }
    }

    public static void main(final String[] args) {
        System.out.println("SAMPLE:");
        System.out.println(builder()
                .addTest("first")
                    .nansecondsPerOp(10)
                    .iterations(2_000)
                .endTest()
                .addTest("second")
                    .nansecondsPerOp(50)
                    .iterations(500)
                .endTest()
                .createSample());
    }
}
