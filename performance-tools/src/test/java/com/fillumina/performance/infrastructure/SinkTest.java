package com.fillumina.performance.infrastructure;

import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinkTest {

    @Test
    public void shouldDrainObject() {
        Sink.drain(Sink.class);
    }

    public static void main(final String[] args) {
        new PerformanceTemplate() {
            @Override
            public void addAssertions(ProgressionAssertion assertions) {
            }

            @Override
            public void config(TestConfiguration config) {
                config.speedTestOnly();
            }

            @Override
            public void addTests(TestContainer<Testable> tests) {
                tests.addTest("single", new Testable() {
                    private int i;
                    @Override
                    public void test() {
                        drain(i++);
                    }
                });
                tests.addTest("double", new Testable() {
                    private int i;
                    @Override
                    public void test() {
                        drain(i++);
                        drain(i++);
                    }
                });
                tests.addTest("triple", new Testable() {
                    private int i;
                    @Override
                    public void test() {
                        drain(i++);
                        drain(i++);
                        drain(i++);
                    }
                });

            }
        }.executeWithFullOutput();
    }

    @Test
    public void shouldDrainBoolean() {
        Sink.drain(true);
    }

    @Test
    public void shouldDrainByte() {
        Sink.drain(Byte.MAX_VALUE);
    }

    @Test
    public void shouldDrainShort() {
        Sink.drain(Short.MAX_VALUE);
    }

    @Test
    public void shouldDrainChar() {
        Sink.drain(Character.MAX_VALUE);
    }

    @Test
    public void shouldDrainInt() {
        Sink.drain(Integer.MAX_VALUE);
    }

    @Test
    public void shouldDrainLong() {
        Sink.drain(Long.MAX_VALUE);
    }

    @Test
    public void shouldDrainFloat() {
        Sink.drain(Float.MAX_VALUE);
        Sink.drain(Float.POSITIVE_INFINITY);
    }

    @Test
    public void shouldDrainDouble() {
        Sink.drain(Double.MAX_VALUE);
        Sink.drain(Double.POSITIVE_INFINITY);
    }

}
