package com.fillumina.jmh.ptcomparison;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
/**
 *
 * @author Francesco Illuminati
 */
@State(Scope.Thread)
public class ExceptionVsBoundaryPerformanceTest {

    public static void main(final String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ExceptionVsBoundaryPerformanceTest.class.getSimpleName())
                .forks(1)
//                         .warmupTime(TimeValue.seconds(1))
//                         .warmupIterations(5)
//                         .measurementTime(TimeValue.seconds(1))
//                         .measurementIterations(5)
//                         .threads(1)
//                         .forks(1)
//                         .shouldFailOnError(true)
//                         .shouldDoGC(true)
//                         .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput/*, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime*/})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void exception() {
        if (exceptionTest.test() == Integer.MIN_VALUE) {
            // avoid dead code eviction
            throw new AssertionError();
        }

    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput/*, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime*/})
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void boundary() {
        if (boundaryTest.test() == Integer.MIN_VALUE) {
            // avoid dead code eviction
            throw new AssertionError();
        }
    }

    private final TestableException exceptionTest = new TestableException();
    private final BoundaryTestable boundaryTest = new BoundaryTestable();

    private static class TestableException {
        private final int[] array = new int[10];
        private int counter = 0;

        public int test() {
            counter++;
            try {
                array[counter] = counter;
            } catch (ArrayIndexOutOfBoundsException e) {
                counter = 0;
            }
            return array[counter];
        }
    }

    private static class BoundaryTestable {
        private final int[] array = new int[10];
        private int counter = 0;

        public int test() {
            counter++;
            if (counter < array.length) {
                array[counter] = counter;
            } else {
                counter = 0;
            }
            return array[counter];
        }
    }
}
