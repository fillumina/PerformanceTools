package com.fillumina.performance.executor.test;

import static com.fillumina.performance.executor.test.Sink.drain;
import static com.fillumina.performance.executor.test.Sink.pass;
import com.fillumina.performance.mock.NullRunnable;
import com.fillumina.performance.time.sample.InvalidTestException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class SinkTest extends SinkTestHelper {

    @Test
    public void shouldNotEvictSinkedTest() {
        int x = 12;
        checkIfItIsEvicted("good", () -> { drain(x); });
    }

    @Test
    public void shouldNotEvictSinkedPassTest() {
        int x = 12;
        checkIfItIsEvicted("good", () -> { int z = pass(x); });
    }

    @Test
    public void shouldNotEvictBadTest() {
        checkIfItIsEvicted("bad", () -> { drain(12); });
    }

    @Test
    public void shouldNotEvictBadPassTest() {
        checkIfItIsEvicted("bad", () -> { int z = pass(12); });
    }

    @Test
    public void shouldNotEvictBooleanTest() {
        checkIfItIsEvicted("bad", () -> { drain(false); });
    }

    @Test
    public void shouldNotEvictBooleanPassTest() {
        checkIfItIsEvicted("bad", () -> { boolean b = pass(false); });
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
        drain(SinkTest.class);
    }

    @Test
    public void shouldNotEvictObjects() {
        checkIfItIsEvicted("object", () -> drain(SinkTest.class) );
    }

    @Test
    public void shouldNotEvictPassedObjects() {
        checkIfItIsEvicted("object", () -> { Object o = pass(SinkTest.class); } );
    }

    @Test
    public void shouldDrainBoolean() {
        drain(true);
        drain(false);
    }

    @Test
    public void shouldNotEvictBooleans() {
        checkIfItIsEvicted("bool", () -> drain(false) );
    }

    @Test
    public void shouldNotEvictPassedBooleans() {
        checkIfItIsEvicted("bool", () -> { boolean b = pass(false); } );
    }

    @Test
    public void shouldDrainByte() {
        drain(Byte.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictBytes() {
        checkIfItIsEvicted("byte", () -> drain((byte)12) );
    }

    @Test
    public void shouldNotEvictPassedBytes() {
        checkIfItIsEvicted("byte", () -> { byte b = pass((byte)12); } );
    }

    @Test
    public void shouldDrainShort() {
        drain(Short.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictShorts() {
        checkIfItIsEvicted("short", () -> drain((short)1_024) );
    }

    @Test
    public void shouldNotEvictPassedShorts() {
        checkIfItIsEvicted("short", () -> { short s = pass((short)1_024); } );
    }

    @Test
    public void shouldDrainChar() {
        drain(Character.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictCharacters() {
        checkIfItIsEvicted("char", () -> drain((char)18) );
    }

    @Test
    public void shouldNotEvictPassedCharacters() {
        checkIfItIsEvicted("char", () -> { char c = pass((char)18); } );
    }

    @Test
    public void shouldDrainInt() {
        drain(Integer.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictIntegers() {
        checkIfItIsEvicted("int", () -> drain(123) );
    }

    @Test
    public void shouldNotEvictPassedIntegers() {
        checkIfItIsEvicted("int", () -> { int i = pass(123); } );
    }

    @Test
    public void shouldDrainLong() {
        drain(Long.MAX_VALUE);
    }

    @Test
    public void shouldNotEvictLongs() {
        checkIfItIsEvicted("long", () -> drain(12345L) );
    }

    @Test
    public void shouldNotEvictPassedLongs() {
        checkIfItIsEvicted("long", () -> { long l = pass(12345L); } );
    }

    @Test
    public void shouldDrainFloat() {
        drain(Float.MAX_VALUE);
        drain(Float.POSITIVE_INFINITY);
    }

    @Test
    public void shouldNotEvicFloats() {
        checkIfItIsEvicted("float", new Runnable() {
            @Override
            public void run() {
                drain((float) 34.567);
            }
        });
    }

    @Test
    public void shouldNotEvicPassFloats() {
        checkIfItIsEvicted("float", () -> { float f = pass((float) 34.567); });
    }

    @Test
    public void shouldDrainDouble() {
        drain(Double.MAX_VALUE);
        drain(Double.POSITIVE_INFINITY);
    }

    @Test
    public void shouldNotEvictDoubles() {
        checkIfItIsEvicted("double", () -> drain(678.923) );
    }

    @Test
    public void shouldNotEvictPassedDoubles() {
        checkIfItIsEvicted("double", () -> { double d = pass(678.923); } );
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
        test.shouldNotEvictBadTest();
        test.shouldNotEvictSinkedTest();
        test.shouldEvictNoSideEffectTest();
    }
}
