package com.fillumina.performance.util;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CamelCaseUtilsTest {

    @Test
    public void shouldConvertSentenceToCamelCase() {
        assertEquals("ThisIsASentence",
                CamelCaseUtils.sentenceToCamelCase("this is a sentence"));
    }

    @Test
    public void shouldConvertCamelCaseToSentence() {
        assertEquals("This Is A Sentence",
                CamelCaseUtils.camelCaseToSentence("ThisIsASentence"));
    }

    @Test
    public void shouldConvertCamelCaseToStringList() {
        List<String> list =
                CamelCaseUtils.camelCaseToStringList("ThisIsASentence");
        assertEquals(Arrays.asList("this", "is", "a", "sentence"), list);
    }

    @Test
    public void shouldLowerCaseIfSingleCharacter() {
        assertEquals("a", CamelCaseUtils.toLowerCaseIfSingleCharacter("A"));
        assertEquals("ABC", CamelCaseUtils.toLowerCaseIfSingleCharacter("ABC"));
    }

    @Test
    public void testToLowerCaseIfNotAcronym() {
        assertEquals("ABC", CamelCaseUtils.toLowerCaseIfNotAcronym("ABC"));
        assertEquals("abc", CamelCaseUtils.toLowerCaseIfNotAcronym("AbC"));
    }

    @Test
    public void shouldCapitalizeFirstLetter() {
        assertEquals("Hello world!",
                CamelCaseUtils.capitalizeFirstLetter("hello world!"));
    }

    @Test
    public void testSmallerizeFirstLetter() {
        assertEquals("aBC", CamelCaseUtils.smallerizeFirstLetter("ABC"));
    }

}
