# Thread awarness

Some code must be executed in a multi-threaded environment and so they should
be tested in that same environment as well. This is easily done by just
changing the static method called in the
[PerformanceTimerFactory.java]
(../performance-tools/src/main/java/com/fillumina/performance/PerformanceTimerFactory.java).

```java
PerformanceTimer pt = PerformanceTimerFactory.getMultiThreadedBuilder()
        .setThreads(threads)
        .setWorkers(workers)
        .setTimeout(5, TimeUnit.SECONDS)
        .build();
```

or in templates:

```java
@Override
public void init(final ProgressionConfigurator config) {
    config.setConcurrencyLevel(32);
}
```

