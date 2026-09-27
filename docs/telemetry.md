# Telemetry

Sometimes you need to evaluate which part of a long algorithm, possibly
embracing many different classes and methods, takes the most
time to execute. With telemetry you can divide your code into sections by
inserting a static telemetry stop watch point and see the percentage of
time spent in each of them:
```java
Telemetry.section("sorting");
```
The sections does not require to be on any class or method boundary, they can
just be wherever you want them to be.
Telemetry can be used in a multi-threaded environment (i.e. in a web server
where it can trace a single request against other requests being executed
at the same time). `getStatsFromAllThreads()` uses keys of the form
`thread-name#thread-id`, so two threads with the same name remain distinct.
Call `Telemetry.clear()` when the collected timers are no longer needed.

You should execute the code for some iterations to allows the telemetry to be
effective and gives valuable results.

This is an example code to show how to use it:
[TelemetryTest.java]
(../performance-tools/src/test/java/com/fillumina/performance/TelemetryTest.java).

And this is what it prints out if run directly:

```
(10 iterations)
START	   0 :	     17.38 us		      0.02 %
ONE  	   1 :	 20,082.57 us		     20.07 %
TWO  	   2 :	 10,074.94 us		     10.07 %
THREE	   3 :	100,087.31 us		    100.00 %
     	   * :	130,262.23 us
```

[Back to index](documentation_index.md)