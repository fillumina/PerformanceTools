# Paradigms

This API supports three different paradigms:

1) _Direct construction_ is the simplest and most basic one, it consists in
    the construction of the needed objects using the java `new` operator and to pass
    them by setters or constructor arguments. Thought this way is the most
    complex one it allows for the wider customization.

2) _Fluent interface_ is easier to write and read. The interface is structured by
    using components that can be linked together to obtain the needed features in
    using a very expressive and auto-documenting chain of methods.
    It is still easy to customize.

3) _Templates_ are abstract classes that forces some method to be filled in.
    The advantage here is semplicity because they are really intuitive and easy to
    use.

This is an example of a performance test written using a _fluent interface_
[DivisionByTwoPerformanceTest.java]
(../performance-tools-examples/src/test/java/com/fillumina/performance/examples/fluent/DivisionByTwoPerformanceTest.java):
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

                .use(AssertPerformance.withTolerance(5)
                    .assertTest("binary").fasterThan("math"))

                .whenever(display).println();
    }
}
```

The same example is rewritten here taking advantage of a template
[DivisionByTwoPerformanceTest.java]
(../performance-tools-examples/src/test/java/com/fillumina/performance/examples/template/DivisionByTwoPerformanceTest.java):
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
        assertion.withPercentageTolerance(5)
                .assertTest("math").fasterThan("binary");
    }
}
```

The template forces to fill the needed methods making it easy to remember what
steps to take in order to write the test.

[Back to index](documentation_index.md)