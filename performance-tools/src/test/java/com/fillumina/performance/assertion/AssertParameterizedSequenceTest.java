package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParameterizedSequenceTest {

    @Test
    public void shouldCreate() {
        AssertParameterizedSequence<?,AssertableMock> aps =
                AssertParameterizedSequence.create();

        assertNotNull(aps);
    }

    private static class MyClass {}

    @Test
    public void shouldReturnToCaller() {
        AssertParameterizedSequence<MyClass,AssertableMock> aps =
                new AssertParameterizedSequence<>(new MyClass());

        assertTrue(aps.endSequences() instanceof MyClass);
    }

    @Test
    public void shouldAddAssertion() {
        AssertParameterizedSequence<?,AssertableMock> aps =
                AssertParameterizedSequence.create();

        AssertionMock<PHolder<AssertableMock>> assertion = new AssertionMock<>();

        aps.addAssertion(assertion);

        PHolder<PHolder<PHolder<AssertableMock>>> assertable =
            PHolder.build("root")
                .branch("sequence")
                    .branch("parameter")
                        .leaf("alpha", new AssertableMock("alpha_mock"))
                .<PHolder<PHolder<AssertableMock>>>getRoot();

        aps.check(assertable);

        assertEquals("alpha_mock", assertion.getList().get(0));
    }

    @Test
    public void shouldAddAssertionForAllSequences() {
        AssertParameterizedSequence<?,AssertableMock> aps =
                AssertParameterizedSequence.create();

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        aps.forAllSequences()
                .forAllTests()
                    .addAssertion(assertion);

        PHolder<PHolder<PHolder<AssertableMock>>> assertable =
            PHolder.build("root")
                .branch("sequence")
                    .branch("parameter")
                        .leaf("alpha", new AssertableMock("alpha_mock"))
                .<PHolder<PHolder<AssertableMock>>>getRoot();

        aps.check(assertable);

        assertEquals("alpha_mock", assertion.getList().get(0));
    }

    @Test
    public void shouldAddAssertionForSequenceValue() {
        AssertParameterizedSequence<?,AssertableMock> aps =
                AssertParameterizedSequence.create();

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        aps.forSequenceValue("sequence_B")
                .forAllTests()
                    .addAssertion(assertion);

        PHolder<PHolder<PHolder<AssertableMock>>> assertable =
            PHolder.build("root")
                .branch("sequence_A")
                    .branch("parameter")
                        .leaf("alpha", new AssertableMock("alpha_mock"))
                    .end()
                .end()
                .branch("sequence_B")
                    .branch("parameter")
                        .leaf("beta", new AssertableMock("beta_mock"))
                .<PHolder<PHolder<AssertableMock>>>getRoot();

        aps.check(assertable);

        assertEquals(1, assertion.getList().size());
        assertEquals("beta_mock", assertion.getList().get(0));
    }

}
