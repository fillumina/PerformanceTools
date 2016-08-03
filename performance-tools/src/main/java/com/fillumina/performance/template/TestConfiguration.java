package com.fillumina.performance.template;

import com.fillumina.performance.mem.strgen.MemStatsTableStringGenerator;
import com.fillumina.performance.util.Activable;
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

    public TestConfiguration() {
        speedConfigurator = new SpeedConfiguration(this);
        usedMemConfigurator = new MemConfiguration(this,
                MemStatsTableStringGenerator.USED_INSTANCE, 10);
        allocatedMemConfigurator = new MemConfiguration(this,
                MemStatsTableStringGenerator.ALLOCATED_INSTANCE, 50);
    }

    public TestConfiguration setName(final String value) {
        this.testName = value;
        return this;
    }

    public TestConfiguration setOutput(Appendable appendable) {
        this.appendable = appendable;
        return this;
    }

    public SpeedConfiguration speedTest() {
        speedConfigurator.setActive(true);
        return speedConfigurator;
    }

    public MemConfiguration usedMemTest() {
        usedMemConfigurator.setActive(true);
        return usedMemConfigurator;
    }

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
