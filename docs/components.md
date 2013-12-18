# Components

The API uses a producer-consumer pattern so that various performance producers
can pass their data to various performance consumers like viewers or
assertions. Composing the two allows for many different and useful combinations.

*   A [PerformanceConsumer]
    (../performance-tools/src/main/java/com/fillumina/performance/consumer/PerformanceConsumer.java)
    consumes the performance produced by a producer.
    It can be a viewer that simply display the collected performances
    in an appropriated form or an assertion that checks if the performances
    match what expected.

*   [PerformanceProducer]
    (../performance-tools/src/main/java/com/fillumina/performance/producer/PerformanceProucer.java)
    is the component that executes the tests and produces the performance data
    to be digested by the consumers.
    A component can be at the same time a producer and a consumer
    meaning that it will consume performance data, elaborate them and produce them
    again for clients to read.

There is another kind of abstraction that helps connecting the various components
together and it is __instrumentation__.
An *instrumentable* is a producer that can be instrumented by an *instrumenter*
to execute its tests. For example an instrumenter can repeat the tests of
a instrumentable until their performances is stable. The same instrumenter can
be given a multi-threaded or a single-threaded producer to control leaving for
a wide variety of interesting combinations.

*   An [InstrumentablePerformanceExecutor]
    (../performance-tools/src/main/java/com/fillumina/performance/producer/InstrumentablePerformanceExecutor.java)
    can be instrumented to execute its test how many times as it is needed.

*   A [PerformanceExecutorInstrumenter]
    (../performance-tools/src/main/java/com/fillumina/performance/producer/PerformanceExecutorInstrumenter.java)
    is able to instrument an instrumentable producer so to run its tests.

[Back to index](documentation_index.md)