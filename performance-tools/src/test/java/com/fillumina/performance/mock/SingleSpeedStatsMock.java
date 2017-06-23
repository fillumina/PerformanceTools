package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.time.stats.SingleSpeedStats;
import com.fillumina.performance.util.unit.DimensionalMeasure;

/**
 *
 * @author Francesco Illuminati
 */
public class SingleSpeedStatsMock {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private DimensionalMeasure timeNs;
        private long totalIterations;
        private long samples;
        private long originalSamples;
        private long totalTime;

        public Builder name(final String value) {
            this.name = value;
            return this;
        }

        public Builder timeNs(final DimensionalMeasure value) {
            this.timeNs = value;
            return this;
        }

        public Builder totalIterations(final long value) {
            this.totalIterations = value;
            return this;
        }

        public Builder samples(final long value) {
            this.samples = value;
            return this;
        }

        public Builder originalSamples(final long value) {
            this.originalSamples = value;
            return this;
        }

        public Builder totalTime(final long value) {
            this.totalTime = value;
            return this;
        }

        public SingleSpeedStats build() {
            return new SingleSpeedStats(TN.tname(name),
                    timeNs, totalIterations, samples,
                    originalSamples, totalTime);
        }
    }
}
