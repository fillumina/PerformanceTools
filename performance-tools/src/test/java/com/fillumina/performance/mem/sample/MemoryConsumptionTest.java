package com.fillumina.performance.mem.sample;

import com.fillumina.performance.mem.MemUtil;
import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryConsumptionTest {

    public static void main(final String[] args) {
        System.out.println(MemoryAllocatorInfo.INSTANCE.getDebugString());
        for (int i=0; i<10; i++) {
            MemoryConsumption mc = new MemoryConsumption();
            System.out.println(mc.toString());
        }
    }

    private static class Person {
        final String name;
        final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    public void shouldArmonizeZero() {
        assertEquals(0, MemUtil.align(0, 16));
        assertEquals(32, MemUtil.align(32, 16));
        assertEquals(32, MemUtil.align(40, 16));
        assertEquals(48, MemUtil.align(48, 16));
    }

    @Test
    public void shouldEvaluateZeroMemoryUsage() {
        assertEquals(MemoryConsumption.INSTANCE.toString(),
                0, evaluateMemoryUsage(new AbstractTestable() {
            @Override
            public Object test() {
                return null;
            }
        }));
    }

    @Test
    public void shouldEvaluateEmptyIntArray() {
        assertEquals(MemoryConsumption.INSTANCE.toString(),
                16, evaluateMemoryUsage(new AbstractTestable() {
            @Override
            public Object test() {
                return new int[0]; // this is 16 byte
            }
        }));
    }

    @Test
    public void shouldEvaluatePerson() {
        assertTrue(MemoryConsumption.INSTANCE.toString(),
                16 < evaluateMemoryUsage(new AbstractTestable() {
            int i=0;
            @Override
            public Object test() {
                return new Person("Mario" + i, i++);
            }
        }));
    }

    private long evaluateMemoryUsage(Testable test) {
        return memoryUsage(32, test);
//        Bag<Long> bag = new Bag<>();
//        for (int i=0; i<10; i++) {
//            final long bytes = memoryUsage(32, test);
//            bag.add(bytes);
//        }
//        final List<Frequency<Long>> orderedEntryList =
//                bag.getOrderedEntryList();
//        // returns the most frequent returned memory
//        return orderedEntryList.get(0).getValue();
    }

    private long memoryUsage(int repetitions, Testable test) {
        MemoryConsumption mem = MemoryConsumption.INSTANCE;
        int i=0;
        mem.start();
        for (;i<repetitions; i++) {
            if (test.test() == this) {
                throw new AssertionError("cannot happen");
            }
        }
        return mem.getUsedMemory() / repetitions;
    }
}
