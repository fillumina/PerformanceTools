package com.fillumina.performance.template;

/**
 * Template with some simple viewers wired in.
 *
 * @author Francesco Illuminati
 */
public abstract class PerformanceTemplate {


    public PerformanceTemplate() {
    }

    /**
     * Prints everything out. Can be verbose.
     */
    public void executeWithFullOutput() {
        execute(Verbosity.FULL_OUTPUT);
    }

    /**
     * Prints out statistics but not intermediate samples.
     * In parameterized and sequence parameterized templates
     */
    public void executeWithMediumOutput() {
        execute(Verbosity.MEDIUM_OUTPUT);
    }

    /**
     * Prints out only the final results.
     */
    public void executeReportingOnlyResults() {
        execute(Verbosity.OUTPUT_ONLY_RESULTS);
    }

    /**
     * Executes the test without any output. Failing assertions are
     * reported collectively as one {@link AssertionError}.
     */
    public void executeWithoutOutput() {
        execute(Verbosity.NO_OUTPUT);
    }

    /**
     * Configures the test. Please note that {@code TestConfiguration}
     * has some sensible defaults.
     * <pre>
     * config.setBaseIterations(1_000)
     *       .setMaxStandardDeviation(5);
     * </pre>
     */
    public abstract void config(final MixedConfigurationBuilder<?> config);

    /** Override to set up a different defaults. */
    protected void initConfiguration(MixedConfigurationBuilder<?> configuration) {}

    /**
     * <pre>
     * tests.addTest("test", new Testable() {
     *       public Object test() {
     *           // test code...
     *           return result; // use as blackhole to avoid code eviction
     *       }
     * });
     * </pre>
     */
    public abstract void addTests(final TestConfiguration<?> tests);

    /**
     * Adds assertions.
     *
     * @param assertions
     */
    public abstract void addAssertions(MixedAssertion<?> assertions);

    private void execute(Verbosity verbosity) {
        MixedConfigurationBuilder<PerformanceTemplate> configBuilder =
                new MixedConfigurationBuilder<>();

        initConfiguration(configBuilder);
        config(configBuilder);
        addTests(configBuilder.tests());
        addAssertions(configBuilder.assertions());

        MixedConfiguration config = configBuilder.build();

        MixedPerformanceExecutor.INSTANCE.execute(config, verbosity);
    }
}
