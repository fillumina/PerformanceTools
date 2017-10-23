package com.fillumina.performance.mem;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MemAllocator {

    private static class Memory {
        private final Object[] array;
        private int index = 0;

        private Memory(int slot) {
            this.array = new Object[slot];
        }

        void allocate(Object o) {
            array[index++] = o;
        }
    }

    private final int max;
    private final ThreadLocal<Memory> tlocal = new ThreadLocal<Memory>() {
        @Override
        protected Memory initialValue() {
            return new Memory(max);
        }
    };

    public MemAllocator() {
        this(1024);
    }

    public MemAllocator(int slots) {
        this.max = slots;
    }

    public void allocate(Object o) {
        tlocal.get().allocate(o);
    }
}
