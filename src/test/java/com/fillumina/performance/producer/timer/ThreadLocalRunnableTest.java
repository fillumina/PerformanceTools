package com.fillumina.performance.producer.timer;

import com.fillumina.performance.PerformanceTimerBuilder;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fra
 */
public class ThreadLocalRunnableTest {
    private static final int WORKER_NUMBER = 16;

    @Test
    public void shouldUseALocalObject() {
        final Set<Object> set =
                Collections.newSetFromMap(new ConcurrentHashMap<Object, Boolean>());

        PerformanceTimerBuilder.createMultiThread()
                .setConcurrencyLevel(WORKER_NUMBER)
                .setWorkerNumber(WORKER_NUMBER)
                .build()

        .addTest("threadLocalTest", new ThreadLocalRunnable<Object>() {

            @Override
            protected Object createLocalObject() {
                return new Object();
            }

            @Override
            public void run(final Object localObject) {
                set.add(localObject);
            }
        })

        .iterate(1);

        assertEquals(WORKER_NUMBER, set.size());
    }
}
