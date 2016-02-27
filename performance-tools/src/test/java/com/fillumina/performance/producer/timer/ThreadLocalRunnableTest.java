package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerFactory;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ThreadLocalRunnableTest {
    private static final int WORKER_NUMBER = 16;

    @Test
    public void shouldUseALocalObject() {
        final Set<Object> set =
                Collections.newSetFromMap(new ConcurrentHashMap<Object, Boolean>());

        PerformanceTimerFactory.getMultiThreadedBuilder()
                .setThreads(WORKER_NUMBER)
                .setWorkers(WORKER_NUMBER)
                .build()

        .addTest("threadLocalTest", new ThreadLocalTestable<Object>() {

            @Override
            protected Object createThreadLocalObject() {
                return new Object();
            }

            @Override
            public Object test(final Object localObject) {
                return set.add(localObject);
            }
        })

        .iterate(1);

        assertEquals(WORKER_NUMBER, set.size());
    }
}
