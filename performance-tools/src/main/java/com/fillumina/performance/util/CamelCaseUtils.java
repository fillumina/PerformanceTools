package com.fillumina.performance.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class CamelCaseUtils {

    private static final Pattern CAMEL_PATTERN =
            Pattern.compile("(([0-9]+)|([A-Z]+[a-z]*)|([a-z]+))");
    private static final Pattern WORD_PATTERN =
            Pattern.compile("[A-Z][a-z]+");


    public static String sentenceToCamelCase(final String sentence) {
        final String[] words = sentence.split(" ");
        final StringBuilder builder = new StringBuilder();
        for (String word: words) {
            builder.append(capitalizeFirstLetter(word.toLowerCase()));
        }
        return builder.toString();
    }

    public static String camelCaseToSentence(final String camelCaseString) {
        final List<String> list = camelCaseToStringList(camelCaseString);
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(capitalizeFirstLetter(s));
        }
        return builder.toString();
    }

    public static List<String> camelCaseToStringList(final String camelCaseString) {
        final List<String> tokens = new ArrayList<>();
        final Matcher matcher = CAMEL_PATTERN.matcher(camelCaseString);
        while(matcher.find()) {
            final String found = matcher.group();
            final Matcher word = WORD_PATTERN.matcher(found);

            int index;
            if (word.find() && (index = word.start()) != 0) {
                final String acronym = found.substring(0, index);
                tokens.add(toLowerCaseIfSingleCharacter(acronym));
                tokens.add(smallerizeFirstLetter(found.substring(index)));
            } else {
                tokens.add(toLowerCaseIfNotAcronym(found));
            }
        }
        return tokens;
    }

    public static String toLowerCaseIfSingleCharacter(final String word) {
        if (word.length() == 1) {
            return word.toLowerCase();
        }
        return word;
    }

    public static String toLowerCaseIfNotAcronym(final String word) {
        if (!word.toUpperCase().equals(word)) {
            return word.toLowerCase();
        }
        return word;
    }

    public static String capitalizeFirstLetter(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        } else if (str.length() == 1) {
            return str.toUpperCase();
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String smallerizeFirstLetter(final String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
