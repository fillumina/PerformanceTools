package com.fillumina.performance.mock;

import com.fillumina.performance.infrastructure.Testable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TestableMock extends Testable {

    public static enum TMethod {
        SET_UP, BEFORE_SAMPLE, TEST, AFTER_SAMPLE, TEAR_DOWN;
    }

    private static class CallLog {
        final int eventNumber;
        final TMethod method;
        final long threadId;

        public CallLog(int eventNumber, TMethod method) {
            this(eventNumber, method, Thread.currentThread().getId());
        }

        public CallLog(int eventNumber, TMethod method, long threadId) {
            this.eventNumber = eventNumber;
            this.method = method;
            this.threadId = threadId;
        }

        public boolean equalsTo(final CallLog other) {
            if (this.threadId != other.threadId) {
                return false;
            }
            if (this.eventNumber != other.eventNumber) {
                return false;
            }
            if (this.method != other.method) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CallLog{" +
                    "eventNumber=" + eventNumber +
                    ", method=" + method +
                    ", threadId=" + threadId + '}';
        }
    }

    private int counter;
    private List<CallLog> log = new ArrayList<>();

    private synchronized void add(TMethod tm) {
        CallLog cl = new CallLog(counter, tm);
        counter++;
        log.add(cl);
    }

    public void assertEvent(int eventNumber, TMethod tm) {
        CallLog searchedEvent = new CallLog(eventNumber, tm);
        if (!isEventExistent(searchedEvent)) {
            throw new AssertionError("event not found: " + searchedEvent);
        }
    }

    public void negateEvent(int eventNumber, TMethod tm) {
        CallLog searchedEvent = new CallLog(eventNumber, tm);
        if (isEventExistent(searchedEvent)) {
            throw new AssertionError("event found: " + searchedEvent);
        }
    }

    public boolean isEventExistent(CallLog searchedEvent) {
        for (CallLog cl : log) {
            if (searchedEvent.equalsTo(cl)) {
                return true;
            }
        }
        return false;
    }

    public void assertCalls(int calls) {
        if (calls != log.size()) {
            throw new AssertionError("expected " + calls + " calls but was " +
                    log.size());
        }
    }

    public void assertMethod(TMethod tm) {
        if (!isTestableMethodBeenCalled(tm)) {
            throw new AssertionError("method not called: " + tm);
        }
    }

    public void negateMethod(TMethod tm) {
        if (isTestableMethodBeenCalled(tm)) {
            throw new AssertionError("method called: " + tm);
        }
    }

    public boolean isTestableMethodBeenCalled(TMethod tm) {
        for (CallLog cl : log) {
            if (cl.method.equals(tm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void test() {
        add(TMethod.TEST);
    }

    @Override
    public void tearDown() {
        add(TMethod.TEAR_DOWN);
    }

    @Override
    public void onAfterSample(int iterations) {
        add(TMethod.AFTER_SAMPLE);
    }

    @Override
    public void onBeforeSample(int iterations) {
        add(TMethod.BEFORE_SAMPLE);
    }

    @Override
    public void setUp() {
        add(TMethod.SET_UP);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder((2 + log.size()) * 30);
        buf.append("TestableMock{").append(System.lineSeparator())
                .append("   ")
                .append("counter=")
                .append(counter)
                .append(System.lineSeparator());
        for (CallLog cl : log) {
            buf.append("   ").append(cl).append(System.lineSeparator());
        }
        buf.append('}');
        return buf.toString();
    }

}
