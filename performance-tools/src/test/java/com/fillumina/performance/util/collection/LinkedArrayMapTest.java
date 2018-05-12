package com.fillumina.performance.util.collection;

import com.fillumina.performance.executor.annotation.BeforeSample;
import com.fillumina.performance.executor.annotation.Param;
import com.fillumina.performance.template.PerformanceBuilder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class LinkedArrayMapTest extends AbstractMapTest {

    @Override
    protected <K, V> Map<K, V> createMap() {
        return new LinkedArrayMap<>();
    }

    public static void main(final String[] args) {
        PerformanceBuilder
            .config()
                .speedConfig()
                    .setWarmupSamples(10)
                .end()
                .tests()
                    .addTest("put", new Runnable() {

                        @Param
                        private Map<Integer,Integer> map;

                        @BeforeSample
                        public void init() {
                            map.clear();
                        }

                        @Override
                        public void run() {
                            for (int i=0; i<100; i++) {
                                int rnd = ThreadLocalRandom.current().nextInt(1000);
                                map.put(rnd, -rnd);
                            }
                        }
                    })
                    .addTest("get", new Runnable() {

                        @Param
                        private Map<Integer,Integer> map;
                        private int[] sequence;

                        @BeforeSample
                        public void init() {
                            map.clear();
                            sequence = new int[10];
                            for (int i=0; i<10; i++) {
                                int rnd = ThreadLocalRandom.current().nextInt(1000);
                                map.put(rnd, -rnd);
                                sequence[i] = rnd;
                            }
                        }

                        @Override
                        public void run() {
                            int rnd;
                            for (int i=0; i<100; i++) {
                                rnd = sequence[i % 10];
                                if (-rnd != map.get(rnd)) {
                                    throw new RuntimeException();
                                }
                            }
                        }
                    })
                    .addParameter("map")
                        .value("LinkedArrayMap", new LinkedArrayMap<Integer,Integer>(16))
                        .value("LinkedHashMap", new LinkedHashMap<Integer,Integer>(16))
                        //.value("IndexedArrayMap", new IndexedArrayMap<Integer,Integer>(16))
                        .end()
                .end()
            .end()
        .end()
        .executeWithFullOutput();

    }
}
