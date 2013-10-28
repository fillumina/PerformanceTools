# Assertion

An important feature of this API is the possibility to assert the performances
(for example in a unit test).
It's very useful if you need to check if some code is as fast as expected
throughout the development phase. The assertion mechanism is very simple
because it is a performance consumer and can be attached to all producers.
Just take care that some producers uses a complex scheme where there are tests
and execution. An execution is for example the name of the parameter by which
some tests are compared.

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

[Back to index](documentation_index.md)