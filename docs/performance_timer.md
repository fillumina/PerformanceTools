# Performance Timer

It's the base class of the API. It encapsulates all the mechanisms of a
performance test and must be initialized with an executor that is in charge
to actually perform the tests.

There are two executors available:

1)  [SingleThreadPerformanceExecutor.java]
    (../performance-tools/src/main/java/com/fillumina/performance/producer/timer/SingleThreadPerformanceExecutor.java)
    allows to execute the tests on the same thread. The tests will be interleaved
    so to average the effect of a disturbance (CPU fluctuations).
2)  [MultiThreadPerformanceExecutor.java]
    (../performance-tools/src/main/java/com/fillumina/performance/producer/timer/MultiThreadPerformanceExecutor.java)
    where the tests are executed in a multi threaded environment. The tests must be
    constructed considering that they will be executed concurrently. Usually
    multi-threaded tests are more difficult to write and assess because there are
    many more variables involved.

[PerformanceTimerFactory.java]
(../performance-tools/src/main/java/com/fillumina/performance/PerformanceTimerFactory.java)
is a factory that helps building the `PerformanceTimer` class.

[Back to index](documentation_index.md)