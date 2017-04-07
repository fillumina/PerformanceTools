package com.fillumina.performance.infrastructure;

import com.fillumina.performance.PerformanceTimerFactory;
import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.mock.NullTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.template.PerformanceTemplate;
import com.fillumina.performance.template.ProgressionAssertion;
import com.fillumina.performance.template.TestConfiguration;
import com.fillumina.performance.util.rnd.HighQualityRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinkTest {

    @Test
    public void shouldDrainObject() {
        Sink.drain(SinkTest.class);
        checkIfItIsEvicted("object", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain(rnd.nextBoolean() ? this.getClass() : SinkTest.class);
            }
        });
    }

    @Test
    public void shouldDrainBoolean() {
        Sink.drain(true);
        Sink.drain(false);
        checkIfItIsEvicted("bool", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain(rnd.nextBoolean());
            }
        });
    }

    @Test
    public void shouldDrainByte() {
        Sink.drain(Byte.MAX_VALUE);
        checkIfItIsEvicted("byte", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain((byte)rnd.nextInt(128));
            }
        });
    }

    @Test
    public void shouldDrainShort() {
        Sink.drain(Short.MAX_VALUE);
        checkIfItIsEvicted("short", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain((short)rnd.nextInt(1_024));
            }
        });
    }

    @Test
    public void shouldDrainChar() {
        Sink.drain(Character.MAX_VALUE);
        checkIfItIsEvicted("char", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain((char)rnd.nextInt(128));
            }
        });
    }

    @Test
    public void shouldDrainInt() {
        Sink.drain(Integer.MAX_VALUE);
        checkIfItIsEvicted("int", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain(rnd.nextInt());
            }
        });
    }

    @Test
    public void shouldDrainLong() {
        Sink.drain(Long.MAX_VALUE);
        checkIfItIsEvicted("long", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain(rnd.nextLong());
            }
        });
    }

    @Test
    public void shouldDrainFloat() {
        Sink.drain(Float.MAX_VALUE);
        Sink.drain(Float.POSITIVE_INFINITY);
        checkIfItIsEvicted("float", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain(rnd.nextFloat());
            }
        });
    }

    @Test
    public void shouldDrainDouble() {
        Sink.drain(Double.MAX_VALUE);
        Sink.drain(Double.POSITIVE_INFINITY);

        checkIfItIsEvicted("double", new Testable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void test() {
                drain(rnd.nextDouble());
            }
        });
    }

    @Test
    public void shouldNotEvictTest() {
        int x = 12;
        checkIfItIsEvicted("good", new Testable() {
            @Override
            public void test() {
                drain(x);
            }
        });
    }

    @Test
    public void shouldEvictBadTest() {
        checkIfItIsEvicted("bad", new Testable() {
            @Override
            public void test() {
                drain(12);
            }
        });
    }

    @Test
    public void shouldEvictNoSideEffectTest() {
        checkIfItIsEvicted("evict", new Testable() {
            @Override
            public void test() {
            }
        });
    }

    @Test
    public void shouldEvictNoSideEffectNullTestable() {
        checkIfItIsEvicted("null", NullTestable.INSTANCE);
    }

    @Test
    public void shouldNotEvictLfsr() {
        checkIfItIsEvicted("lfsr", new LfsrTestable());
    }

    @Test
    public void shouldAllTestFasterThanNull() {

    }

    //Include exorcism.h
    private void checkIfItIsEvicted(String name, Testable testable) {
        final DefaultPerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded()
                .addTest(name, testable);
        int iterations = pt.iterationTimeEstimatorMs(250)[0];
        System.out.print(name + ":\t");
        //System.out.println("iterations       " + iterations);
        final SpeedSample sample = pt.execute(iterations);
        //System.out.println(sample.getValue(name).getMean());
        //System.out.println("total time       " + sample.getTotalTimeNs());
    }

    public static void main(final String[] args) {
        executeTests();
        checkRandomDraing();
    }

    private static void executeTests() {
        SinkTest test = new SinkTest();

        test.shouldDrainBoolean();
        test.shouldDrainByte();
        test.shouldDrainChar();
        test.shouldDrainDouble();
        test.shouldDrainFloat();
        test.shouldDrainInt();
        test.shouldDrainLong();
        test.shouldDrainObject();
        test.shouldDrainShort();
        test.shouldEvictBadTest();
        test.shouldNotEvictTest();
        test.shouldEvictNoSideEffectTest();
    }

    private static void checkIntegerDraing() {
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

    private static void checkRandomDraing() {
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
                    @Override
                    public void test() {
                        drain(ThreadLocalRandom.current().nextInt());
                    }
                });
                tests.addTest("double", new Testable() {
                    @Override
                    public void test() {
                        drain(ThreadLocalRandom.current().nextInt());
                        drain(ThreadLocalRandom.current().nextInt());
                    }
                });
                tests.addTest("triple", new Testable() {
                    @Override
                    public void test() {
                        drain(ThreadLocalRandom.current().nextInt());
                        drain(ThreadLocalRandom.current().nextInt());
                        drain(ThreadLocalRandom.current().nextInt());
                    }
                });

            }
        }.executeWithFullOutput();
    }
}
