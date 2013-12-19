# Paradigms

This API supports three different paradigms:

1) _Direct construction_ is the most basic way of using this API. Every needed
    object must be constructed using the available constructors and setters.
    It's not easy to use but it's very flexible.
    It's a precise design choice to leave the library
    open and to protect as little as possible of it.

2) _Fluent interface_ is easier to write and read. The interface is structured by
    using components that can be linked together to obtain the needed features
    using a very expressive and auto-documenting chain of methods.
    The chain can be built using custom made objects.

3) _Templates_ are abstract classes that forces some method to be filled in.
    The advantage here is semplicity and easiness because they are really
    intuitive to use and don't require to remember how to setup the test.

This is an example of a performance test written using a _fluent interface_ :
[DivisionByTwoPerformanceTest.java]
(../performance-tools-examples/src/test/java/com/fillumina/performance/examples/fluent/DivisionByTwoPerformanceTest.java).

Note that it is possible to add customized objects to the chain so to modify
the behavior of the test.

The same example is rewritten here taking advantage of a template:
[DivisionByTwoPerformanceTest.java]
(../performance-tools-examples/src/test/java/com/fillumina/performance/examples/template/DivisionByTwoPerformanceTest.java).

The template requires to fill the needed methods making it easy to remember what
steps to take in order to write the test. On the other hand the template isn't
easily customizable.

[Back to index](documentation_index.md)