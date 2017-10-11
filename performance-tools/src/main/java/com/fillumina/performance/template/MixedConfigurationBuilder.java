package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AbstractAssertionError;
import com.fillumina.performance.executor.TN;
import com.fillumina.performance.executor.generator.PerformanceGenerator;
import com.fillumina.performance.executor.generator.ProducerConfiguration;
import com.fillumina.performance.executor.generator.ProducerConfigurationImpl;
import com.fillumina.performance.executor.generator.TestConfiguration;
import com.fillumina.performance.mem.stats.MemStatsTableStringGenerator;
import com.fillumina.performance.template.MixedConfigurationBuilder.Configuration;
import com.fillumina.performance.time.sample.PerformanceTimerFactory;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.Platform;
import com.fillumina.performance.util.formatter.TableFormatter;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.List;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class MixedConfigurationBuilder<C>
        extends CallBackBuilder<C, MixedConfigurationBuilder<C>.Configuration> {

    private final TestConfiguration<MixedConfigurationBuilder<C>> testConfigurator;

    private final ProducerConfigurationImpl<MixedConfigurationBuilder<C>> speedConfigurator;
    private final MemConfiguration<MixedConfigurationBuilder<C>> usedMemConfigurator;
    private final MemConfiguration<MixedConfigurationBuilder<C>> allocatedMemConfigurator;
    private final MixedAssertionableResult.Builder mixedAssertionableResultBuilder;

    private TName testName = TN.EMPTY;
    private Appendable appendable = System.out;
    private String errorAudioFilename;
    private String successAudioFilename;
    private boolean alertActive;
    //private TestListener testListener;
    private boolean throwExceptionIfFailingAssertion;

    public MixedConfigurationBuilder() {
        this((Setter<C,Configuration>)null);
    }

    public MixedConfigurationBuilder(C caller) {
        this((builtObject) -> { return caller; });
    }

    public MixedConfigurationBuilder(Setter<C, Configuration> setter) {
        super(setter);
        testConfigurator = new TestConfiguration<>(this);

        speedConfigurator = new ProducerConfigurationImpl<>(
                conf -> PerformanceTimerFactory.createPerformanceTimer(conf),
                this);

        usedMemConfigurator = new MemConfiguration<>(this);
        usedMemConfigurator.setStringGenerator(
                MemStatsTableStringGenerator.USED_INSTANCE);

        allocatedMemConfigurator = new MemConfiguration<>(this);
        allocatedMemConfigurator.setStringGenerator(
                MemStatsTableStringGenerator.ALLOCATED_INSTANCE);

        mixedAssertionableResultBuilder = MixedAssertionableResult.builder();
    }

    @Override
    public Configuration build() {
        return new Configuration();
    }

    /** Sets the test name. */
    public MixedConfigurationBuilder<C> setName(final String name) {
        this.testName = TN.tname(name);
        return this;
    }

    /** Sets the test name. */
    public MixedConfigurationBuilder<C> setName(final TName name) {
        this.testName = name;
        return this;
    }

    /**
     * The output of the test will be appended to the given
     * {@link Appendable}.
     *
     * @see System#out
     */
    public MixedConfigurationBuilder<C> setOutput(Appendable appendable) {
        this.appendable = appendable;
        return this;
    }

    public MixedAssertionBuilder<MixedConfigurationBuilder<C>> assertions() {
        return new MixedAssertionBuilder<>(mixedAssertionableResultBuilder, this);
    }

    public TestConfiguration<MixedConfigurationBuilder<C>> tests() {
        return testConfigurator;
    }

    public MixedConfigurationBuilder<C> speed() {
        speedConfigurator.setActive(true);
        return this;
    }

    public MixedConfigurationBuilder<C> usedMem() {
        usedMemConfigurator.setActive(true);
        return this;
    }

    public MixedConfigurationBuilder<C> allocatedMem() {
        allocatedMemConfigurator.setActive(true);
        return this;
    }

    /** Configures the speedConfig test. */
    public ProducerConfigurationImpl<MixedConfigurationBuilder<C>> speedConfig() {
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    /**
     * Configures the used memory test. Used memory is the total memory
     * heap used by the test including those which is freed afterwards.
     */
    public MemConfiguration<MixedConfigurationBuilder<C>> usedMemConfig() {
        usedMemConfigurator.setActive(true);
        return usedMemConfigurator;
    }

    /**
     * Configures the allocated memory test. Allocated memory is the
     * memory which stays allocated after the test has finished.
     */
    public MemConfiguration<MixedConfigurationBuilder<C>> allocatedMemConfig() {
        allocatedMemConfigurator.setActive(true);
        return allocatedMemConfigurator;
    }

    /**
     * A consumer that will receive {@link AbstractAssertionError}s. It might
     * be useful to execute some specific action (i.e. send an alert email).
     *
     * @param value the {@link AbstractAssertionError} thrown.
     */
//    public MixedConfigurationBuilder<C> setTestListener(final TestListener value) {
//        this.testListener = value;
//        return this;
//    }

    public MixedConfigurationBuilder<C> setFailureAudioFilename(final String value) {
        this.errorAudioFilename = value;
        return this;
    }

    public MixedConfigurationBuilder<C> setSuccessAudioFilename(final String value) {
        this.successAudioFilename = value;
        return this;
    }

    public MixedConfigurationBuilder<C> setAlertActive(boolean defaultAudioAlert) {
        this.alertActive = defaultAudioAlert;
        return this;
    }

    public MixedConfigurationBuilder<C> setThrowExceptionIfFailingAssertion(
            boolean throwExceptionIfFailingAssertion) {
        this.throwExceptionIfFailingAssertion = throwExceptionIfFailingAssertion;
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
        appendObject(buf, "Test", testConfigurator.toString(testName.toString()));
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

    public class Configuration implements PerformanceGenerator.Configuration {
        private List<ProducerConfiguration> producers;

        public Configuration() {
            producers = new ArrayList<>();

        }

        public MixedAssertionableResult.Builder
                getMixedAssertionableResultBuilder() {
                    return mixedAssertionableResultBuilder;
        }

        @Override
        public TestConfiguration<?> getTestConfig() {
            return testConfigurator;
        }

        @Override
        public String toString() {
            return MixedConfigurationBuilder.this.toString();
        }

        @Override
        public List<ProducerConfiguration> getProducers() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
