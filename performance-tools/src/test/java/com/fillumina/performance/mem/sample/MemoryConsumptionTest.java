package com.fillumina.performance.mem.sample;

import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.Bag;
import com.fillumina.performance.util.Bag.Frequency;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
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
        assertEquals(0, MemoryConsumption.armonize(0, 16));
        assertEquals(32, MemoryConsumption.armonize(32, 16));
        assertEquals(32, MemoryConsumption.armonize(40, 16));
        assertEquals(48, MemoryConsumption.armonize(48, 16));
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

    @Ignore @Test //TODO keep failing... there are problems with huge allocations
    public void shouldEvaluateAnObjectBiggerThan1Mb() {
        final int size = 1_500_000;
        assertEquals(MemoryConsumption.INSTANCE.toString(),
                size * 4, evaluateMemoryUsage(new AbstractTestable() {
            @Override
            public Object test() {
                return new int[size];
            }
        }), 24);
    }

    private long evaluateMemoryUsage(Testable test) {
        Bag<Long> bag = new Bag<>();
        for (int i=0; i<10; i++) {
            final long bytes = memoryUsage(32, test);
            bag.add(bytes);
        }
        final List<Frequency<Long>> orderedEntryList =
                bag.getOrderedEntryList();
        // returns the most frequent returned memory
        return orderedEntryList.get(0).getValue();
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
