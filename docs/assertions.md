# Assertion

An important feature of this API is the possibility to assert the performances.
It's very useful if you need to check if an algorithm is as fast as expected
throughout the development phase. The assertion mechanism is very simple
because it is a performance consumer and can be attached to all producers.
Just take care that some producers uses a complex scheme where there are tests
and execution. An execution is for example the name of the parameter by which
some tests are compared.

[Back to index](documentation_index.md)