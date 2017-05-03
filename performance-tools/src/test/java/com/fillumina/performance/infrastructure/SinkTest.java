package com.fillumina.performance.infrastructure;

import com.fillumina.performance.PerformanceTimerFactory;
import static com.fillumina.performance.infrastructure.Sink.drain;
import com.fillumina.performance.mock.NullTestable;
import com.fillumina.performance.speed.sample.DefaultPerformanceTimer;
import com.fillumina.performance.speed.sample.InvalidTestException;
import com.fillumina.performance.speed.sample.SpeedSample;
import com.fillumina.performance.util.rnd.HighQualityRandom;
import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinkTest {
    private boolean printout;

    @Test
    public void shouldDrainObject() {
        Sink.drain(SinkTest.class);
        checkIfItIsEvicted("object", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextBoolean() ? this.getClass() : SinkTest.class);
            }
        });
    }

    @Test
    public void shouldDrainBoolean() {
        Sink.drain(true);
        Sink.drain(false);
        checkIfItIsEvicted("bool", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextBoolean());
            }
        });
    }

    @Test
    public void shouldDrainByte() {
        Sink.drain(Byte.MAX_VALUE);
        checkIfItIsEvicted("byte", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain((byte)rnd.nextInt(128));
            }
        });
    }

    @Test
    public void shouldDrainShort() {
        Sink.drain(Short.MAX_VALUE);
        checkIfItIsEvicted("short", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain((short)rnd.nextInt(1_024));
            }
        });
    }

    @Test
    public void shouldDrainChar() {
        Sink.drain(Character.MAX_VALUE);
        checkIfItIsEvicted("char", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain((char)rnd.nextInt(128));
            }
        });
    }

    @Test
    public void shouldDrainInt() {
        Sink.drain(Integer.MAX_VALUE);
        checkIfItIsEvicted("int", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextInt());
            }
        });
    }

    @Test
    public void shouldDrainLong() {
        Sink.drain(Long.MAX_VALUE);
        checkIfItIsEvicted("long", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextLong());
            }
        });
    }

    @Test
    public void shouldDrainFloat() {
        Sink.drain(Float.MAX_VALUE);
        Sink.drain(Float.POSITIVE_INFINITY);
        checkIfItIsEvicted("float", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextFloat());
            }
        });
    }

    @Test
    public void shouldDrainDouble() {
        Sink.drain(Double.MAX_VALUE);
        Sink.drain(Double.POSITIVE_INFINITY);

        checkIfItIsEvicted("double", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextDouble());
            }
        });
    }

    @Test(expected = InvalidTestException.class)
    public void shouldEvictTest() {
        int x = 12;
        checkIfItIsEvicted("good", new Runnable() {
            @Override
            public void run() {
                drain(x);
            }
        });
    }

    @Test(expected = InvalidTestException.class)
    public void shouldEvictBadTest() {
        checkIfItIsEvicted("bad", new Runnable() {
            @Override
            public void run() {
                drain(12);
            }
        });
    }

    @Test(expected = InvalidTestException.class)
    public void shouldEvictNoSideEffectTest() {
        checkIfItIsEvicted("evict", new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Test(expected = InvalidTestException.class)
    public void shouldEvictNoSideEffectNullTestable() {
        checkIfItIsEvicted("null", NullTestable.INSTANCE);
    }

    @Test
    public void shouldNotEvictLfsr() {
        checkIfItIsEvicted("lfsr", new LfsrTestable());
    }

    private String call(int i) {
        return "int";
    }

    private String call(Object o) {
        return "object";
    }

    @Test
    public void shouldIntegerParamCallObject() {
        Integer i = 5;
        assertEquals("object", call(i));
    }

    //Include exorcism.h
    private void checkIfItIsEvicted(String name, Runnable testable) {
        final DefaultPerformanceTimer pt = PerformanceTimerFactory
                .createSingleThreaded()
                .addTest(name, testable);
        int iterations = pt.iterationTimeEstimatorMs(250)[0];
        if (printout) {
            System.out.print(name + ":\t");
            System.out.println("iterations       " + iterations);
        }
        final SpeedSample sample = pt.iterate(iterations);
        if (printout) {
            System.out.println(sample.getMeasure(name).getMean());
            System.out.println("total time       " + sample.getTotalTimeNs());
        }
    }

    public static void main(final String[] args) {
        SinkTest test = new SinkTest();
        test.printout = true;

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
        test.shouldEvictTest();
        test.shouldEvictNoSideEffectTest();
    }
}
