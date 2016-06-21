package com.fillumina.performance.mem;

import com.fillumina.performance.speed.sample.AbstractTestable;
import com.fillumina.performance.speed.sample.Testable;
import com.fillumina.performance.util.Bag;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemoryConsumptionTest {

    private static class Person {
        final String name;
        final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
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

    @Test
    public void shouldEvaluateAnObjectBiggerThan1Mb() {
        final int size = 1_500_000;
        assertEquals(MemoryConsumption.INSTANCE.toString(),
                size * 4, evaluateMemoryUsage(new AbstractTestable() {
            @Override
            public Object test() {
                return new int[size];
            }
        }), 100);
    }

    private long evaluateMemoryUsage(Testable test) {
        Bag<Long> bag = new Bag<>();
        for (int i=0; i<30; i++) {
            bag.add(memoryUsage(test));
        }
        return getMostFrequentValue(bag);
    }

    private long getMostFrequentValue(Bag<Long> bag) {
        long mostFrqValue = -1;
        long higherFreq = -1;
        for (Map.Entry<Long,Long> entry : bag.getMap().entrySet()) {
            long frequency = entry.getValue();
            if (frequency > higherFreq) {
                higherFreq = frequency;
                mostFrqValue = entry.getKey();
            }
        }
        return mostFrqValue;
    }

    private long memoryUsage(Testable test) {
        MemoryConsumption mem = MemoryConsumption.INSTANCE;
        long usedMemory = -1;

        mem.start();
        if (test.test() == this) {
            throw new AssertionError("cannot happen");
        }
        usedMemory = mem.getUsedMemory();
        return usedMemory;
    }
}
