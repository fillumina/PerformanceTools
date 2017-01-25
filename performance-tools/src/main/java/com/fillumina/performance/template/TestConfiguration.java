package com.fillumina.performance.template;

import com.fillumina.performance.mem.MemAnalyzer;
import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.Activable;
import com.fillumina.performance.util.Platform;
import com.fillumina.performance.util.formatter.TableFormatter;

/**
 * Configures the tests using a <i>fluent interface</i>.
 *
 * @author Francesco Illuminati
 */
public class TestConfiguration {

    private String testName;
    private Appendable appendable = System.out;
    private final SpeedConfiguration speedConfigurator;
    private final MemConfiguration usedMemConfigurator;
    private final MemConfiguration allocatedMemConfigurator;
    private String errorAudioFilename;
    private String successAudioFilename;
    private boolean defaultAudio;

    public TestConfiguration() {
        speedConfigurator = new SpeedConfiguration(this);
        usedMemConfigurator = new MemConfiguration(this,
                MemStatsTableStringGenerator.USED_INSTANCE,
                MemAnalyzer.DEFAULT_SAMPLES);
        allocatedMemConfigurator = new MemConfiguration(this,
                MemStatsTableStringGenerator.ALLOCATED_INSTANCE,
                MemAnalyzer.DEFAULT_SAMPLES);
    }

    /** Sets the test name. */
    public TestConfiguration setName(final String value) {
        this.testName = value;
        return this;
    }

    /**
     * The output of the test will be appended to the given
     * {@link Appendable}.
     *
     * @see System#out
     */
    public TestConfiguration setOutput(Appendable appendable) {
        this.appendable = appendable;
        return this;
    }

    public SpeedConfiguration onlySpeedTest() {
        usedMemConfigurator.setActive(false);
        allocatedMemConfigurator.setActive(false);
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    public MemConfiguration onlyUsedMemTest() {
        usedMemConfigurator.setActive(true);
        allocatedMemConfigurator.setActive(false);
        speedConfigurator.setActive(false);
        return usedMemConfigurator;
    }

    public MemConfiguration onlyAllocatedMemTest() {
        usedMemConfigurator.setActive(false);
        allocatedMemConfigurator.setActive(true);
        speedConfigurator.setActive(false);
        return allocatedMemConfigurator;
    }

    /** Configures the speed test. */
    public SpeedConfiguration speedTest() {
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    /**
     * Configures the used memory test. Used memory is the total memory
     * heap used by the test including those which is freed afterwards.
     */
    public MemConfiguration usedMemTest() {
        usedMemConfigurator.setActive(true);
        return usedMemConfigurator;
    }

    /**
     * Configures the allocated memory test. Allocated memory is the
     * memory which stays allocated after the test has finished.
     */
    public MemConfiguration allocatedMemTest() {
        allocatedMemConfigurator.setActive(true);
        return allocatedMemConfigurator;
    }

    String getTestName() {
        return testName;
    }

    Appendable getOutput() {
        return appendable;
    }

    SpeedConfiguration getSpeed() {
        checkIfAllInactive();
        return speedConfigurator;
    }

    MemConfiguration getUsedMem() {
        checkIfAllInactive();
        return usedMemConfigurator;
    }

    MemConfiguration getAllocatedMem() {
        checkIfAllInactive();
        return allocatedMemConfigurator;
    }

    public TestConfiguration setErrorAudioFilename(final String value) {
        this.errorAudioFilename = value;
        return this;
    }

    public TestConfiguration setSuccessAudioFilename(final String value) {
        this.successAudioFilename = value;
        return this;
    }

    public TestConfiguration useDefaultAlert() {
        this.defaultAudio = true;
        return this;
    }

    String getErrorAudioFilename() {
        return errorAudioFilename;
    }

    String getSuccessAudioFilename() {
        return successAudioFilename;
    }

    boolean isDefaultAudio() {
        return defaultAudio;
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
        if (testName != null) {
            buf.append(TableFormatter.title("CONFIGURATION OF " + testName, '='));
        } else {
            buf.append(TableFormatter.title("CONFIGURATION", '='));
        }
        buf.append(Platform.INSTANCE.toString()).append(System.lineSeparator());
        append(buf, "Speed", speedConfigurator);
        append(buf, "Used Memory", usedMemConfigurator);
        append(buf, "Allocated Memory", allocatedMemConfigurator);
        return buf.toString();
    }

    private void append(StringBuilder buf,
            String title,
            Activable activable) {
        if (activable.isActive()) {
            buf
                .append(System.lineSeparator())
                .append(TableFormatter.title(title, '-'))
                .append(activable.toString())
                .append(System.lineSeparator());
        }
    }
}
