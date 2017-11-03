package com.fillumina.performance.executor;

import com.fillumina.performance.assertion.Assertable;
import com.fillumina.performance.assertion.AssertableMock;
import com.fillumina.performance.util.tname.TName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class MixedAssertableHolderTest {

    private static class AssertableMock_1 extends AssertableMock {}
    private static class AssertableMock_2 extends AssertableMock {}
    private static class AssertableMock_3 extends AssertableMock {}

    @Test
    public void shouldEMPTYstaticFieldShouldBeEmpty() {
        MixedAssertableHolder mixedHolder = MixedAssertableHolder.EMPTY;
        assertTrue(mixedHolder.isEmpty());
    }

    @Test
    public void shouldBeEmptyIfNoMapIsSpecified() {
        MixedAssertableHolder mixedHolder = new MixedAssertableHolder();
        assertTrue(mixedHolder.isEmpty());
    }

    @Test
    public void shouldReturnTheGivenHolder() {
        AssertableHolder<AssertableMock> holder =
                AssertableHolder.builder(AssertableMock.class, "root")
                        .test("one", new AssertableMock("1"))
                        .build();

        MixedAssertableHolder mixedHolder = new MixedAssertableHolder(holder);

        assertEquals(holder, mixedHolder.getStats());
    }

    @Test
    public void shouldReturnTheGivenHoldersAccordingToClass() {
        AssertableHolder<AssertableMock> h1 =
                AssertableHolder.builder(AssertableMock.class, "root")
                        .test("one", new AssertableMock())
                        .build();

        AssertableHolder<AssertableMock_1> h2 =
                AssertableHolder.builder(AssertableMock_1.class, "root")
                        .test("one", new AssertableMock_1())
                        .build();

        MixedAssertableHolder mixedHolder = new MixedAssertableHolder(h1, h2);

        assertEquals(h1, mixedHolder.getStats(AssertableMock.class));
        assertEquals(h2, mixedHolder.getStats(AssertableMock_1.class));
    }

    @Test
    public void shouldBuildFromAssertables() {
        AssertableMock a1 = new AssertableMock();
        AssertableMock_1 a2 = new AssertableMock_1();
        MixedAssertableHolder mixedHolder = MixedAssertableHolder.builder()
                .addAssertable(AssertableMock.class,
                        TN.tname("one"), a1)
                .addAssertable(AssertableMock_1.class,
                        TN.tname("two"), a2)
                .build();

        assertEquals(a1,
                mixedHolder.getStats(AssertableMock.class).getAssertable());
        assertEquals(a2,
                mixedHolder.getStats(AssertableMock_1.class).getAssertable());
    }

    @Test
    public void shouldReturnTheRegisteredTypes() {
        AssertableMock a1 = new AssertableMock();
        AssertableMock_1 a2 = new AssertableMock_1();
        MixedAssertableHolder mixedHolder = MixedAssertableHolder.builder()
                .addAssertable(AssertableMock.class,
                        TN.tname("one"), a1)
                .addAssertable(AssertableMock_1.class,
                        TN.tname("two"), a2)
                .build();

        Set<Class<? extends Assertable>> set = mixedHolder.getTypes();
        assertTrue(set.containsAll(Arrays.asList(
                AssertableMock.class, AssertableMock_1.class
        )));
    }

    @Test
    public void shouldReturnTheStatsMap() {
        AssertableMock a1 = new AssertableMock();
        AssertableMock_1 a2 = new AssertableMock_1();
        MixedAssertableHolder mixedHolder = MixedAssertableHolder.builder()
                .addAssertable(AssertableMock.class,
                        TN.tname("one"), a1)
                .addAssertable(AssertableMock_1.class,
                        TN.tname("two"), a2)
                .build();

        Map<Class<? extends Assertable>, AssertableHolder<?>> map =
                mixedHolder.getStatsMap();

        assertTrue(map.keySet().containsAll(Arrays.asList(
                AssertableMock.class, AssertableMock_1.class
        )));

        assertTrue(map.values().stream().map((t) -> {return t.getAssertable();})
                .collect(Collectors.toCollection(ArrayList::new))
                .containsAll(Arrays.asList(a1, a2)) );
    }

    @Test
    public void shouldJoinOneMixedAssertableHolders() {
        AssertableMock_1 a1 = new AssertableMock_1();
        MixedAssertableHolder one = create("one_", a1);

        TName rootName = TN.tname("root");

        MixedAssertableHolder root = MixedAssertableHolder.joiner(rootName)
                .addSubExperiment(one)
                .join();

        assertEquals(a1,
                getAssertable(root, AssertableMock_1.class,
                        createName("one_", 0)));
    }

    @Test
    public void shouldJoinTwoMixedAssertableHolders() {
        AssertableMock_1 a1 = new AssertableMock_1();
        MixedAssertableHolder one = create("one_", a1);
        AssertableMock_2 a2 = new AssertableMock_2();
        MixedAssertableHolder two = create("two_", a2);

        TName rootName = TN.tname("root");

        MixedAssertableHolder root = MixedAssertableHolder.joiner(rootName)
                .addSubExperiment(one)
                .addSubExperiment(two)
                .join();

        assertEquals(a1,
                getAssertable(root, AssertableMock_1.class,
                        createName("one_", 0)));

        assertEquals(a2,
                getAssertable(root, AssertableMock_2.class,
                        createName("two_", 0)));
    }

    @Test
    public void shouldJoinTwoMixedAssertableHoldersWithSameStats() {
        AssertableMock_1 a1 = new AssertableMock_1();
        AssertableMock_2 a2 = new AssertableMock_2();
        MixedAssertableHolder one = create("one_", a1, a2);

        AssertableMock_1 b1 = new AssertableMock_1();
        AssertableMock_2 b2 = new AssertableMock_2();
        AssertableMock_3 b3 = new AssertableMock_3();
        MixedAssertableHolder two = create("two_", b1, b2, b3);

        TName rootName = TN.tname("root");

        MixedAssertableHolder root = MixedAssertableHolder.joiner(rootName)
                .addSubExperiment(one)
                .addSubExperiment(two)
                .join();

        assertEquals(a1,
                getAssertable(root, AssertableMock_1.class,
                        createName("one_", 0)));

        assertEquals(a2,
                getAssertable(root, AssertableMock_2.class,
                        createName("one_", 1)));

        assertEquals(b1,
                getAssertable(root, AssertableMock_1.class,
                        createName("two_", 0)));

        assertEquals(b2,
                getAssertable(root, AssertableMock_2.class,
                        createName("two_", 1)));

        assertEquals(b3,
                getAssertable(root, AssertableMock_3.class,
                        createName("two_", 2)));
    }

    @Test
    public void shouldSetTheGivenRootName() {
        AssertableMock_1 a1 = new AssertableMock_1();
        MixedAssertableHolder one = create("one_", a1);

        TName rootName = TN.tname("root");

        MixedAssertableHolder root = MixedAssertableHolder.joiner(rootName)
                .addSubExperiment(one)
                .join();

        assertEquals(rootName, root.getStats(AssertableMock_1.class).getName());
    }

    private MixedAssertableHolder create(String prefix, Assertable... array) {
        MixedAssertableHolder.Builder builder = MixedAssertableHolder.builder();
        int index = 0;
        for (Assertable a : array) {
            builder.addAssertable(a.getClass(),
                    createName(prefix, index),
                    a);
            index++;
        }
        return builder.build();
    }

    private static TName createName(String prefix, int index) {
        return TN.tname(prefix + Integer.toString(index));
    }

    private Assertable getAssertable(MixedAssertableHolder mixedHolder,
            Class<? extends Assertable> type, TName name) {
        return mixedHolder
                .getStats(type)
                .getTree()
                .getTreeAtPath(name)
                .getValue();
    }
}
