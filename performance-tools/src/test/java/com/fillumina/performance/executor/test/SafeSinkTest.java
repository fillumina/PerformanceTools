package com.fillumina.performance.executor.test;

import static com.fillumina.performance.executor.test.SafeSink.drain;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.time.sample.InvalidTestException;
import com.fillumina.performance.util.rnd.HighQualityRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SafeSinkTest extends SinkTestHelper {

    @Test
    public void shouldNotEvictSinkedTest() {
        int x = 12;
        checkIfItIsEvicted("good", () -> { drain(x); });
    }

    @Test
    public void shouldNotEvictBadTest() {
        checkIfItIsEvicted("bad", () -> { drain(12); });
    }

    @Test
    public void shouldNotEvictBooleanTest() {
        checkIfItIsEvicted("bad", () -> { drain(false); });
    }

    @Test(expected = InvalidTestException.class)
    public void shouldEvictNoSideEffectTest() {
        checkIfItIsEvicted("no code", () -> {});
    }

    @Test(expected = InvalidTestException.class)
    public void shouldEvictNoSideEffectNullTestable() {
        checkIfItIsEvicted("null", NullRunnable.INSTANCE);
    }

    @Test
    public void shouldNotEvictLfsr() {
        checkIfItIsEvicted("lfsr", new LfsrRunnable());
    }

    @Test
    public void shouldLfsrModifyItsValue() {
        List<Integer> list = new ArrayList<>();
        int l = ThreadLocalRandom.current().nextInt() | 1;
        for (int i=0; i<100; i++) {
            l = lfsr(l);
            if (list.contains(l)) {
                throw new AssertionError("value present");
            }
            list.add(l);
        }
    }

    private int lfsr(int value) {
        return ((value >>> 1) ^ (-(value & 1) & -536870400)) & -1;
    }

    @Test
    public void shouldAlwaysBeOdd() {
        int counter = Integer.MAX_VALUE - 4;
        for (int i=0; i<100; i++) {
            counter+=2;
            assertTrue((counter | 1) == counter);
        }
    }

    @Test
    public void shouldDrainObject() {
        drain(SafeSinkTest.class);
    }

    @Test
    public void shouldNotEvictObjects() {
        checkIfItIsEvicted("object", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextBoolean() ? this.getClass() : SafeSinkTest.class);
            }
        });
    }

    @Test
    public void shouldDrainBoolean() {
        drain(true);
        drain(false);
    }

    @Test
    public void shouldNotEvictBooleans() {
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
        drain(Byte.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictBytes() {
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
        drain(Short.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictShorts() {
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
        drain(Character.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictCharacters() {
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
        drain(Integer.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictIntegers() {
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
        drain(Long.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictLongs() {
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
        drain(Float.MAX_VALUE);
        drain(Float.POSITIVE_INFINITY);
    }

    @Test
    public void shouldNotEvicFloats() {
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
        drain(Double.MAX_VALUE);
        drain(Double.POSITIVE_INFINITY);
    }

    @Test
    public void shouldNotEvictDoubles() {
        checkIfItIsEvicted("double", new Runnable() {
            private final Random rnd = new HighQualityRandom();
            @Override
            public void run() {
                drain(rnd.nextDouble());
            }
        });
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

    public static void main(final String[] args) {
        SafeSinkTest test = new SafeSinkTest();
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
        test.shouldNotEvictBadTest();
        test.shouldNotEvictSinkedTest();
        test.shouldEvictNoSideEffectTest();
    }
}
