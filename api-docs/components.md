# Components

The API is structured into two kind of components that can be attached
together to create different type of performance tests.
Basically it follows the listener or producer-consumer pattern.

1) [PerformanceConsumer]([DivisionByTwoPerformanceTest.java]
(../performance-tools/src/main/java/com/fillumina/performance/consumer/PerformanceConsumer.java)
this component will, at some time, call the consumers and pass them the
performance data collected. A consumer can be a viewer that simply display the
data in an appropriated form or an assertion that checks if the performances
match what expected.

2) [PerformanceProducer]([DivisionByTwoPerformanceTest.java]
(../performance-tools/src/main/java/com/fillumina/performance/producer/PerformanceProucer.java)
is the component that produces the performance data to be digested by the
consumers. A component can be at the same time a producer and a consumer
meaning that it will consume performance data, elaborate them and produce them
for clients to read.
