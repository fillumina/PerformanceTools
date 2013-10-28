# Assertion

An important feature of this API is the possibility to assert the performances
(for example in a unit test).
It's very useful if you need to check if some code is as fast as expected
throughout the development cycle. The assertion mechanism is very simple
because it is a performance consumer and can be attached to all type of producers.
Just take care that some producers uses a complex scheme where there are tests
and executions. An execution is for example the name of the passed parameter.

This is an example of a simple assertion:
```java
assertion.withPercentageTolerance(7)
        .assertTest("math").fasterThan("binary");
```

This is a more complex one involving a test with parameters
("CURRENT RANDOM READ" is the name of the parameter passed):
```java
assertion.forExecution("CONCURRENT RANDOM READ")
    .withPercentageTolerance(7)
    .assertTest("SynchronizedHashMap").slowerThan("ConcurrentHashMap");
```

In case of a parametrized test with a sequence the name of the asserted test
is the composition of the name of the actual test and the string representation
of the sequence (this behavior can be modified by setting a
[SequenceNominator.java]
(../performance-tools/src/main/java/com/fillumina/performance/producer/suite/SequenceNominator.java).
```java
assertion.forExecution("test-10")
    .assertTest("linear").fasterThan("binary");

assertion.forExecution("test-30")
    .assertTest("binary").fasterThan("linear");
```


Assertions can also refers to a percentage directly:
```java
assertion.withPercentageTolerance(7)
    .assertPercentageFor("LinkedList").lessThan(30);
```

Note that usually performances assessed in a unit test run are much less
precise than when you ran them in a stand-alone program.

[Back to index](documentation_index.md)