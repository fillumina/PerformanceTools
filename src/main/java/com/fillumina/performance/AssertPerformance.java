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
public class AssertPerformance implements PerformanceConsumer {
    private static final long serialVersionUID = 1L;

    private LoopPerformances loopPerformances;
    private float tolerancePercentage = 5F;
    private String message;

    public AssertPerformance setTolerancePercentage(final float tolerancePercentage) {
        this.tolerancePercentage = tolerancePercentage;
        return this;
    }

    public class AssertPercentage {
        final AssertPerformance assertPerformance;
        final String name;
        final float actualPercentage;

        public AssertPercentage(final AssertPerformance assertPerformance,
                final String name,
                final float actualPercentage) {
            this.assertPerformance = assertPerformance;
            this.name = name;
            this.actualPercentage = actualPercentage;
        }

        public AssertPerformance equals(
                final float percentage) {
            lessThan(percentage);
            greaterThan(percentage);
            return assertPerformance;
        }

        public AssertPerformance lessThan(
                final float percentage) {
            if (actualPercentage > percentage + tolerancePercentage) {
                throw new AssertionError(getMessage() +
                        "'" + name + "' percentage mismatch," +
                        " expected: " + formatPercentage(percentage) +
                        ", found : " + formatPercentage(actualPercentage));
            }

            return assertPerformance;
        }

        public AssertPerformance greaterThan(
                final float percentage) {
            if (actualPercentage > percentage + tolerancePercentage) {
                throw new AssertionError(getMessage() +
                        "'" + name + "' percentage mismatch," +
                        " expected: " + formatPercentage(percentage) +
                        ", found : " + formatPercentage(actualPercentage));
            }

            return assertPerformance;
        }
    }

    public class AssertTest {
        final AssertPerformance assertPerformance;
        final String name;
        final float actualPercentage;

        public AssertTest(final AssertPerformance assertPerformance,
                final String name,
                final float actualPercentage) {
            this.assertPerformance = assertPerformance;
            this.name = name;
            this.actualPercentage = actualPercentage;
        }

        public AssertPerformance equals(final String name) {
            slowerThan(name);
            fasterThan(name);
            return assertPerformance;
        }

        public AssertPerformance slowerThan(
                final String other) {
            final float percentage = getPercentageFor(other);
            if (actualPercentage < percentage - tolerancePercentage) {
                throw new AssertionError(getMessage() +
                        "'" + name + "' (" + formatPercentage(percentage) +
                        ") faster than '" + other +
                        "' (" + formatPercentage(actualPercentage) + ")");
            }

            return assertPerformance;
        }

        public AssertPerformance fasterThan(
                final String other) {
            final float percentage = getPercentageFor(other);
            if (actualPercentage > percentage + tolerancePercentage) {
                throw new AssertionError(getMessage() +
                        "'" + name + "' (" + formatPercentage(percentage) +
                        ") slower than '" + other +
                        "' (" + formatPercentage(actualPercentage) + ")");
            }

            return assertPerformance;
        }

        private float getPercentageFor(final String name) {
            return loopPerformances.get(name).getPercentage();
        }

    }

    public AssertPercentage assertPercentageFor(final String name) {
        final float actualPercentage = loopPerformances.get(name).getPercentage();
        return new AssertPercentage(this, name, actualPercentage);
    }

    public AssertTest assertTest(final String name) {
        final float actualPercentage = loopPerformances.get(name).getPercentage();
        return new AssertTest(this, name, actualPercentage);
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
    public void consume() {
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
