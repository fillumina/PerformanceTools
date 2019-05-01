package com.fillumina.performance.template;

import com.fillumina.performance.executor.PN;
import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.mem.MemStatsType;
import com.fillumina.performance.mem.sample.AllocatedMemSampleProducer;
import com.fillumina.performance.mem.sample.UsedMemSampleProducer;
import com.fillumina.performance.template.MixedConfigurationBuilder.Configuration;
import com.fillumina.performance.time.TimeStatsType;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.Platform;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.pathname.PathName;
import java.util.Arrays;
import java.util.List;

/**
 * Configures tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class MixedConfigurationBuilder<C>
        extends CallBackBuilder<C, MixedConfigurationBuilder<C>.Configuration> {

    private TestConfiguration<?> testConfigurator;

    private final SpeedConfiguration<MixedConfigurationBuilder<C>> speedConfigurator;
    private final MemConfiguration<MixedConfigurationBuilder<C>> usedMemConfigurator;
    private final MemConfiguration<MixedConfigurationBuilder<C>> allocatedMemConfigurator;
    private final MixedAssertionableResult.Builder mixedAssertionableResultBuilder;

    private PathName testName = PN.EMPTY;
    private String failureAudioFilename;
    private String successAudioFilename;
    private boolean alertActive;
    private boolean throwExceptionIfFailingAssertion;

    public MixedConfigurationBuilder() {
        this((Setter<C,Configuration>)null);
    }

    public MixedConfigurationBuilder(C caller) {
        this((builtObject) -> { return caller; });
    }

    public MixedConfigurationBuilder(Setter<C, Configuration> setter) {
        super(setter);

        speedConfigurator = new SpeedConfiguration<>(this);

        usedMemConfigurator = new MemConfiguration<>(this,
                new UsedMemSampleProducer(), "used",
                MemStatsType.USED);

        allocatedMemConfigurator = new MemConfiguration<>(this,
                new AllocatedMemSampleProducer(), "allocated",
                MemStatsType.ALLOCATED);

        // sets the test order
        mixedAssertionableResultBuilder = MixedAssertionableResult.builder(
                TimeStatsType.AVERAGE,
                TimeStatsType.THROUGHPUT,
                MemStatsType.USED,
                MemStatsType.ALLOCATED
            );
    }

    @Override
    public Configuration build() {
        return new Configuration();
    }

    /** Sets the test name. */
    public MixedConfigurationBuilder<C> setName(final String name) {
        this.testName = PN.pname(name);
        return this;
    }

    /** Sets the test name. */
    public MixedConfigurationBuilder<C> setName(final PathName name) {
        this.testName = name;
        return this;
    }

    /**
     * Configures the assertions.
     * Remember to <b>always end the builder with the
     * {@link com.fillumina.performance.util.CallBackBuilder#end() } method</b>.
     * @return
     */
    public MixedAssertionBuilder<MixedConfigurationBuilder<C>> assertions() {
        return new MixedAssertionBuilder<>(mixedAssertionableResultBuilder, this);
    }

    /**
     * Configure the tests with eventual parameters and sequences.
     * Remember to <b>always end the builder with the
     * {@link com.fillumina.performance.util.CallBackBuilder#end() } method</b>.
     */
    @SuppressWarnings("unchecked")
    public TestConfiguration<MixedConfigurationBuilder<C>> tests() {
        testConfigurator = new TestConfiguration<>(this);
        return (TestConfiguration<MixedConfigurationBuilder<C>>) testConfigurator;
    }

    @SuppressWarnings("unchecked")
    public <K> TestConfiguration<K> tests(K callback) {
        testConfigurator = new TestConfiguration<>(callback);
        return (TestConfiguration<K>) testConfigurator;
    }

    /**
     * Configures the speedConfig test.
     * Remember to <b>always end the builder with the
     * {@link com.fillumina.performance.util.CallBackBuilder#end() } method</b>.
     */
    public SpeedConfiguration<MixedConfigurationBuilder<C>> speedConfig() {
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    /**
     * Configures the used memory test. Used memory is the total memory
     * heap used by the test including those which is freed afterwards.
     * Remember to <b>always end the builder with the
     * {@link com.fillumina.performance.util.CallBackBuilder#end() } method</b>.
     */
    public MemConfiguration<MixedConfigurationBuilder<C>> usedMemConfig() {
        usedMemConfigurator.setActive(true);
        return usedMemConfigurator;
    }

    /**
     * Configures the allocated memory test. Allocated memory is the
     * memory which stays allocated after the test has finished.
     * Remember to <b>always end the builder with the
     * {@link com.fillumina.performance.util.CallBackBuilder#end() } method</b>.
     */
    public MemConfiguration<MixedConfigurationBuilder<C>> allocatedMemConfig() {
        allocatedMemConfigurator.setActive(true);
        return allocatedMemConfigurator;
    }

    public MixedConfigurationBuilder<C> setFailureAudioFilename(final String value) {
        this.failureAudioFilename = value;
        return this;
    }

    public MixedConfigurationBuilder<C> setSuccessAudioFilename(String value) {
        this.successAudioFilename = value;
        return this;
    }

    public MixedConfigurationBuilder<C> setAlertActive(boolean defaultAudioAlert) {
        this.alertActive = defaultAudioAlert;
        return this;
    }

    public MixedConfigurationBuilder<C> setThrowExceptionIfFailingAssertion(
            boolean value) {
        this.throwExceptionIfFailingAssertion = value;
        return this;
    }

    /** If all tests are inactive then activate them all. */
    private void checkIfAllInactive() {
        if (!speedConfigurator.isActive() &&
                !usedMemConfigurator.isActive() &&
                !allocatedMemConfigurator.isActive()) {
            speedConfigurator.setActive(true);
            usedMemConfigurator.setActive(true);
            allocatedMemConfigurator.setActive(true);
        }
    }

    @Override
    public String toString() {
        checkIfAllInactive();
        StringBuilder buf = new StringBuilder();
        buf.append(Platform.INSTANCE.toString()).append(System.lineSeparator());
        appendObject(buf, "Experiment Plan",
                testConfigurator.toString(testName.toString()));
        appendActivable(buf, "Speed", speedConfigurator);
        appendActivable(buf, "Used Memory", usedMemConfigurator);
        appendActivable(buf, "Allocated Memory", allocatedMemConfigurator);
        return buf.toString();
    }

    private void appendActivable(StringBuilder buf,
            String title,
            Activable activable) {
        if (activable.isActive()) {
            appendObject(buf, title, activable);
        }
    }

    private void appendObject(StringBuilder buf,
            String title,
            Object obj) {
        buf
                .append(System.lineSeparator())
                .append(TableFormatter.title(title, '-'))
                .append(obj.toString())
                .append(System.lineSeparator());
    }

    public class Configuration
            implements PerformanceBuilder.Configuration {

        private Appendable appendable;
        private Verbosity verbosity;

        private final MixedProducerConfiguration[] prodConfs =
                new MixedProducerConfiguration[] {
                        speedConfigurator.build(),
                        usedMemConfigurator.build(),
                        allocatedMemConfigurator.build()
                };

        @Override
        public void setConsole(Appendable appendable, Verbosity verbosity) {
            this.verbosity = verbosity;
            this.appendable = appendable;
            for (MixedProducerConfiguration c : prodConfs) {
                c.setVerbosity(verbosity);
            }
        }

        @Override
        public PerformanceBuilderListener getPerformanceBuilderListener() {
            return new ConsolePerformanceBuilderListener(appendable, verbosity,
                            this, throwExceptionIfFailingAssertion);
        }

        @Override
        public List<ProducerConfiguration> getProducers() {
            return Arrays.asList((ProducerConfiguration[])prodConfs);
        }

        @Override
        public MixedAssertionableResult.Builder
                getMixedAssertionableResultBuilder() {
            for (MixedProducerConfiguration pc : prodConfs) {
                pc.getStringGenerators().forEach((type, generator) ->
                    mixedAssertionableResultBuilder
                            .getStatsBuilder(type)
                            .setStringGenerator(generator));
            }
            return mixedAssertionableResultBuilder;
        }

        @Override
        public TestConfiguration<?> getTestConfig() {
            return testConfigurator;
        }

        @Override
        public String getFailureAudioFilename() {
            return failureAudioFilename;
        }

        @Override
        public String getSuccessAudioFilename() {
            return successAudioFilename;
        }

        @Override
        public boolean isAlertActive() {
            return alertActive;
        }

        @Override
        public String toString() {
            return MixedConfigurationBuilder.this.toString();
        }
    }
}
