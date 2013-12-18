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

This is an example of a performance test written using a _fluent interface_
([DivisionByTwoPerformanceTest.java]
(../performance-tools-examples/src/test/java/com/fillumina/performance/examples/fluent/DivisionByTwoPerformanceTest.java)):
```java
public class DivisionByTwoPerformanceTest {

    private boolean display;

    public static void main(final String[] args) {
        final DivisionByTwoPerformanceTest test =
                new DivisionByTwoPerformanceTest();
        test.display = true;
        test.executeTest();
    }

    @Test
    public void executeTest() {
        final Random rnd = new Random(System.currentTimeMillis());

        PerformanceTimerFactory.createSingleThreaded()
                .addTest("math", new RunnableSink() {

                    @Override
                    public Object sink() {
                        return rnd.nextInt() / 2;
                    }
                })

                .addTest("binary", new RunnableSink() {

                    @Override
                    public Object sink() {
                        return rnd.nextInt() >> 1;
                    }
                })

                .instrumentedBy(
                        AutoProgressionPerformanceInstrumenter.builder()
                            .setMaxStandardDeviation(2)
                            .build())

                .execute()

                .use(AssertPerformance.withTolerance(7)
                    .assertTest("binary").fasterThan("math"))

                .printIf(display);
    }
}
```

Note that it is possible to add customized objects to the chain so to modify
the behavior of the test.

The same example is rewritten here taking advantage of a template
([DivisionByTwoPerformanceTest.java]
(../performance-tools-examples/src/test/java/com/fillumina/performance/examples/template/DivisionByTwoPerformanceTest.java)):
```java
public class DivisionByTwoPerformanceTest
        extends JUnitAutoProgressionPerformanceTemplate {

    public static void main(final String[] args) {
        new DivisionByTwoPerformanceTest().executeWithIntermediateOutput();
    }

    @Override
    public void init(ProgressionConfigurator config) {
        config.setMaxStandardDeviation(2);
    }

    @Override
    public void addTests(TestsContainer tests) {
        final Random rnd = new Random(System.currentTimeMillis());

        tests.addTest("math", new RunnableSink() {

            @Override
            public Object sink() {
                return rnd.nextInt() / 2;
            }
        });

        tests.addTest("binary", new RunnableSink() {

            @Override
            public Object sink() {
                return rnd.nextInt() >> 1;
            }
        });
    }

    @Override
    public void addAssertions(PerformanceAssertion assertion) {
        assertion.withPercentageTolerance(7)
                .assertTest("math").fasterThan("binary");
    }
}
```

The template requires to fill the needed methods making it easy to remember what
steps to take in order to write the test. On the other hand the template isn't
easily customizable.

[Back to index](documentation_index.md)