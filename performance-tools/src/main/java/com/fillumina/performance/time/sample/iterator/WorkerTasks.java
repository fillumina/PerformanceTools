package com.fillumina.performance.time.sample.iterator;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/** Propagates failures from completed timing workers to the caller. */
final class WorkerTasks {
    private WorkerTasks() {
    }

    static void checkFailures(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("interrupted while checking workers", e);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof Error) {
                    throw (Error) cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                throw new IllegalStateException("timing worker failed", cause);
            }
        }
    }
}
