# Telemetry

Sometimes you need to evaluate which part of a long algorithm, possibly
embracing many different classes and methods, takes the most
time to execute. With telemetry you can divide your code into sections by
inserting a static telemetry stop watch point and see the percentage of
time spent in each of them:
```java
Telemetry.segment("sorting");
```
The sections does not require to be on any class or method boundary, they can
just be wherever you want them to be.
Telemetry can be used in a multi-threaded environment (i.e. in a web server
where it can trace a single request against other requests being executed
at the same time).

You should execute the code for some iterations to allows the telemetry to be
effective and gives valuable results.

This is an example code to show how to use it
([TelemetryTest.java]
(../performance-tools/src/test/java/com/fillumina/performance/TelemetryTest.java)):
:

```java
public class TelemetryTest {
    private static final int ITERATIONS = 10;
    private static final String START = "START";
    private static final String ONE = "ONE";
    private static final String TWO = "TWO";
    private static final String THREE = "THREE";

    private boolean printout = false;

    public static void main(final String[] args) {
        final TelemetryTest tt = new TelemetryTest();
        tt.printout = true;
        tt.shouldReturnValidResults();
    }

    void process() {
        Telemetry.section(START);

        stepOne();
        Telemetry.section(ONE);

        stepTwo();
        Telemetry.section(TWO);

        stepThree();
        Telemetry.section(THREE);
    }

    void stepOne() {
        worksForMills(20);
    }

    void stepTwo() {
        worksForMills(10);
    }

    void stepThree() {
        worksForMills(100);
    }

    void worksForMills(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    @Test
    public void shouldReturnValidResults() {
        Telemetry.init();
        for (int i=0; i<ITERATIONS; i++) {
            Telemetry.startIteration();
            process();
        }
        if (printout) {
            Telemetry.print();
        }
        Telemetry.use(AssertPerformance.withTolerance(5)
                .assertPercentageFor(START).sameAs(0)
                .assertPercentageFor(ONE).sameAs(20)
                .assertPercentageFor(TWO).sameAs(10)
                .assertPercentageFor(THREE).sameAs(100));
    }
}
```

[Back to index](documentation_index.md)