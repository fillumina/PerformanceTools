package com.fillumina.performance.template;

import com.fillumina.performance.assertion.AbstractAssertionError;
import com.fillumina.performance.infrastructure.TN;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.CallBackBuilder;
import com.fillumina.performance.util.Platform;
import com.fillumina.performance.util.TName;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class Configuration<C>
        extends CallBackBuilder<C, Configuration<C>> {

    private final TestConfiguration<Configuration<C>> testConfigurator;
    private final SpeedConfiguration<Configuration<C>> speedConfigurator;
    private final MemConfiguration<Configuration<C>> usedMemConfigurator;
    private final MemConfiguration<Configuration<C>> allocatedMemConfigurator;

    private TName testName = TN.EMPTY;
    private Appendable appendable = System.out;
    private String errorAudioFilename;
    private String successAudioFilename;
    private boolean defaultAudioAlert;
    private TestListener testListener;

    public Configuration() {
        testConfigurator = new TestConfiguration<>(this);
        speedConfigurator = new SpeedConfiguration<>(this);
        usedMemConfigurator = new MemConfiguration<>(this);
        usedMemConfigurator.setStringGenerator(
                MemStatsTableStringGenerator.USED_INSTANCE);
        allocatedMemConfigurator = new MemConfiguration<>(this);
        allocatedMemConfigurator.setStringGenerator(
                MemStatsTableStringGenerator.ALLOCATED_INSTANCE);
    }

    /** Sets the test name. */
    public Configuration<C> setName(final String name) {
        this.testName = TN.tname(name);
        return this;
    }

    /** Sets the test name. */
    public Configuration<C> setName(final TName name) {
        this.testName = name;
        return this;
    }

    /**
     * The output of the test will be appended to the given
     * {@link Appendable}.
     *
     * @see System#out
     */
    public Configuration<C> setOutput(Appendable appendable) {
        this.appendable = appendable;
        return this;
    }

    public SpeedConfiguration<Configuration<C>> speedTestOnly() {
        usedMemConfigurator.setActive(false);
        allocatedMemConfigurator.setActive(false);
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    public MemConfiguration<Configuration<C>> usedMemTestOnly() {
        usedMemConfigurator.setActive(true);
        allocatedMemConfigurator.setActive(false);
        speedConfigurator.setActive(false);
        return usedMemConfigurator;
    }

    public MemConfiguration<Configuration<C>> allocatedMemTestOnly() {
        usedMemConfigurator.setActive(false);
        allocatedMemConfigurator.setActive(true);
        speedConfigurator.setActive(false);
        return allocatedMemConfigurator;
    }

    public TestConfiguration<Configuration<C>> testConfig() {
        return testConfigurator;
    }

    /** Configures the speed test. */
    public SpeedConfiguration<Configuration<C>> speedTest() {
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    /**
     * Configures the used memory test. Used memory is the total memory
     * heap used by the test including those which is freed afterwards.
     */
    public MemConfiguration<Configuration<C>> usedMemTest() {
        usedMemConfigurator.setActive(true);
        return usedMemConfigurator;
    }

    /**
     * Configures the allocated memory test. Allocated memory is the
     * memory which stays allocated after the test has finished.
     */
    public MemConfiguration<Configuration<C>> allocatedMemTest() {
        allocatedMemConfigurator.setActive(true);
        return allocatedMemConfigurator;
    }

    /**
     * A consumer that will receive {@link AbstractAssertionError}s. It might
     * be useful to execute some specific action (i.e. send an alert email).
     *
     * @param value the {@link AbstractAssertionError} thrown.
     */
    public Configuration<C> setTestListener(final TestListener value) {
        this.testListener = value;
        return this;
    }

    TestListener getTestListener() {
        return testListener;
    }

    TName getTestName() {
        return testName;
    }

    Appendable getOutput() {
        return appendable;
    }

    TestConfiguration<Configuration<C>> getTestConfig() {
        return testConfigurator;
    }

    SpeedConfiguration<Configuration<C>> getSpeed() {
        checkIfAllInactive();
        return speedConfigurator;
    }

    MemConfiguration<Configuration<C>> getUsedMem() {
        checkIfAllInactive();
        return usedMemConfigurator;
    }

    MemConfiguration<Configuration<C>> getAllocatedMem() {
        checkIfAllInactive();
        return allocatedMemConfigurator;
    }

    public Configuration<C> setErrorAudioFilename(final String value) {
        this.errorAudioFilename = value;
        return this;
    }

    public Configuration<C> setSuccessAudioFilename(final String value) {
        this.successAudioFilename = value;
        return this;
    }

    public Configuration<C> setDefaultAlert(boolean defaultAudioAlert) {
        this.defaultAudioAlert = defaultAudioAlert;
        return this;
    }

    public Configuration<C> useDefaultAlert() {
        this.defaultAudioAlert = true;
        return this;
    }

    String getErrorAudioFilename() {
        return errorAudioFilename;
    }

    String getSuccessAudioFilename() {
        return successAudioFilename;
    }

    boolean isDefaultAudio() {
        return defaultAudioAlert;
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

    @Override
    public Configuration<C> build() {
        return this;
    }
}
