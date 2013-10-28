# Templates

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

3.  [ParametrizedPerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/ParametrizedPerformanceTemplate.java)
    allows to define a test that accepts a parameter and then to define and pass to
    the test several parameters. It is useful to check which of a serie of
    alternative objects performs better (i.e. which different `Map` perform
    better writing objects randomly).

4.  [ParametrizedSequencePerformanceTemplate.java]
    (../performance-tools/src/main/java/com/fillumina/performance/template/ParametrizedSequencePerformanceTemplate.java)
    in addition to accepting a parameter it accepts an elemnet of a sequence too. It
    is useful to check which of a series of alternative objects perform better on
    different input (i.e. which different `Map` perform better writing objects
    randomly in a map of different sizes).

[Back to index](documentation_index.md)