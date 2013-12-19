# Templates

Templates are available in the core module (```performance-tools```) in an
agnostic form (not depending on any test unit environment) and in
```perform-tools-junit``` in a JUnit specific form.

There are 4 templates available:

1.  [SimplePerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/SimplePerformanceTemplate.java)
    is just a skeleton that enables some basic templating but isn't really
    useful per-se.

2.  [AutoProgressionPerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/SimplePerformanceTemplate.java)
    allows to set a target stability value expressed by the maximum allowed
    standard deviation that a serie of samples of a certain number
    of iterations of the tests must have. In order to stabilize up to this value this
    template will repeat the serie of iterations incrementing its value over time.
    It's useful because it doesn't force to discover manually the number of
    iterations at which the test stabilizes.

    The JUnit template here is just an adaptation of the original template
    and can be found in the module ```performance-tools-junit```
    (it's in a different module to avoid having JUnit as a dependence on the
    core module).

    See [DivisionByTwoPerformanceTest.java]
    (../performance-tools-examples/src/test/java/com/fillumina/performance/examples/template/DivisionByTwoPerformanceTest.java).

3.  [ParametrizedPerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/ParametrizedPerformanceTemplate.java)
    allows to define a test that accepts a parameter and then to define and pass to
    the test several parameters. It is useful to check which of a serie of
    alternative objects performs better (i.e. which different `Map` perform
    better writing objects randomly).

    See [MapMultiThreadedPerformanceTest.java]
    (../performance-tools-examples/src/test/java/com/fillumina/performance/examples/template/MapMultiThreadedPerformanceTest.java).

4.  [ParametrizedSequencePerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/ParametrizedSequencePerformanceTemplate.java)
    in addition to accepting a parameter it accepts an elemnet of a sequence too. It
    is useful to check which of a series of alternative objects perform better on
    different input (i.e. which different `Map` perform better writing objects
    randomly in a map of different sizes).

    See [SearchTypePerformanceTest.java]
    (../performance-tools-examples/src/test/java/com/fillumina/performance/examples/template/SearchTypePerformanceTest.java).

[Back to index](documentation_index.md)