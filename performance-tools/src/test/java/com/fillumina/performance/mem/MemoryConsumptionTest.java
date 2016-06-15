package com.fillumina.performance.mem;

import static org.junit.Assert.assertEquals;
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
    public void shouldRecognizeMemoryUsage() {
        long allocate = 0;
        for (int i=0; i<100; i++) {
            allocate = allocate(1);
            System.out.println("" + i + ": " + (allocate));
        }
    }

    private long allocate(int size) {
        MemoryConsumption mem = new MemoryConsumption();
        Object[] array = new Object[size];
        long usedMemory = -1;
        int i = -1;
        mem.start();
        for (i=0; i<size; i++) {
            array[i] = memTest();//new int[0]; //new Person("alfa" + i, i);
        }
        usedMemory = mem.getUsedMemory();

        int counter = 0;
        for (Object p : array) {
            if (p != null) {
                counter++;
            }
        }
        assertEquals(counter, size);
        return usedMemory;
    }

    private Object memTest() {
//        return new int[0];
        return new Person("alfa" + System.nanoTime(), (int)System.nanoTime());
    }
}
