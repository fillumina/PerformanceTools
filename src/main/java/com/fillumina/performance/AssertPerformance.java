package com.fillumina.performance;

/**
 * Allow to easily build performance tests.
 *
 * <p>
 * <b>WARNING:</b><br />
 * Performance tests are subject to many factors that may
 * hinder their accuracy, i.e.: system load, CPUs heat level,
 * JDK version and brand etc.
 * So if a test fails randomly try to increase the iterations,
 * relax the tolerance and close background processes.
 *
 * @author fra
 */
public class AssertPerformance implements Presenter {
    private static final long serialVersionUID = 1L;

    private LoopPerformances loopPerformances;
    private double tolerancePercentage = 5D;
    private String message;

    public AssertPerformance setTolerance(final double tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public AssertPerformance assertPercentageEquals(final String name,
            final double percentage) {
        assertPercentageLessThan(name, percentage);
        assertPercentageGreaterThan(name, percentage);
        return this;
    }

    public AssertPerformance assertPercentageLessThan(final String name,
            final double percentage) {
        final double actualPercentage = loopPerformances.get(name).getPercentage();

        if (actualPercentage > percentage + tolerancePercentage) {
            throw new AssertionError(getMessage() +
                    name + " percentage mismatch," +
                    " expected: " + formatPercentage(percentage) +
                    ", found : " + formatPercentage(actualPercentage));
        }

        return this;
    }

    public AssertPerformance assertPercentageGreaterThan(final String name,
            final double percentage) {
        final double actualPercentage = loopPerformances.get(name).getPercentage();

        if (actualPercentage > percentage + tolerancePercentage) {
            throw new AssertionError(getMessage() +
                    name + " percentage mismatch," +
                    " expected: " + formatPercentage(percentage) +
                    ", found : " + formatPercentage(actualPercentage));
        }

        return this;
    }

    @Override
    public AssertPerformance setMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public AssertPerformance setPerformances(final LoopPerformances performances) {
        this.loopPerformances = performances;
        return this;
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String getMessage() {
        if (message == null) {
            return "";
        }
        return message + ": ";
    }

    private String formatPercentage(final double percentage) {
        return String.format("%.2f %%", percentage);
    }

}
