package com.fillumina.performance.mock;

import com.fillumina.performance.speed.sample.IterationTimeCollector;
import com.fillumina.performance.speed.sample.SpeedSample;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class MockSpeedSample {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<TestSample> list = new ArrayList<>();

        public TestSample addTest(String name) {
            return new TestSample(name);
        }

        public class TestSample {
            private String name;
            private long timeNs;
            private long iterations = 10L;

            public TestSample(String name) {
                this.name = name;
            }

            public TestSample timePerOp(final long value) {
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

        public SpeedSample createSample() {
            IterationTimeCollector collector = new IterationTimeCollector();
            for (TestSample ts : list) {
                collector.add(ts.name, ts.timeNs * ts.iterations, (int)ts.iterations);
            }
            return collector.createPerformanceSample();
        }
    }

    public static void main(final String[] args) {
        System.out.println("SAMPLE:");
        System.out.println(builder()
                .addTest("first")
                    .timePerOp(10)
                    .iterations(2_000)
                .endTest()
                .addTest("second")
                    .timePerOp(50)
                    .iterations(500)
                .endTest()
                .createSample());
    }
}
