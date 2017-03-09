package com.fillumina.performance.assertion;

import com.fillumina.performance.infrastructure.PHolder;
import com.fillumina.performance.mock.AssertableMock;
import com.fillumina.performance.mock.AssertionMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class AssertParameterizedTest {

    private static class MyClazz {}

    @Test
    public void shouldReturnCaller() {
        AssertParameterized<MyClazz,AssertableMock> assertParameterized =
                new AssertParameterized<>(new MyClazz());

        assertTrue(assertParameterized.endTests() instanceof MyClazz);
    }

    @Test
    public void shouldCheckANamedAssertion() {
        AssertParameterized<MyClazz,AssertableMock> assertParameterized =
                new AssertParameterized<>();

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        assertParameterized.forTest("alpha").addAssertion(assertion);

        AssertableMock assertable = AssertableMock.create("title");

        PHolder<PHolder<AssertableMock>> root =
                PHolder.build("root")
                    .leaf("alpha", assertable)
                        .<PHolder<AssertableMock>>getRoot();

        assertParameterized.check(root);

        assertEquals("title", assertion.getList().get(0));
    }

    @Test
    public void shouldCheckRegexpAssertions() {
        AssertParameterized<MyClazz,AssertableMock> assertParameterized =
                new AssertParameterized<>();

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        assertParameterized.forRegexpTest("a.*")
                .addAssertion(assertion);

        AssertableMock alphaAssertable = AssertableMock.create("alpha_title");
        AssertableMock betaAssertable = AssertableMock.create("beta_title");

        PHolder<PHolder<AssertableMock>> root =
                PHolder.build("root")
                    .leaf("alpha", alphaAssertable)
                    .leaf("beta", betaAssertable)
                        .<PHolder<AssertableMock>>getRoot();

        assertParameterized.check(root);

        assertEquals("alpha_title", assertion.getList().get(0));
    }

    @Test
    public void shouldCheckAllTestAssertions() {
        AssertParameterized<MyClazz,AssertableMock> assertParameterized =
                new AssertParameterized<>();

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        assertParameterized.forAllTests()
                .addAssertion(assertion);

        AssertableMock alphaAssertable = AssertableMock.create("alpha_title");
        AssertableMock betaAssertable = AssertableMock.create("beta_title");

        PHolder<PHolder<AssertableMock>> root =
                PHolder.build("root")
                    .leaf("alpha", alphaAssertable)
                    .leaf("beta", betaAssertable)
                        .<PHolder<AssertableMock>>getRoot();

        assertParameterized.check(root);

        assertEquals(2, assertion.getList().size());
        assertEquals("alpha_title", assertion.getList().get(0));
        assertEquals("beta_title", assertion.getList().get(1));
    }

    @Test
    public void shouldCheckCustomAssertion() {
        AssertParameterized<MyClazz,AssertableMock> assertParameterized =
                new AssertParameterized<>();

        AssertionMock<AssertableMock> assertion = new AssertionMock<>();

        assertParameterized.addAssertion(assertion);

        AssertableMock alphaAssertable = AssertableMock.create("alpha_title");
        AssertableMock betaAssertable = AssertableMock.create("beta_title");

        PHolder<PHolder<AssertableMock>> root =
                PHolder.build("root")
                    .leaf("alpha", alphaAssertable)
                    .leaf("beta", betaAssertable)
                        .<PHolder<AssertableMock>>getRoot();

        assertParameterized.check(root);

        assertEquals(2, assertion.getList().size());
        assertEquals("alpha_title", assertion.getList().get(0));
        assertEquals("beta_title", assertion.getList().get(1));
    }

}
